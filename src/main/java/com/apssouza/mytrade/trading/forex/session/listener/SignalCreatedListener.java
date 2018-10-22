package com.apssouza.mytrade.trading.forex.session.listener;

import com.apssouza.mytrade.trading.forex.order.OrderDto;
import com.apssouza.mytrade.trading.forex.order.OrderHandler;
import com.apssouza.mytrade.trading.forex.risk.RiskManagementHandler;
import com.apssouza.mytrade.trading.forex.session.HistoryBookHandler;
import com.apssouza.mytrade.trading.forex.session.event.Event;
import com.apssouza.mytrade.trading.forex.session.event.EventType;
import com.apssouza.mytrade.trading.forex.session.event.OrderCreatedEvent;
import com.apssouza.mytrade.trading.forex.session.event.SignalCreatedEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

public class SignalCreatedListener implements PropertyChangeListener {
    private static Logger log = Logger.getLogger(SignalCreatedListener.class.getName());
    private final RiskManagementHandler riskManagementHandler;
    private final OrderHandler orderHandler;
    private final BlockingQueue<Event> eventQueue;
    private final HistoryBookHandler historyHandler;

    public SignalCreatedListener(
            RiskManagementHandler riskManagementHandler,
            OrderHandler orderHandler,
            BlockingQueue<Event> eventQueue,
            HistoryBookHandler historyHandler) {
        this.riskManagementHandler = riskManagementHandler;
        this.orderHandler = orderHandler;
        this.eventQueue = eventQueue;
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
        this.historyHandler.addSignal(event.getSignal());
        if (riskManagementHandler.canCreateOrder(event)) {
            OrderDto order = this.orderHandler.createOrderFromSignal(event);
            try {
                this.eventQueue.put(new OrderCreatedEvent(
                        EventType.ORDER_CREATED,
                        event.getTimestamp(),
                        event.getPrice(),
                        order
                ));
            } catch (InterruptedException ev) {
                throw new RuntimeException(ev);
            }
        }
    }
}
