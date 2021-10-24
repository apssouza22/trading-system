package com.apssouza.mytrade.trading.domain.forex.order;

import com.apssouza.mytrade.trading.domain.forex.common.Event;
import com.apssouza.mytrade.trading.domain.forex.common.observer.PropertyChangeEvent;
import com.apssouza.mytrade.trading.domain.forex.common.observer.PropertyChangeListener;
import com.apssouza.mytrade.trading.domain.forex.feed.signalfeed.SignalCreatedEvent;
import com.apssouza.mytrade.trading.domain.forex.risk.RiskManagementService;
import com.apssouza.mytrade.trading.domain.forex.session.EventNotifier;

import java.util.logging.Logger;

class SignalCreatedListener implements PropertyChangeListener {
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
    public void propertyChange(PropertyChangeEvent evt) {
        Event e = (Event) evt.getNewValue();
        if (!(e instanceof SignalCreatedEvent)) {
            return;
        }
        SignalCreatedEvent event = (SignalCreatedEvent) e;
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
