package com.apssouza.mytrade.trading.domain.forex.common.observerinfra;

import com.apssouza.mytrade.trading.domain.forex.common.Event;

public interface Observer
{
    public void update(Event e);
}