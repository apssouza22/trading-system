package com.apssouza.mytrade.trading.domain.forex.order;

import com.apssouza.mytrade.trading.domain.forex.common.Event;
import com.apssouza.mytrade.trading.domain.forex.common.observer.PropertyChangeEvent;
import com.apssouza.mytrade.trading.domain.forex.common.observer.PropertyChangeListener;
import com.apssouza.mytrade.trading.domain.forex.broker.BrokerService;
import com.apssouza.mytrade.trading.domain.forex.portfolio.FilledOrderDto;
import com.apssouza.mytrade.trading.domain.forex.risk.RiskManagementService;
import com.apssouza.mytrade.trading.domain.forex.session.EventNotifier;
import static com.apssouza.mytrade.trading.domain.forex.order.OrderDto.OrderOrigin.EXITS;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

class OrderFoundListener implements PropertyChangeListener {

    private static Logger log = Logger.getLogger(OrderFoundListener.class.getSimpleName());
    private final BrokerService executionHandler;
    private final OrderService orderService;
    private final EventNotifier eventNotifier;
    private final RiskManagementService riskManagementService;

    public OrderFoundListener(
            BrokerService executionHandler,
            OrderService orderService,
            EventNotifier eventNotifier,
            RiskManagementService riskManagementService
    ) {

        this.executionHandler = executionHandler;
        this.orderService = orderService;
        this.eventNotifier = eventNotifier;
        this.riskManagementService = riskManagementService;
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
            if (!riskManagementService.canExecuteOrder(event, order, new ArrayList<>(), exitedPositions)) {
                orderService.updateOrderStatus(order.id(), OrderDto.OrderStatus.CANCELLED);
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
            orderService.updateOrderStatus(order.id(), OrderDto.OrderStatus.EXECUTED);
        } else {
            orderService.updateOrderStatus(order.id(), OrderDto.OrderStatus.FAILED);
        }
    }

}
