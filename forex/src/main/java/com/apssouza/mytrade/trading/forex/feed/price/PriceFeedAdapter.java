package com.apssouza.mytrade.trading.forex.feed.price;

import com.apssouza.mytrade.feed.FeedModule;
import com.apssouza.mytrade.feed.PriceDto;

import java.time.LocalDateTime;
import java.util.Map;

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
