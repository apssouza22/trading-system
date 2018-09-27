package com.apssouza.mytrade.trading.forex.execution;

import com.apssouza.mytrade.feed.price.PriceHandler;

public class SimulatedExecutionHandler implements ExecutionHandler{

    private final PriceHandler priceHandler;

    public SimulatedExecutionHandler(PriceHandler priceHandler) {
        this.priceHandler = priceHandler;
    }

    @Override
    public void closeAllPositions() {

    }

    @Override
    public void cancelOpenLimitOrders() {

    }
}
