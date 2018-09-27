package com.apssouza.mytrade.trading.forex.portfolio;

import com.apssouza.mytrade.feed.price.PriceHandler;

public class StopOrderHandler {
    private final Portfolio portfolio;
    private final PriceHandler priceHandler;

    public StopOrderHandler(Portfolio portfolio, PriceHandler priceHandler) {

        this.portfolio = portfolio;
        this.priceHandler = priceHandler;
    }
}
