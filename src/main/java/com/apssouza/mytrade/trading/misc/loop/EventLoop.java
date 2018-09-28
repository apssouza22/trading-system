package com.apssouza.mytrade.trading.misc.loop;


public interface EventLoop<T> {

    boolean hasNext();

    T next();

    void abort(boolean bool);

    void sleep();
}
