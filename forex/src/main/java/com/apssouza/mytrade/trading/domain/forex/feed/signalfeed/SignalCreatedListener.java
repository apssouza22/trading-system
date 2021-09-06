package com.apssouza.mytrade.trading.domain.forex.feed.signalfeed;

import com.apssouza.mytrade.trading.domain.forex.common.Event;
import com.apssouza.mytrade.trading.domain.forex.common.observer.PropertyChangeEvent;
import com.apssouza.mytrade.trading.domain.forex.common.observer.PropertyChangeListener;
import com.apssouza.mytrade.trading.domain.forex.order.OrderCreatedEvent;
import com.apssouza.mytrade.trading.domain.forex.order.OrderDto;
import com.apssouza.mytrade.trading.domain.forex.order.OrderHandler;
import com.apssouza.mytrade.trading.domain.forex.risk.RiskManagementHandler;
import com.apssouza.mytrade.trading.domain.forex.session.EventNotifier;

import java.util.logging.Logger;

class SignalCreatedListener implements PropertyChangeListener {
    private static Logger log = Logger.getLogger(SignalCreatedListener.class.getName());
    private final RiskManagementHandler riskManagementHandler;
    private final OrderHandler orderHandler;
    private final EventNotifier eventNotifier;

    public SignalCreatedListener(
            RiskManagementHandler riskManagementHandler,
            OrderHandler orderHandler,
            EventNotifier eventNotifier
    ) {
        this.riskManagementHandler = riskManagementHandler;
        this.orderHandler = orderHandler;
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
        if (riskManagementHandler.canCreateOrder(event)) {
            OrderDto order = this.orderHandler.createOrderFromSignal(event);
            eventNotifier.notify(new OrderCreatedEvent(
                    event.getTimestamp(),
                    event.getPrice(),
                    order
            ));
        }
    }
}
