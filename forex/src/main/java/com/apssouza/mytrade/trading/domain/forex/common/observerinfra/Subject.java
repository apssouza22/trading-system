package com.apssouza.mytrade.trading.domain.forex.common.observerinfra;

import com.apssouza.mytrade.trading.domain.forex.common.events.Event;

public interface Subject
{
    void attach(Observer o);
    void detach(Observer o);
    void notify(Event e);
}
