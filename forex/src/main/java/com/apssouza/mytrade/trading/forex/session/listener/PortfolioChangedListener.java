package com.apssouza.mytrade.trading.forex.session.listener;

import com.apssouza.mytrade.trading.forex.portfolio.ExitReason;
import com.apssouza.mytrade.trading.forex.portfolio.PortfolioHandler;
import com.apssouza.mytrade.trading.forex.portfolio.ReconciliationHandler;
import com.apssouza.mytrade.trading.forex.session.event.Event;
import com.apssouza.mytrade.trading.forex.session.event.PortfolioChangedEvent;
import com.apssouza.mytrade.trading.forex.portfolio.ReconciliationException;
import com.apssouza.mytrade.trading.forex.common.observer.PropertyChangeEvent;
import com.apssouza.mytrade.trading.forex.common.observer.PropertyChangeListener;

public class PortfolioChangedListener implements PropertyChangeListener {
    private final ReconciliationHandler reconciliationHandler;
    private final PortfolioHandler portfolioHandler;

    public PortfolioChangedListener(ReconciliationHandler reconciliationHandler, PortfolioHandler portfolioHandler) {
        this.reconciliationHandler = reconciliationHandler;
        this.portfolioHandler = portfolioHandler;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Event e = (Event) evt.getNewValue();
        if (!(e instanceof PortfolioChangedEvent)) {
            return;
        }

        try {
            reconciliationHandler.process(e);
        } catch (ReconciliationException e1) {
            portfolioHandler.closeAllPositions(ExitReason.RECONCILIATION_FAILED, e);
        }
    }
}
