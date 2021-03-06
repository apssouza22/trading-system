package com.apssouza.mytrade.trading.adapters;

import com.apssouza.mytrade.feed.api.FeedModule;
import com.apssouza.mytrade.feed.api.PriceDto;
import com.apssouza.mytrade.trading.forex.feed.PriceFeed;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Price feed adapter is responsible for adapter the Feed Module api into the Forex system
 */
public class PriceFeedAdapter implements PriceFeed {

    private FeedModule feedModule;

    public PriceFeedAdapter(final FeedModule feedModule) {
        this.feedModule = feedModule;
    }

    @Override
    public Map<String, PriceDto> getPriceSymbolMapped(final LocalDateTime time) {
        return feedModule.getPriceSymbolMapped(time);
    }
}
