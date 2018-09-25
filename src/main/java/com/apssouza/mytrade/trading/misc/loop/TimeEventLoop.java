package com.apssouza.mytrade.trading.misc.loop;

import java.time.LocalDateTime;

public interface TimeEventLoop {

    boolean hasNext();

    LocalDateTime getNext();

    void abort(boolean bool);

    void sleep();
}
