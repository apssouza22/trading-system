package com.apssouza.mytrade.trading.forex.session.event;

import com.apssouza.mytrade.feed.price.PriceDto;

import java.time.LocalDateTime;
import java.util.Map;

public class PriceUpdatedEvent extends AbstractEvent {

    public PriceUpdatedEvent(EventType type, LocalDateTime timestamp, Map<String, PriceDto> priceDtoMap) {
        super(type, timestamp, priceDtoMap);
    }
}
