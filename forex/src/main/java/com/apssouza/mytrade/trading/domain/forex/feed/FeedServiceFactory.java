package com.apssouza.mytrade.trading.domain.forex.feed;

import com.apssouza.mytrade.feed.api.FeedModule;

public class FeedServiceFactory {

    public static FeedService create(final FeedModule feedModule) {
        return new TradingFeed(feedModule);
    }
}
