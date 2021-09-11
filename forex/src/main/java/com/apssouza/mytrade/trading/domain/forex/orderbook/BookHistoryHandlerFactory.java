package com.apssouza.mytrade.trading.domain.forex.orderbook;

import com.apssouza.mytrade.trading.domain.forex.common.observer.PropertyChangeListener;
import com.apssouza.mytrade.trading.domain.forex.risk.RiskManagementHandler;

import java.util.ArrayList;
import java.util.List;

public class BookHistoryHandlerFactory {

    public static BookHistoryHandler create() {
        return new BookHistoryHandler(new TransactionsExporter());
    }

    public static List<PropertyChangeListener> createListeners(
            BookHistoryHandler bookHandler,
            RiskManagementHandler riskManager
    ) {
        var listeners = new ArrayList<PropertyChangeListener>();
        listeners.add(new HistoryStopOrderFilledListener(bookHandler));
        listeners.add(new HistoryFilledOrderListener(bookHandler));
        listeners.add(new HistoryOrderFoundListener(bookHandler, riskManager));
        listeners.add(new SessionFinishedListener(bookHandler));
        listeners.add(new HistoryPortfolioChangedListener(bookHandler));
        return listeners;
    }
}
