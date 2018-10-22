package com.apssouza.mytrade.trading.forex.session.listener;

import com.apssouza.mytrade.trading.forex.portfolio.ReconciliationHandler;
import com.apssouza.mytrade.trading.forex.session.event.Event;
import com.apssouza.mytrade.trading.forex.session.event.PortfolioChangedEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class PortfolioChangedListener implements PropertyChangeListener {
    private final ReconciliationHandler reconciliationHandler;

    public PortfolioChangedListener(ReconciliationHandler reconciliationHandler) {
        this.reconciliationHandler = reconciliationHandler;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Event e = (Event) evt.getNewValue();
        if (!(e instanceof PortfolioChangedEvent)) {
            return;
        }

        PortfolioChangedEvent event = (PortfolioChangedEvent) e;
        reconciliationHandler.process();
    }
}
