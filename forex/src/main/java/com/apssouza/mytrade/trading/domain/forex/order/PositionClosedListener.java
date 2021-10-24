package com.apssouza.mytrade.trading.domain.forex.order;

import com.apssouza.mytrade.trading.domain.forex.common.Event;
import com.apssouza.mytrade.trading.domain.forex.common.observerinfra.Observer;
import com.apssouza.mytrade.trading.domain.forex.feed.pricefeed.PriceChangedEvent;
import com.apssouza.mytrade.trading.domain.forex.portfolio.PositionClosedEvent;


class PositionClosedListener implements Observer {

    private final OrderService orderService;

    public PositionClosedListener(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public void update(final Event e) {
        if (!(e instanceof PositionClosedEvent event)) {
            return;
        }
        this.orderService.persist(event.getOrder());
    }

}
