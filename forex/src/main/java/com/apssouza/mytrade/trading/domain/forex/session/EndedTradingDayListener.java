package com.apssouza.mytrade.trading.domain.forex.session;

import com.apssouza.mytrade.trading.domain.forex.common.observer.PropertyChangeEvent;
import com.apssouza.mytrade.trading.domain.forex.common.observer.PropertyChangeListener;
import com.apssouza.mytrade.trading.domain.forex.common.Event;
import com.apssouza.mytrade.trading.domain.forex.portfolio.PortfolioHandler;
import com.apssouza.mytrade.trading.domain.forex.portfolio.Position;

import java.util.List;
import java.util.logging.Logger;

class EndedTradingDayListener implements PropertyChangeListener {

    private final PortfolioHandler portfolioHandler;

    private static Logger log = Logger.getLogger(EndedTradingDayListener.class.getSimpleName());

    public EndedTradingDayListener(PortfolioHandler portfolioHandler) {
        this.portfolioHandler = portfolioHandler;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Event event = (Event) evt.getNewValue();
        if (!(event instanceof EndedTradingDayEvent)) {
            return;
        }

        EndedTradingDayEvent finishedEvent = (EndedTradingDayEvent) event;
        List<Position> positions = portfolioHandler.closeAllPositions(Position.ExitReason.END_OF_DAY, finishedEvent);
        log.info(positions.size() + " positions closed");
    }

}
