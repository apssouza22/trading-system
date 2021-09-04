package com.apssouza.mytrade.trading.domain.forex.common;

import com.apssouza.mytrade.feed.api.PriceDto;

import java.time.LocalDateTime;
import java.util.Map;

public interface Event {

    LocalDateTime getTimestamp();

    Map<String, PriceDto> getPrice();

}
