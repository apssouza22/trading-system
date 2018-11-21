package com.apssouza.mytrade.trading.forex.session.listener;

import com.apssouza.mytrade.trading.forex.order.OrderDto;
import com.apssouza.mytrade.trading.forex.order.OrderHandler;
import com.apssouza.mytrade.trading.forex.risk.RiskManagementHandler;
import com.apssouza.mytrade.trading.forex.session.EventNotifier;
import com.apssouza.mytrade.trading.forex.session.HistoryBookHandler;
import com.apssouza.mytrade.trading.forex.session.event.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;

public class SignalCreatedListener implements PropertyChangeListener {
    private static Logger log = Logger.getLogger(SignalCreatedListener.class.getName());
    private final RiskManagementHandler riskManagementHandler;
    private final OrderHandler orderHandler;
    private final EventNotifier eventNotifier;
    private final HistoryBookHandler historyHandler;

    public SignalCreatedListener(
            RiskManagementHandler riskManagementHandler,
            OrderHandler orderHandler,
            EventNotifier eventNotifier,
            HistoryBookHandler historyHandler) {
        this.riskManagementHandler = riskManagementHandler;
        this.orderHandler = orderHandler;
        this.eventNotifier = eventNotifier;
        this.historyHandler = historyHandler;
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
                    EventType.ORDER_CREATED,
                    event.getTimestamp(),
                    event.getPrice(),
                    order
            ));
        }
    }
}
