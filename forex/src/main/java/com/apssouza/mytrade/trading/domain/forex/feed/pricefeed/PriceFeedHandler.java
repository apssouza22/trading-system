package com.apssouza.mytrade.trading.domain.forex.feed.pricefeed;

import com.apssouza.mytrade.feed.api.FeedModule;
import com.apssouza.mytrade.feed.api.PriceDto;
import com.apssouza.mytrade.trading.domain.forex.feed.FeedService;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * PriceFeedHandler is responsible for providing ticker prices
 */
public class PriceFeedHandler {

    private FeedService feedModule;

    public PriceFeedHandler(FeedService feedModule) {
        this.feedModule = feedModule;
    }

    public Map<String, PriceDto> getPriceSymbolMapped(LocalDateTime time) {
        return feedModule.getPriceSymbolMapped(time);
    }
}
