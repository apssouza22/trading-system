package com.apssouza.mytrade.trading.misc.loop;

import java.time.LocalDateTime;
import java.util.List;

public class RangeEventLoop extends AbstractEventLoop {

    protected int previous;
    private final int length;
    protected int current;
    private List<LocalDateTime> range;

    public RangeEventLoop(
            List<LocalDateTime> range
    ) {
        this.range = range;
        this.length = this.range.size();
        this.current = -1;
    }


    @Override
    public boolean hasNext() {
        if (this.aborted)
            return false;
        if (this.range.isEmpty())
            return false;

        if (this.current + 1 < this.length)
            return true;
        return false;
    }

    @Override
    public void sleep() {

    }

    @Override
    public LocalDateTime next() {
        this.previous = this.current;
        this.current = this.current + 1;
        return this.range.get(this.current);
    }
}
