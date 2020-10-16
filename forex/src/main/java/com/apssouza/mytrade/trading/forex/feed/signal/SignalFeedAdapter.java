package com.apssouza.mytrade.trading.forex.feed.signal;

import com.apssouza.mytrade.feed.FeedModule;
import com.apssouza.mytrade.feed.SignalDto;

import java.time.LocalDateTime;
import java.util.List;

public class SignalFeedAdapter implements SignalFeed {

    private FeedModule feedModule;

    public SignalFeedAdapter(final FeedModule feedModule) {
        this.feedModule = feedModule;
    }

    @Override
    public List<SignalDto> getSignal(final String systemName, final LocalDateTime currentTime) {
        return feedModule.getSignal(systemName, currentTime);
    }
}
