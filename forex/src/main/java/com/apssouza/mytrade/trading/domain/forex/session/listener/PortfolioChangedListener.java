package com.apssouza.mytrade.trading.domain.forex.session.listener;

import com.apssouza.mytrade.trading.domain.forex.common.observer.PropertyChangeEvent;
import com.apssouza.mytrade.trading.domain.forex.common.observer.PropertyChangeListener;
import com.apssouza.mytrade.trading.domain.forex.portfolio.PortfolioHandler;
import com.apssouza.mytrade.trading.domain.forex.session.event.Event;
import com.apssouza.mytrade.trading.domain.forex.session.event.PortfolioChangedEvent;

public class PortfolioChangedListener implements PropertyChangeListener {
    private final PortfolioHandler portfolioHandler;

    public PortfolioChangedListener(PortfolioHandler portfolioHandler) {
        this.portfolioHandler = portfolioHandler;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Event e = (Event) evt.getNewValue();
        if (!(e instanceof PortfolioChangedEvent)) {
            return;
        }
        portfolioHandler.processReconciliation(e);
    }
}
