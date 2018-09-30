package com.apssouza.mytrade.trading.misc.loop;

import com.apssouza.mytrade.feed.price.PriceDto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoopEvent {

    private final LocalDateTime time;
    private final Map<String, PriceDto> priceDtoMap;

    public LoopEvent(LocalDateTime time, Map<String, PriceDto> priceDtoMap) {
        this.time = time;
        this.priceDtoMap = priceDtoMap;
    }

    public LocalDateTime getTime() {
        return this.time;
    }

    public Map<String, PriceDto> getPrice() {
        return priceDtoMap;
    }
}
