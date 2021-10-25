package com.apssouza.mytrade.trading.domain.forex.common.events;

import com.apssouza.mytrade.feed.api.PriceDto;
import com.apssouza.mytrade.trading.domain.forex.common.events.AbstractEvent;

import java.time.LocalDateTime;
import java.util.Map;

public class EndedTradingDayEvent extends AbstractEvent {

    public EndedTradingDayEvent(LocalDateTime time, Map<String, PriceDto> price) {
        super(time, price);
    }

}
