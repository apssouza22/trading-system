package com.apssouza.mytrade.trading.domain.forex.order;

import com.apssouza.mytrade.trading.domain.forex.common.events.Event;
import com.apssouza.mytrade.trading.domain.forex.common.events.OrderCreatedEvent;
import com.apssouza.mytrade.trading.domain.forex.common.observerinfra.Observer;
import com.apssouza.mytrade.trading.domain.forex.common.events.SignalCreatedEvent;
import com.apssouza.mytrade.trading.domain.forex.risk.RiskManagementService;
import com.apssouza.mytrade.trading.domain.forex.common.observerinfra.EventNotifier;

import java.util.logging.Logger;

class SignalCreatedListener implements Observer {
    private static Logger log = Logger.getLogger(SignalCreatedListener.class.getName());
    private final RiskManagementService riskManagementService;
    private final OrderService orderService;
    private final EventNotifier eventNotifier;

    public SignalCreatedListener(
            RiskManagementService riskManagementService,
            OrderService orderService,
            EventNotifier eventNotifier
    ) {
        this.riskManagementService = riskManagementService;
        this.orderService = orderService;
        this.eventNotifier = eventNotifier;
    }


    @Override
    public void update(final Event e) {
        if (!(e instanceof SignalCreatedEvent event)) {
            return;
        }
        log.info("Processing  new signal...");
        OrderDto order = this.orderService.createOrderFromSignal(event);
        if (riskManagementService.canCreateOrder(order)) {
            this.orderService.persist(order);
            log.info("Created order: " + order);
            eventNotifier.notify(new OrderCreatedEvent(
                    event.getTimestamp(),
                    event.getPrice(),
                    order
            ));
        }
    }
}
