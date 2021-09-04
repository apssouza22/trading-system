package com.apssouza.mytrade.trading.domain.forex.order;

import com.apssouza.mytrade.trading.domain.forex.common.observer.PropertyChangeEvent;
import com.apssouza.mytrade.trading.domain.forex.common.observer.PropertyChangeListener;
import com.apssouza.mytrade.trading.domain.forex.event.Event;
import com.apssouza.mytrade.trading.domain.forex.event.EventType;
import com.apssouza.mytrade.trading.domain.forex.execution.OrderExecution;
import com.apssouza.mytrade.trading.domain.forex.portfolio.FilledOrderDto;
import com.apssouza.mytrade.trading.domain.forex.risk.RiskManagementHandler;
import com.apssouza.mytrade.trading.domain.forex.session.EventNotifier;
import com.apssouza.mytrade.trading.domain.forex.statistics.HistoryBookHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

class OrderFoundListener implements PropertyChangeListener {

    private static Logger log = Logger.getLogger(OrderFoundListener.class.getSimpleName());
    private final OrderExecution executionHandler;
    private final HistoryBookHandler historyHandler;
    private final OrderHandler orderHandler;
    private final EventNotifier eventNotifier;
    private final RiskManagementHandler riskManagementHandler;

    public OrderFoundListener(
            OrderExecution executionHandler,
            HistoryBookHandler historyHandler,
            OrderHandler orderHandler,
            EventNotifier eventNotifier,
            RiskManagementHandler riskManagementHandler
    ) {

        this.executionHandler = executionHandler;
        this.historyHandler = historyHandler;
        this.orderHandler = orderHandler;
        this.eventNotifier = eventNotifier;
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
            if (order.getOrigin() == OrderDto.OrderOrigin.EXITS) {
                exitedPositions.add(order.getSymbol());
            }
        }

        for (OrderDto order : orders) {
            if (!riskManagementHandler.canExecuteOrder(event, order, processedOrders, exitedPositions)) {
                orderHandler.updateOrderStatus(order.getId(), OrderDto.OrderStatus.CANCELLED);
                continue;
            }
            this.historyHandler.addOrder(order);
            processNewOrder(processedOrders, order, orderFoundEvent);
        }
    }

    private void processNewOrder(List<String> processedOrders, OrderDto order, OrderFoundEvent event) {
        FilledOrderDto filledOrder = executionHandler.executeOrder(order);
        if (filledOrder != null) {
            eventNotifier.notify(new OrderFilledEvent(
                    EventType.ORDER_FILLED,
                    filledOrder.getTime(),
                    event.getPrice(),
                    filledOrder
            ));
            orderHandler.updateOrderStatus(order.getId(), OrderDto.OrderStatus.EXECUTED);
            processedOrders.add(order.getSymbol());
        } else {
            orderHandler.updateOrderStatus(order.getId(), OrderDto.OrderStatus.FAILED);
        }
    }

}
