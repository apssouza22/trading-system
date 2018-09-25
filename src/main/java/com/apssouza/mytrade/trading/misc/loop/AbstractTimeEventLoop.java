package com.apssouza.mytrade.trading.misc.loop;

import com.apssouza.mytrade.trading.misc.helper.time.Interval;

import java.time.LocalDateTime;

public abstract class AbstractTimeEventLoop implements TimeEventLoop {


    protected boolean aborted = false;



    public abstract boolean hasNext();

    public abstract void sleep();

    public abstract LocalDateTime getNext();


    public void abort(boolean abort) {
        this.aborted = abort;
    }


}
