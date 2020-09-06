package com.apssouza.mytrade.trading.forex.session.event;

import com.apssouza.mytrade.feed.price.PriceDto;

import java.time.LocalDateTime;
import java.util.Map;

public interface Event {

    EventType getType();

    LocalDateTime getTimestamp();

    Map<String, PriceDto> getPrice();

}
