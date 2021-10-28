package com.apssouza.mytrade.trading.domain.forex.orderbook;

import com.apssouza.mytrade.trading.domain.forex.common.observerinfra.Observer;

import java.util.ArrayList;
import java.util.List;

public class OrderBookServiceFactory {

    public static OrderBookService create() {
        return new OrderBookServiceImpl(new TransactionsExporter());
    }

    public static List<Observer> createListeners(
            OrderBookService bookHandler
    ) {
        var listeners = new ArrayList<Observer>();
        listeners.add(new HistoryStopOrderFilledListener(bookHandler));
        listeners.add(new HistoryFilledOrderListener(bookHandler));
        listeners.add(new OrderCreatedListener(bookHandler));
        listeners.add(new SessionFinishedListener(bookHandler));
        listeners.add(new HistoryPortfolioChangedListener(bookHandler));
        return listeners;
    }
}
