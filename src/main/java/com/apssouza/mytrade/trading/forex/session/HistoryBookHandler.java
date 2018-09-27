package com.apssouza.mytrade.trading.forex.session;

import com.apssouza.mytrade.feed.price.PriceHandler;
import com.apssouza.mytrade.trading.forex.portfolio.Portfolio;

public class HistoryBookHandler {
    private final Portfolio portfolio;
    private final PriceHandler priceHandler;

    public HistoryBookHandler(Portfolio portfolio, PriceHandler priceHandler) {
        this.portfolio = portfolio;
        this.priceHandler = priceHandler;
    }
}
