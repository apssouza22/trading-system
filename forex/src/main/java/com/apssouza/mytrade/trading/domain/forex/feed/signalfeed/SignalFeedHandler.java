package com.apssouza.mytrade.trading.domain.forex.feed.signalfeed;

import com.apssouza.mytrade.feed.api.FeedModule;
import com.apssouza.mytrade.feed.api.SignalDto;
import com.apssouza.mytrade.trading.domain.forex.feed.FeedService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * SignalFeedHandler is responsible for handle trading signals
 */
public class SignalFeedHandler {

    private FeedService feedModule;

    public SignalFeedHandler(FeedService feedModule) {
        this.feedModule = feedModule;
    }

    public List<SignalDto> getSignal(String systemName, final LocalDateTime currentTime) {
        return feedModule.getSignal(systemName, currentTime);
    }
}
