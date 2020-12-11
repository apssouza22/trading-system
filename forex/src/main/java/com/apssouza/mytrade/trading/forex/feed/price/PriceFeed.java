package com.apssouza.mytrade.trading.forex.feed.price;

import com.apssouza.mytrade.feed.api.PriceDto;

import java.time.LocalDateTime;
import java.util.Map;

public interface PriceFeed {
    Map<String, PriceDto> getPriceSymbolMapped(LocalDateTime time);
}
