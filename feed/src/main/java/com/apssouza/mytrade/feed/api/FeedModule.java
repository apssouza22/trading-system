package com.apssouza.mytrade.feed.api;

import com.apssouza.mytrade.feed.domain.price.PriceHandler;
import com.apssouza.mytrade.feed.domain.signal.SignalHandler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class FeedModule {

    private final SignalHandler signalHandler;
    private final PriceHandler priceHandler;

    public FeedModule(SignalHandler signalHandler, PriceHandler priceHandler) {
        this.signalHandler = signalHandler;
        this.priceHandler = priceHandler;
    }

    public List<SignalDto> getSignal(String systemName, final LocalDateTime currentTime) {
        return signalHandler.getSignal(systemName, currentTime);
    }

    public Map<String, PriceDto> getPriceSymbolMapped(LocalDateTime time) {
        return priceHandler.getPriceSymbolMapped(time);
    }
}
