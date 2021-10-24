package com.apssouza.mytrade.trading.domain.forex.orderbook;

import com.apssouza.mytrade.trading.domain.forex.common.Event;
import com.apssouza.mytrade.trading.domain.forex.common.observer.PropertyChangeEvent;
import com.apssouza.mytrade.trading.domain.forex.common.observer.PropertyChangeListener;
import com.apssouza.mytrade.trading.domain.forex.portfolio.PortfolioChangedEvent;

class HistoryPortfolioChangedListener implements PropertyChangeListener {

    private final BookHistoryService bookHandler;

    public HistoryPortfolioChangedListener(BookHistoryService bookHandler) {
        this.bookHandler = bookHandler;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Event e = (Event) evt.getNewValue();
        if (!(e instanceof PortfolioChangedEvent)) {
            return;
        }
        bookHandler.addPosition(((PortfolioChangedEvent) e).getPosition());
    }
}
