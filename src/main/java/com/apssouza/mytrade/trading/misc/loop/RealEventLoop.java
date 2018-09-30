package com.apssouza.mytrade.trading.misc.loop;

import com.apssouza.mytrade.feed.price.PriceHandler;
import com.apssouza.mytrade.trading.misc.helper.time.DateTimeHelper;
import com.apssouza.mytrade.trading.misc.helper.time.Interval;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAmount;

public class RealEventLoop extends AbstractEventLoop {

    private final LocalDateTime start;
    private final LocalDateTime end;
    private final TemporalAmount frequency;
    private final CurrentTimeCreator current_time_creator;
    private final PriceHandler priceHandler;
    private LocalDateTime previous;
    private LocalDateTime current;

    public RealEventLoop(
            LocalDateTime start,
            LocalDateTime end,
            TemporalAmount frequency,
            CurrentTimeCreator current_time_creator,
            PriceHandler priceHandler
    ) {
        this.start = start;
        this.end = end;
        this.frequency = frequency;
        this.current_time_creator = current_time_creator;
        this.priceHandler = priceHandler;
    }

    @Override
    public boolean hasNext() {
        if (this.aborted)
            return false;
        LocalDateTime now = this.current_time_creator.getNow();
        if (this.end.compareTo(now) >= 0 ){
            return true;
        }
        return false;
    }

    @Override
    public void sleep() {
        LocalDateTime next = this.current.plus(this.frequency);
        LocalDateTime now = this.current_time_creator.getNow();
        if (now.compareTo(next) < 0) {
            Interval interval = DateTimeHelper.calculate(now, next);
            try {
                Thread.sleep(Math.abs(interval.getMilliseconds()));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public LoopEvent next() {
        this.previous = this.current;
        this.current = this.current_time_creator.getNow();
        return new LoopEvent(this.current, this.priceHandler.getPriceSymbolMapped(this.current));
    }
}
