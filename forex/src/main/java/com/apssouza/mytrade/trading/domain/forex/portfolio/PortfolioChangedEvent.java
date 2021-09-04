package com.apssouza.mytrade.trading.domain.forex.portfolio;

import com.apssouza.mytrade.feed.api.PriceDto;
import com.apssouza.mytrade.trading.domain.forex.event.AbstractEvent;
import com.apssouza.mytrade.trading.domain.forex.event.EventType;
import com.apssouza.mytrade.trading.domain.forex.portfolio.Position;

import java.time.LocalDateTime;
import java.util.Map;

public class PortfolioChangedEvent extends AbstractEvent {
    private final Position position;

    public PortfolioChangedEvent(
            EventType type,
            LocalDateTime timestamp,
            Map<String, PriceDto> price,
            Position position
    ) {
        super(type, timestamp, price);
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }
}
