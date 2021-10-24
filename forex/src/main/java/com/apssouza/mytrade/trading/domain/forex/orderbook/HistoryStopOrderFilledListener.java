package com.apssouza.mytrade.trading.domain.forex.orderbook;

import com.apssouza.mytrade.trading.domain.forex.common.Event;
import com.apssouza.mytrade.trading.domain.forex.common.observer.PropertyChangeEvent;
import com.apssouza.mytrade.trading.domain.forex.common.observer.PropertyChangeListener;
import com.apssouza.mytrade.trading.domain.forex.portfolio.FilledOrderDto;
import com.apssouza.mytrade.trading.domain.forex.risk.stopordercreation.StopOrderDto;
import com.apssouza.mytrade.trading.domain.forex.risk.stopordercreation.StopOrderFilledEvent;

import java.time.LocalDateTime;

class HistoryStopOrderFilledListener implements PropertyChangeListener {

    private final BookHistoryService historyHandler;

    public HistoryStopOrderFilledListener(BookHistoryService historyHandler) {
        this.historyHandler = historyHandler;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        var event = (Event) evt.getNewValue();
        if (!(event instanceof StopOrderFilledEvent)) {
            return;
        }
        StopOrderFilledEvent orderFilledEvent = (StopOrderFilledEvent) event;
        StopOrderDto stopOrder = orderFilledEvent.getStopOrder();
        LocalDateTime time = orderFilledEvent.getTimestamp();

        this.historyHandler.addOrderFilled(new FilledOrderDto(
                time,
                stopOrder.symbol(),
                stopOrder.action(),
                stopOrder.quantity(),
                stopOrder.filledPrice(),
                "",
                stopOrder.id()
        ));
    }
}
