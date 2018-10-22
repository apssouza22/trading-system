package com.apssouza.mytrade.trading.forex.portfolio;

import com.apssouza.mytrade.trading.forex.execution.ExecutionHandler;

public class ReconciliationHandler {
    private final Portfolio portfolio;
    private final ExecutionHandler executionHandler;

    public ReconciliationHandler(Portfolio portfolio, ExecutionHandler executionHandler) {

        this.portfolio = portfolio;
        this.executionHandler = executionHandler;
    }

    public void process() {

    }
}
