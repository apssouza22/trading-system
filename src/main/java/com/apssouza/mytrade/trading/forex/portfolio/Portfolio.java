package com.apssouza.mytrade.trading.forex.portfolio;

import com.apssouza.mytrade.feed.price.PriceHandler;

import java.math.BigDecimal;

public class Portfolio {
    private final PriceHandler priceHandler;
    private final BigDecimal equity;

    public Portfolio(PriceHandler priceHandler, BigDecimal equity) {
        this.priceHandler = priceHandler;
        this.equity = equity;
    }
}
