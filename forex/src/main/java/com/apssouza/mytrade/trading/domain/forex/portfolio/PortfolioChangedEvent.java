package com.apssouza.mytrade.trading.domain.forex.portfolio;

import com.apssouza.mytrade.feed.api.PriceDto;
import com.apssouza.mytrade.trading.domain.forex.common.AbstractEvent;

import java.time.LocalDateTime;
import java.util.Map;

public class PortfolioChangedEvent extends AbstractEvent {
    private final PositionDto position;

    public PortfolioChangedEvent(
            LocalDateTime timestamp,
            Map<String, PriceDto> price,
            PositionDto position
    ) {
        super(timestamp, price);
        this.position = position;
    }

    public PositionDto getPosition() {
        return position;
    }
}
