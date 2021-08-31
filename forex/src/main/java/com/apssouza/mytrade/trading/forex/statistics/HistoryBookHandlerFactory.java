package com.apssouza.mytrade.trading.forex.statistics;

public class HistoryBookHandlerFactory {

    public static HistoryBookHandler create() {
        return new HistoryBookHandler(new TransactionsExporter());
    }
}
