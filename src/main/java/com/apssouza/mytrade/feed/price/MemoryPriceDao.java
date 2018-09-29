package com.apssouza.mytrade.feed.price;

import java.time.LocalDateTime;

public class MemoryPriceDao implements PriceDao {

    private final PriceDao priceDao;

    public MemoryPriceDao(PriceDao priceDao) {
        this.priceDao = priceDao;
    }

    @Override
    public void loadData(LocalDateTime start, LocalDateTime end) {

    }
}
