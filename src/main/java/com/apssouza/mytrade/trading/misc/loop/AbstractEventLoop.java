package com.apssouza.mytrade.trading.misc.loop;

import java.time.LocalDateTime;

public abstract class AbstractEventLoop implements EventLoop {

    protected boolean aborted = false;

    public abstract boolean hasNext();

    public abstract void sleep();

    public abstract LoopEvent next();

    public void abort(boolean abort) {
        this.aborted = abort;
    }

}
