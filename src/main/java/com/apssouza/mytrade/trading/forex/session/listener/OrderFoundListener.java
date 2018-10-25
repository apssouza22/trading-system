package com.apssouza.mytrade.trading.forex.session.listener;

import com.apssouza.mytrade.trading.forex.execution.ExecutionHandler;
import com.apssouza.mytrade.trading.forex.order.OrderDto;
import com.apssouza.mytrade.trading.forex.order.OrderHandler;
import com.apssouza.mytrade.trading.forex.order.OrderOrigin;
import com.apssouza.mytrade.trading.forex.order.OrderStatus;
import com.apssouza.mytrade.trading.forex.portfolio.FilledOrderDto;
import com.apssouza.mytrade.trading.forex.risk.RiskManagementHandler;
import com.apssouza.mytrade.trading.forex.session.HistoryBookHandler;
import com.apssouza.mytrade.trading.forex.session.event.Event;
import com.apssouza.mytrade.trading.forex.session.event.EventType;
import com.apssouza.mytrade.trading.forex.session.event.OrderFilledEvent;
import com.apssouza.mytrade.trading.forex.session.event.OrderFoundEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

public class OrderFoundListener implements PropertyChangeListener {

    private static Logger log = Logger.getLogger(OrderFoundListener.class.getSimpleName());
    private final ExecutionHandler executionHandler;
    private final HistoryBookHandler historyHandler;
    private final OrderHandler orderHandler;
    private final BlockingQueue<Event> eventQueue;
    private final RiskManagementHandler riskManagementHandler;

    public OrderFoundListener(
            ExecutionHandler executionHandler,
            HistoryBookHandler historyHandler,
            OrderHandler orderHandler,
            BlockingQueue<Event> eventQueue,
            RiskManagementHandler riskManagementHandler
    ) {

        this.executionHandler = executionHandler;
        this.historyHandler = historyHandler;
        this.orderHandler = orderHandler;
        this.eventQueue = eventQueue;
        this.riskManagementHandler = riskManagementHandler;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Event event = (Event) evt.getNewValue();
        if (!(event instanceof OrderFoundEvent)) {
            return;
        }
        OrderFoundEvent orderFoundEvent = (OrderFoundEvent) event;

        List<OrderDto> orders = orderFoundEvent.getOrders();
        if (orders.isEmpty()) {
            log.info("No orders");
            return;
        }

        log.info(orders.size() + " new orders");
        List<String> processedOrders = new ArrayList<>();
        List<String> exitedPositions = new ArrayList<>();
        for (OrderDto order : orders) {
            if (order.getOrigin() == OrderOrigin.STOP_ORDER) {
                exitedPositions.add(order.getSymbol());
            }
        }

        for (OrderDto order : orders) {
            if (!riskManagementHandler.canExecuteOrder(order, processedOrders, exitedPositions)) {
                continue;
            }
            this.historyHandler.addOrder(order);
            try {
                processNewOrder(processedOrders, order, orderFoundEvent);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void processNewOrder(List<String> processedOrders, OrderDto order, OrderFoundEvent event) throws InterruptedException {
        FilledOrderDto filledOrder = executionHandler.executeOrder(order);
        if (filledOrder != null) {
            this.eventQueue.put(new OrderFilledEvent(
                    EventType.ORDER_FILLED,
                    filledOrder.getTime(),
                    event.getPrice(),
                    filledOrder
            ));
            orderHandler.updateOrderStatus(order.getId(), OrderStatus.EXECUTED);
            processedOrders.add(order.getSymbol());
        } else {
            orderHandler.updateOrderStatus(order.getId(), OrderStatus.FAILED);
        }
    }

}
