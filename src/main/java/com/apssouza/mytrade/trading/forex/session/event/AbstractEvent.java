package com.apssouza.mytrade.trading.forex.session.event;

import com.apssouza.mytrade.feed.price.PriceDto;
import com.apssouza.mytrade.trading.forex.common.Symbol;

import java.time.LocalDateTime;
import java.util.Map;

public class AbstractEvent implements Event {

    protected final EventType type;
    protected final LocalDateTime timestamp;
    private final Map<String, PriceDto> priceDtoMap;

    public AbstractEvent(EventType type, LocalDateTime timestamp, Map<String, PriceDto> priceDtoMap) {
        this.type = type;
        this.timestamp = timestamp;
        this.priceDtoMap = priceDtoMap;
    }

    @Override
    public EventType getType() {
        return type;
    }

    @Override
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Map<String, PriceDto> getPrice() {
        return priceDtoMap;
    }
}
