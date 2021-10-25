package com.apssouza.mytrade.trading.domain.forex.common.events;

import com.apssouza.mytrade.feed.api.PriceDto;
import com.apssouza.mytrade.trading.domain.forex.common.events.Event;

import java.time.LocalDateTime;
import java.util.Map;

public class PriceChangedEvent implements Event {
    private final LocalDateTime time;
    private final Map<String, PriceDto> priceSymbolMapped;

    public PriceChangedEvent(LocalDateTime time, Map<String, PriceDto> priceSymbolMapped) {
        this.time = time;
        this.priceSymbolMapped = priceSymbolMapped;
    }

    @Override
    public LocalDateTime getTimestamp() {
        return time;
    }

    @Override
    public Map<String, PriceDto> getPrice() {
        return priceSymbolMapped;
    }
}
