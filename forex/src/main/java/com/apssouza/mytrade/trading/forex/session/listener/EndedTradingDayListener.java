package com.apssouza.mytrade.trading.forex.session.listener;

import com.apssouza.mytrade.trading.forex.portfolio.ExitReason;
import com.apssouza.mytrade.trading.forex.portfolio.PortfolioHandler;
import com.apssouza.mytrade.trading.forex.portfolio.Position;
import com.apssouza.mytrade.trading.forex.session.event.EndedTradingDayEvent;
import com.apssouza.mytrade.trading.forex.session.event.Event;
import com.apssouza.mytrade.trading.forex.common.observer.PropertyChangeEvent;
import com.apssouza.mytrade.trading.forex.common.observer.PropertyChangeListener;

import java.util.List;
import java.util.logging.Logger;

public class EndedTradingDayListener implements PropertyChangeListener {

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
        List<Position> positions = portfolioHandler.closeAllPositions(ExitReason.END_OF_DAY, finishedEvent);
        log.info(positions.size() + " positions closed");
    }

}
