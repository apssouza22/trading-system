package com.apssouza.mytrade.trading.forex.feed;

import com.apssouza.mytrade.feed.api.PriceDto;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Price feed API for the Forex system
 */
public interface PriceFeed {
    Map<String, PriceDto> getPriceSymbolMapped(LocalDateTime time);
}
