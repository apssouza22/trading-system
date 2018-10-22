package com.apssouza.mytrade.trading.misc.loop;

import com.apssouza.mytrade.feed.price.PriceHandler;
import com.apssouza.mytrade.trading.forex.session.event.EventType;
import com.apssouza.mytrade.trading.forex.session.event.LoopEvent;

import java.time.LocalDateTime;
import java.util.List;

public class RangeEventLoop extends AbstractEventLoop {

    protected int previous;
    private final int length;
    protected int current;
    private List<LocalDateTime> range;
    private final PriceHandler priceHandler;

    public RangeEventLoop(
            List<LocalDateTime> range,
            PriceHandler priceHandler
    ) {
        this.range = range;
        this.priceHandler = priceHandler;
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
    public LoopEvent next() {
        this.previous = this.current;
        this.current = this.current + 1;
        LocalDateTime time = this.range.get(this.current);
        return new LoopEvent(EventType.LOOP_FOUND_NEXT,time, this.priceHandler.getPriceSymbolMapped(time));
    }
}
