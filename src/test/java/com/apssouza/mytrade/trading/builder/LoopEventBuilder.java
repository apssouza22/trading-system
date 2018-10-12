package com.apssouza.mytrade.trading.builder;

import com.apssouza.mytrade.feed.price.PriceDto;
import com.apssouza.mytrade.trading.misc.loop.LoopEvent;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;

public class LoopEventBuilder {

    HashMap<String, PriceDto> priceMap = new HashMap<>();
    LocalDateTime time;

    public LoopEventBuilder createPriceMap(BigDecimal close) {
        if (time == null)
            time = LocalDateTime.MIN;
        PriceDto priceDto = new PriceDto(time, close, close, close, close, "AUDUSD");
        priceMap.put("AUDUSD", priceDto);
        return this;
    }

    public LoopEventBuilder setTime(LocalDateTime time) {
        this.time = time;
        return this;
    }

    public LoopEvent build() {
        if (time == null)
            time = LocalDateTime.MIN;
        return new LoopEvent(time, priceMap);
    }
}
