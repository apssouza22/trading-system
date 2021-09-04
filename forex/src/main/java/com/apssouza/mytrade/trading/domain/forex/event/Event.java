package com.apssouza.mytrade.trading.domain.forex.event;

import com.apssouza.mytrade.feed.api.PriceDto;

import java.time.LocalDateTime;
import java.util.Map;

public interface Event {

    EventType getType();

    LocalDateTime getTimestamp();

    Map<String, PriceDto> getPrice();

}
