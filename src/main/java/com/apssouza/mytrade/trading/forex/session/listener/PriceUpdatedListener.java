package com.apssouza.mytrade.trading.forex.session.listener;

import com.apssouza.mytrade.trading.forex.execution.ExecutionHandler;
import com.apssouza.mytrade.trading.forex.portfolio.PortfolioHandler;
import com.apssouza.mytrade.trading.forex.session.event.Event;
import com.apssouza.mytrade.trading.forex.session.event.PriceUpdatedEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class PriceUpdatedListener implements PropertyChangeListener {

    private final ExecutionHandler executionHandler;
    private final PortfolioHandler portfolioHandler;

    public PriceUpdatedListener(
            ExecutionHandler executionHandler,
            PortfolioHandler portfolioHandler
    ) {
        this.executionHandler = executionHandler;
        this.portfolioHandler = portfolioHandler;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Event e = (Event) evt.getNewValue();
        if (!(e instanceof PriceUpdatedEvent)) {
            return;
        }

        PriceUpdatedEvent event = (PriceUpdatedEvent) e;

        this.executionHandler.setPriceMap(event.getPrice());
        this.portfolioHandler.updatePortfolioValue(event);

        try {
            this.portfolioHandler.stopOrderHandle(event);
        } catch (InterruptedException e1) {
            throw new RuntimeException(e1);
        }

    }
}
