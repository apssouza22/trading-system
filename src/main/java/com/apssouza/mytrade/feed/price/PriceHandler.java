package com.apssouza.mytrade.feed.price;

import java.time.LocalDateTime;

public class PriceHandler {
    private final PriceDao priceDao;

    public PriceHandler(PriceDao priceDao) {
        this.priceDao = priceDao;
    }

    public PriceDto getClosestPrice(LocalDateTime time) {
        return null;
    }
}
