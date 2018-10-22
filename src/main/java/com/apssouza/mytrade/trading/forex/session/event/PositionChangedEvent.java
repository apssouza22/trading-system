package com.apssouza.mytrade.trading.forex.session.event;

import com.apssouza.mytrade.feed.price.PriceDto;
import com.apssouza.mytrade.trading.forex.portfolio.Position;

import java.time.LocalDateTime;
import java.util.Map;

public class PositionChangedEvent extends AbstractEvent {

    private final Position ps;

    public PositionChangedEvent(EventType type, LocalDateTime timestamp, Map<String, PriceDto> priceDtoMap, Position ps) {
        super(type, timestamp, priceDtoMap);
        this.ps = ps;
    }

    public Position getPosition() {
        return ps;
    }
}
