package com.apssouza.mytrade.trading.domain.forex.session.event;

import com.apssouza.mytrade.feed.api.PriceDto;

import java.time.LocalDateTime;
import java.util.Map;

public class PriceChangedEvent implements Event {
    private final EventType type;
    private final LocalDateTime time;
    private final Map<String, PriceDto> priceSymbolMapped;

    public PriceChangedEvent(EventType type, LocalDateTime time, Map<String, PriceDto> priceSymbolMapped) {
        this.type = type;
        this.time = time;
        this.priceSymbolMapped = priceSymbolMapped;
    }

    @Override
    public EventType getType() {
        return type;
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
