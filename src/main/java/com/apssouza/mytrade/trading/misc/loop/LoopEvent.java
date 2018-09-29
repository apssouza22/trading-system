package com.apssouza.mytrade.trading.misc.loop;

import com.apssouza.mytrade.feed.price.PriceDto;

import java.time.LocalDateTime;

public class LoopEvent {

    private final LocalDateTime time;
    private final PriceDto price;

    public LoopEvent(LocalDateTime time, PriceDto price) {
        this.time = time;
        this.price = price;
    }

    public LocalDateTime getTime() {
        return this.time;
    }

    public PriceDto getPrice() {
        return price;
    }
}
