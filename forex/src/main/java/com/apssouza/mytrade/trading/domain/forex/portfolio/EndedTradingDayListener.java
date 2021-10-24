package com.apssouza.mytrade.trading.domain.forex.portfolio;

import com.apssouza.mytrade.trading.domain.forex.common.observer.PropertyChangeEvent;
import com.apssouza.mytrade.trading.domain.forex.common.observer.PropertyChangeListener;
import com.apssouza.mytrade.trading.domain.forex.common.Event;
import com.apssouza.mytrade.trading.domain.forex.session.EndedTradingDayEvent;

import java.util.List;
import java.util.logging.Logger;

class EndedTradingDayListener implements PropertyChangeListener {

    private final PortfolioService portfolioService;

    private static Logger log = Logger.getLogger(EndedTradingDayListener.class.getSimpleName());

    public EndedTradingDayListener(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Event event = (Event) evt.getNewValue();
        if (!(event instanceof EndedTradingDayEvent)) {
            return;
        }

        EndedTradingDayEvent finishedEvent = (EndedTradingDayEvent) event;
        List<Position> positions = portfolioService.closeAllPositions(Position.ExitReason.END_OF_DAY, finishedEvent);
        log.info(positions.size() + " positions closed");
    }

}
