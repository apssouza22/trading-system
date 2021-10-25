package com.apssouza.mytrade.trading.domain.forex.portfolio;

import com.apssouza.mytrade.trading.domain.forex.common.events.Event;
import com.apssouza.mytrade.trading.domain.forex.common.observerinfra.Observer;
import com.apssouza.mytrade.trading.domain.forex.common.events.EndedTradingDayEvent;

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

        List<PositionDto> positions = portfolioService.closeAllPositions(PositionDto.ExitReason.END_OF_DAY, event);
        log.info(positions.size() + " positions closed");
    }

}
