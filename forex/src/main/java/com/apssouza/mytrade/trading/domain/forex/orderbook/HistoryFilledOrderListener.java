package com.apssouza.mytrade.trading.domain.forex.orderbook;

import com.apssouza.mytrade.trading.domain.forex.common.Event;
import com.apssouza.mytrade.trading.domain.forex.common.observer.PropertyChangeEvent;
import com.apssouza.mytrade.trading.domain.forex.common.observer.PropertyChangeListener;
import com.apssouza.mytrade.trading.domain.forex.order.OrderFilledEvent;
import com.apssouza.mytrade.trading.domain.forex.portfolio.FilledOrderDto;


class HistoryFilledOrderListener implements PropertyChangeListener {

    private final BookHistoryService historyHandler;

    public HistoryFilledOrderListener(BookHistoryService historyHandler) {
        this.historyHandler = historyHandler;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Event event = (Event) evt.getNewValue();
        if (!(event instanceof OrderFilledEvent)) {
            return;
        }

        var orderFilledEvent = (OrderFilledEvent) event;
        FilledOrderDto filledOrder = orderFilledEvent.getFilledOrder();
        this.historyHandler.addOrderFilled(filledOrder);
    }
}
