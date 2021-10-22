package com.apssouza.mytrade.trading.domain.forex.order;

import com.apssouza.mytrade.trading.domain.forex.common.Event;
import com.apssouza.mytrade.trading.domain.forex.common.observer.PropertyChangeEvent;
import com.apssouza.mytrade.trading.domain.forex.common.observer.PropertyChangeListener;
import com.apssouza.mytrade.trading.domain.forex.broker.BrokerHandler;
import com.apssouza.mytrade.trading.domain.forex.portfolio.FilledOrderDto;
import com.apssouza.mytrade.trading.domain.forex.risk.RiskManagementHandler;
import com.apssouza.mytrade.trading.domain.forex.session.EventNotifier;
import static com.apssouza.mytrade.trading.domain.forex.order.OrderDto.OrderOrigin.EXITS;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

class OrderFoundListener implements PropertyChangeListener {

    private static Logger log = Logger.getLogger(OrderFoundListener.class.getSimpleName());
    private final BrokerHandler executionHandler;
    private final OrderHandler orderHandler;
    private final EventNotifier eventNotifier;
    private final RiskManagementHandler riskManagementHandler;

    public OrderFoundListener(
            BrokerHandler executionHandler,
            OrderHandler orderHandler,
            EventNotifier eventNotifier,
            RiskManagementHandler riskManagementHandler
    ) {

        this.executionHandler = executionHandler;
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
        List<String> exitedPositions = new ArrayList<>();
        for (OrderDto order : orders) {
            if (order.origin() == EXITS) {
                exitedPositions.add(order.symbol());
            }
        }

        for (OrderDto order : orders) {
            if (!riskManagementHandler.canExecuteOrder(event, order, new ArrayList<>(), exitedPositions)) {
                orderHandler.updateOrderStatus(order.id(), OrderDto.OrderStatus.CANCELLED);
                continue;
            }
            processNewOrder(order, orderFoundEvent);
        }
    }

    private void processNewOrder(OrderDto order, OrderFoundEvent event) {
        FilledOrderDto filledOrder = executionHandler.executeOrder(order);
        if (filledOrder != null) {
            eventNotifier.notify(new OrderFilledEvent(
                    filledOrder.time(),
                    event.getPrice(),
                    filledOrder
            ));
            orderHandler.updateOrderStatus(order.id(), OrderDto.OrderStatus.EXECUTED);
        } else {
            orderHandler.updateOrderStatus(order.id(), OrderDto.OrderStatus.FAILED);
        }
    }

}
