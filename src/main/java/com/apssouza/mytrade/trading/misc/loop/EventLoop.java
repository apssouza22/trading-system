package com.apssouza.mytrade.trading.misc.loop;


import com.apssouza.mytrade.trading.forex.session.event.LoopEvent;

public interface EventLoop {

    boolean hasNext();

    LoopEvent next();

    void abort(boolean bool);

    void sleep();
}
