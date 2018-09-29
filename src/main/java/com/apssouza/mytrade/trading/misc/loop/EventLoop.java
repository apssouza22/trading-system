package com.apssouza.mytrade.trading.misc.loop;


public interface EventLoop {

    boolean hasNext();

    LoopEvent next();

    void abort(boolean bool);

    void sleep();
}
