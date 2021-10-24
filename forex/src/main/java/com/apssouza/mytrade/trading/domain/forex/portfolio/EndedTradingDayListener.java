package com.apssouza.mytrade.trading.domain.forex.portfolio;

import com.apssouza.mytrade.trading.domain.forex.common.Event;
import com.apssouza.mytrade.trading.domain.forex.common.observerinfra.Observer;
import com.apssouza.mytrade.trading.domain.forex.risk.stopordercreation.StopOrderFilledEvent;
import com.apssouza.mytrade.trading.domain.forex.session.EndedTradingDayEvent;

import java.util.List;
import java.util.logging.Logger;

class EndedTradingDayListener implements Observer {

    private final PortfolioService portfolioService;

    private static Logger log = Logger.getLogger(EndedTradingDayListener.class.getSimpleName());

    public EndedTradingDayListener(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @Override
    public void update(final Event e) {
        if (!(e instanceof EndedTradingDayEvent event)) {
            return;
        }

        List<Position> positions = portfolioService.closeAllPositions(Position.ExitReason.END_OF_DAY, event);
        log.info(positions.size() + " positions closed");
    }

}
