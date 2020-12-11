package com.apssouza.mytrade.trading.builder;

import com.apssouza.mytrade.feed.api.PriceDto;
import com.apssouza.mytrade.trading.forex.session.event.EventType;
import com.apssouza.mytrade.trading.forex.session.event.PriceChangedEvent;

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

    public PriceChangedEvent build() {
        if (time == null)
            time = LocalDateTime.MIN;
        return new PriceChangedEvent(EventType.PRICE_CHANGED, time, priceMap);
    }
}
