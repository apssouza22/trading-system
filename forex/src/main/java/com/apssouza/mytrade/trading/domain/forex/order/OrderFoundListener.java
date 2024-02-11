package com.apssouza.mytrade.trading.domain.forex.order;

import com.apssouza.mytrade.trading.domain.forex.brokerintegration.BrokerIntegrationService;
import com.apssouza.mytrade.trading.domain.forex.common.events.Event;
import com.apssouza.mytrade.trading.domain.forex.common.events.OrderFilledEvent;
import com.apssouza.mytrade.trading.domain.forex.common.events.OrderFoundEvent;
import com.apssouza.mytrade.trading.domain.forex.common.observerinfra.Observer;
import com.apssouza.mytrade.trading.domain.forex.portfolio.FilledOrderDto;
import com.apssouza.mytrade.trading.domain.forex.riskmanagement.RiskManagementService;
import com.apssouza.mytrade.trading.domain.forex.common.observerinfra.EventNotifier;
import static com.apssouza.mytrade.trading.domain.forex.order.OrderDto.OrderOrigin.EXITS;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

class OrderFoundListener implements Observer {

    private static Logger log = Logger.getLogger(OrderFoundListener.class.getSimpleName());
    private final BrokerIntegrationService executionHandler;
    private final OrderService orderService;
    private final EventNotifier eventNotifier;
    private final RiskManagementService riskManagementService;

    public OrderFoundListener(
            BrokerIntegrationService executionHandler,
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
    public void update(final Event e) {
        if (!(e instanceof OrderFoundEvent event)) {
            return;
        }

        List<OrderDto> orders = event.getOrders();
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
            if (!riskManagementService.canExecuteOrder(e, order, new ArrayList<>(), exitedPositions)) {
                orderService.updateOrderStatus(order.id(), OrderDto.OrderStatus.CANCELLED);
                continue;
            }
            processNewOrder(order, event);
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
