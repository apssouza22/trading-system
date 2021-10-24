package com.apssouza.mytrade.trading.domain.forex.common.observerinfra;

import com.apssouza.mytrade.trading.domain.forex.common.Event;

public interface Subject
{
    public void attach(Observer o);
    public void detach(Observer o);
    public void notify(Event e);
}
