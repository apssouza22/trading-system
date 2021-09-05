package com.apssouza.mytrade.trading.domain.forex.feed.pricefeed;

import com.apssouza.mytrade.feed.api.PriceDto;

import java.time.LocalDateTime;
import java.util.Map;


/**
 * Price feed stream API for the Forex system
 */
public interface PriceStream {

    void start(LocalDateTime start, LocalDateTime end);

    Map<String, PriceDto> getPriceSymbolMapped(LocalDateTime current);
}
