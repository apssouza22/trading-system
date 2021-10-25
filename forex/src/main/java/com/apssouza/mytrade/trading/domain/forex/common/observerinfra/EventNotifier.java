package com.apssouza.mytrade.trading.domain.forex.common.observerinfra;

import com.apssouza.mytrade.trading.domain.forex.common.events.Event;

import java.util.ArrayList;
import java.util.List;

public class EventNotifier implements Subject {
    private List<Observer> observers = new ArrayList<>();

    @Override
    public void attach(Observer o) {
        observers.add(o);
    }

    @Override
    public void detach(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notify(Event e) {
        for(Observer o: observers) {
            o.update(e);
        }
    }
}