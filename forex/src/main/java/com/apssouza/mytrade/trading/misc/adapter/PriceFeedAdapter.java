package com.apssouza.mytrade.trading.misc.adapter;

import com.apssouza.mytrade.feed.price.PriceDto;
import com.apssouza.mytrade.feed.price.PriceHandler;
import com.apssouza.mytrade.trading.forex.feed.price.PriceFeed;

import java.time.LocalDateTime;
import java.util.Map;

public class PriceFeedAdapter implements PriceFeed {
    private final PriceHandler priceHandler;

    public PriceFeedAdapter(final PriceHandler priceHandler) {
        this.priceHandler = priceHandler;
    }

    @Override
    public Map<String, PriceDto> getPriceSymbolMapped(final LocalDateTime time) {
        return priceHandler.getPriceSymbolMapped(time);
    }
}
