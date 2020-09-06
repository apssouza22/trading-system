package com.apssouza.mytrade.trading.forex.session.event;

import com.apssouza.mytrade.feed.price.PriceDto;

import java.time.LocalDateTime;
import java.util.Map;

public class EndedTradingDayEvent extends AbstractEvent {

    public EndedTradingDayEvent(EventType type, LocalDateTime time, Map<String, PriceDto> price) {
        super(type, time, price);
    }

}
