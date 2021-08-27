package com.apssouza.mytrade.trading.forex.session.listener;

import com.apssouza.mytrade.trading.forex.session.event.Event;
import com.apssouza.mytrade.trading.forex.session.event.SessionFinishedEvent;
import com.apssouza.mytrade.trading.forex.statistics.HistoryBookHandler;
import com.apssouza.mytrade.trading.forex.common.TradingParams;
import com.apssouza.mytrade.trading.forex.common.observer.PropertyChangeEvent;
import com.apssouza.mytrade.trading.forex.common.observer.PropertyChangeListener;

import java.io.IOException;

public class SessionFinishedListener implements PropertyChangeListener {

    private final HistoryBookHandler historyHandler;

    public SessionFinishedListener(HistoryBookHandler historyHandler) {
        this.historyHandler = historyHandler;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Event event = (Event) evt.getNewValue();
        if (!(event instanceof SessionFinishedEvent)) {
            return;
        }

        SessionFinishedEvent finishedEvent = (SessionFinishedEvent) event;
        try {
            historyHandler.export(TradingParams.transaction_path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Finished session");
    }

}
