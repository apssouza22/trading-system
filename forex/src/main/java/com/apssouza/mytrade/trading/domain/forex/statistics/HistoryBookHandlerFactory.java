package com.apssouza.mytrade.trading.domain.forex.statistics;

public class HistoryBookHandlerFactory {

    public static HistoryBookHandler create() {
        return new HistoryBookHandler(new TransactionsExporter());
    }
}
