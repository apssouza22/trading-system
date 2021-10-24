package com.apssouza.mytrade.trading.domain.forex.order;

import com.apssouza.mytrade.trading.domain.forex.common.Event;
import com.apssouza.mytrade.trading.domain.forex.common.observer.PropertyChangeEvent;
import com.apssouza.mytrade.trading.domain.forex.common.observer.PropertyChangeListener;
import com.apssouza.mytrade.trading.domain.forex.portfolio.PositionClosedEvent;


class PositionClosedListener implements PropertyChangeListener {

    private final OrderService orderService;

    public PositionClosedListener(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Event e = (Event) evt.getNewValue();
        if (!(e instanceof PositionClosedEvent)) {
            return;
        }

        PositionClosedEvent event = (PositionClosedEvent) e;
        this.orderService.persist(event.getOrder());
    }
}
