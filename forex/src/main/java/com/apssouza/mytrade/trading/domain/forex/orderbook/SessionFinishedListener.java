package com.apssouza.mytrade.trading.domain.forex.orderbook;

import com.apssouza.mytrade.trading.domain.forex.common.events.Event;
import com.apssouza.mytrade.trading.domain.forex.common.TradingParams;
import com.apssouza.mytrade.trading.domain.forex.common.observerinfra.Observer;
import com.apssouza.mytrade.trading.domain.forex.common.events.SessionFinishedEvent;

import java.io.IOException;

class SessionFinishedListener implements Observer {

    private final OrderBookService historyHandler;

    public SessionFinishedListener(OrderBookService historyHandler) {
        this.historyHandler = historyHandler;
    }

    @Override
    public void update(final Event e) {
        if (!(e instanceof SessionFinishedEvent event)) {
            return;
        }
        try {
            historyHandler.export(TradingParams.transaction_path);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        System.out.println("Finished session");
    }

}
