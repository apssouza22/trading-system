package com.apssouza.mytrade.trading.forex.session.listener;

import com.apssouza.mytrade.trading.forex.order.OrderAction;
import com.apssouza.mytrade.trading.forex.portfolio.*;
import com.apssouza.mytrade.trading.forex.session.HistoryBookHandler;
import com.apssouza.mytrade.trading.forex.session.event.*;
import com.apssouza.mytrade.trading.forex.statistics.TransactionState;
import com.apssouza.mytrade.trading.misc.helper.config.Properties;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.BlockingQueue;

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
        System.out.println("FInished session");
    }

}
