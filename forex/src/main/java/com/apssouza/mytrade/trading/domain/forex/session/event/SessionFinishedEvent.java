package com.apssouza.mytrade.trading.domain.forex.session.event;

import com.apssouza.mytrade.feed.api.PriceDto;

import java.time.LocalDateTime;
import java.util.Map;

public class SessionFinishedEvent implements Event {

    private final EventType type;
    private final LocalDateTime timestamp;
    private final Map<String, PriceDto> price;

    public SessionFinishedEvent(EventType type, LocalDateTime timestamp, Map<String, PriceDto> price) {

        this.type = type;
        this.timestamp = timestamp;
        this.price = price;
    }

    @Override
    public EventType getType() {
        return type;
    }

    @Override
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public Map<String, PriceDto> getPrice() {
        return price;
    }
}
