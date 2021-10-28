package com.apssouza.mytrade.trading.domain.forex.orderbook;

import com.apssouza.mytrade.trading.domain.forex.common.events.Event;
import com.apssouza.mytrade.trading.domain.forex.common.observerinfra.Observer;
import com.apssouza.mytrade.trading.domain.forex.common.events.OrderFilledEvent;
import com.apssouza.mytrade.trading.domain.forex.portfolio.FilledOrderDto;


class HistoryFilledOrderListener implements Observer {

    private final OrderBookService historyHandler;

    public HistoryFilledOrderListener(OrderBookService historyHandler) {
        this.historyHandler = historyHandler;
    }

    @Override
    public void update(final Event e) {
        if (!(e instanceof OrderFilledEvent event)) {
            return;
        }
        FilledOrderDto filledOrder = event.getFilledOrder();
        this.historyHandler.addOrderFilled(filledOrder);
    }
}
