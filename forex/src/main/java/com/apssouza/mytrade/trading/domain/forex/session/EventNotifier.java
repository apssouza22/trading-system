package com.apssouza.mytrade.trading.domain.forex.session;

import com.apssouza.mytrade.trading.domain.forex.common.Event;

import com.apssouza.mytrade.trading.domain.forex.common.observer.PropertyChangeListener;
import com.apssouza.mytrade.trading.domain.forex.common.observer.PropertyChangeSupport;

public class EventNotifier {
    private Event event;

    private PropertyChangeSupport support;

    public EventNotifier() {
        support = new PropertyChangeSupport(this);
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        support.removePropertyChangeListener(pcl);
    }

    public void notify(Event event) {
        support.firePropertyChange("event", this.event, event);
        this.event = event;
    }
}