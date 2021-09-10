package com.apssouza.mytrade.trading.domain.forex.feed;

import com.apssouza.mytrade.feed.api.FeedModule;
import com.apssouza.mytrade.feed.api.PriceDto;
import com.apssouza.mytrade.feed.api.SignalDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class TradingFeed implements FeedService {
    private FeedModule feed;

    public TradingFeed(FeedModule feed) {
        this.feed = feed;
    }

    @Override
    public List<SignalDto> getSignal(final String systemName, final LocalDateTime currentTime) {
        return feed.getSignal(systemName, currentTime);
    }

    @Override
    public Map<String, PriceDto> getPriceSymbolMapped(final LocalDateTime time) {
        return feed.getPriceSymbolMapped(time);
    }
}
