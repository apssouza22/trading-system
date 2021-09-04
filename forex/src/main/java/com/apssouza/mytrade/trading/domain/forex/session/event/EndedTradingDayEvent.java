package com.apssouza.mytrade.trading.domain.forex.session.event;

import com.apssouza.mytrade.feed.api.PriceDto;

import java.time.LocalDateTime;
import java.util.Map;

public class EndedTradingDayEvent extends AbstractEvent {

    public EndedTradingDayEvent(EventType type, LocalDateTime time, Map<String, PriceDto> price) {
        super(type, time, price);
    }

}
