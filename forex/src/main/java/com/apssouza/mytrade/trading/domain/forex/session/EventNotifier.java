package com.apssouza.mytrade.trading.domain.forex.session;

import com.apssouza.mytrade.trading.domain.forex.common.Event;

import com.apssouza.mytrade.trading.domain.forex.common.observerinfra.Observer;
import com.apssouza.mytrade.trading.domain.forex.common.observerinfra.Publisher;

public class EventNotifier {
    private Event event;

    private Publisher support;

    public EventNotifier() {
        support = new Publisher();
    }

    public void attach(Observer pcl) {
        support.attach(pcl);
    }

    public void detach(Observer pcl) {
        support.detach(pcl);
    }

    public void notify(Event event) {
        support.notify(event);
    }
}