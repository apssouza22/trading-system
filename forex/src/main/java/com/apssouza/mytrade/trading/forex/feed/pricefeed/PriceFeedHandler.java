package com.apssouza.mytrade.trading.forex.feed.pricefeed;

import com.apssouza.mytrade.feed.api.FeedModule;
import com.apssouza.mytrade.feed.api.PriceDto;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * PriceFeedHandler is responsible for providing ticker prices
 */
public class PriceFeedHandler {

    private FeedModule feedModule;

    public PriceFeedHandler(FeedModule feedModule) {
        this.feedModule = feedModule;
    }

    public Map<String, PriceDto> getPriceSymbolMapped(LocalDateTime time) {
        return feedModule.getPriceSymbolMapped(time);
    }
}
