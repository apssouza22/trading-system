package com.apssouza.mytrade.trading.domain.forex.orderbook;

import com.apssouza.mytrade.trading.domain.forex.common.events.Event;
import com.apssouza.mytrade.trading.domain.forex.common.observerinfra.Observer;
import com.apssouza.mytrade.trading.domain.forex.common.events.OrderCreatedEvent;

class OrderCreatedListener implements Observer {
    private final OrderBookService historyHandler;

    public OrderCreatedListener(OrderBookService historyHandler) {
        this.historyHandler = historyHandler;
    }

    @Override
    public void update(final Event e) {
        if (!(e instanceof OrderCreatedEvent event)) {
            return;
        }
        var order = event.getOrder();
        this.historyHandler.addOrder(order);

    }


}
