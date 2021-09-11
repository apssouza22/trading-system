package com.apssouza.mytrade.trading.domain.forex.orderbook;

import com.apssouza.mytrade.trading.domain.forex.common.Event;
import com.apssouza.mytrade.trading.domain.forex.common.observer.PropertyChangeEvent;
import com.apssouza.mytrade.trading.domain.forex.common.observer.PropertyChangeListener;
import com.apssouza.mytrade.trading.domain.forex.order.OrderCreatedEvent;
import com.apssouza.mytrade.trading.domain.forex.risk.RiskManagementHandler;

class OrderCreatedListener implements PropertyChangeListener {
    private final BookHistoryHandler historyHandler;

    public OrderCreatedListener(BookHistoryHandler historyHandler) {
        this.historyHandler = historyHandler;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        var event = (Event) evt.getNewValue();
        if (!(event instanceof OrderCreatedEvent)) {
            return;
        }
        var orderCreatedEvent = (OrderCreatedEvent) event;
        var order = orderCreatedEvent.getOrder();
        this.historyHandler.addOrder(order);

    }


}
