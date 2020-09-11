package com.apssouza.mytrade.trading.misc.adapter;

import com.apssouza.mytrade.feed.signal.SignalDto;
import com.apssouza.mytrade.feed.signal.SignalHandler;
import com.apssouza.mytrade.trading.forex.feed.price.SignalFeed;

import java.time.LocalDateTime;
import java.util.List;

public class SignalFeedAdapter implements SignalFeed {

    private final SignalHandler signalHandler;

    public SignalFeedAdapter(final SignalHandler signalHandler) {
        this.signalHandler = signalHandler;
    }

    @Override
    public List<SignalDto> getSignal(final String systemName, final LocalDateTime currentTime) {
        return signalHandler.getSignal(systemName, currentTime);
    }
}
