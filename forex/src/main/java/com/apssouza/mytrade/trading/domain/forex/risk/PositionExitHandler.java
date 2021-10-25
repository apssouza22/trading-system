package com.apssouza.mytrade.trading.domain.forex.risk;

import com.apssouza.mytrade.common.misc.helper.time.MarketTimeHelper;
import com.apssouza.mytrade.feed.api.SignalDto;
import com.apssouza.mytrade.trading.domain.forex.order.OrderDto;
import com.apssouza.mytrade.trading.domain.forex.portfolio.PortfolioModel;
import com.apssouza.mytrade.trading.domain.forex.portfolio.PositionDto;
import com.apssouza.mytrade.trading.domain.forex.common.events.PriceChangedEvent;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

class PositionExitHandler {
    private final PortfolioModel portfolio;
    private static Logger log = Logger.getLogger(PositionExitHandler.class.getSimpleName());

    public PositionExitHandler(PortfolioModel portfolio) {
        this.portfolio = portfolio;
    }

    public List<PositionDto> process(PriceChangedEvent event, List<SignalDto> signals) {
        if (this.portfolio.isEmpty()){
            return Collections.emptyList();
        }
        log.info("Processing exits...");
        List<PositionDto> exitedPositions = new ArrayList<>();
        for (PositionDto position  : this.portfolio.getPositions()) {
            PositionDto.ExitReason exit_reason = null;

            if (this.hasCounterSignal(position, signals)) {
                exit_reason = PositionDto.ExitReason.COUNTER_SIGNAL;
            }
            if (exit_reason != null) {
                log.info("Exiting position for(" + position.symbol() + " Reason " + exit_reason);
                portfolio.closePosition(position.identifier(), exit_reason);
                exitedPositions.add(position);
            }
        }
        return exitedPositions;
    }

    private boolean isEndOfDay(LocalDateTime time) {
        return MarketTimeHelper.isEOD(time);
    }

    private SignalDto getSignalBySymbol(String symbol, List<SignalDto> signals) {
        for (SignalDto signal : signals) {
            if (signal.symbol().equals(symbol)) {
                return signal;
            }
        }
        return null;
    }


    private boolean hasCounterSignal(PositionDto position, List<SignalDto> signals) {
        SignalDto signal = getSignalBySymbol(position.symbol(), signals);
        if (signal == null) {
            return false;
        }

        OrderDto.OrderAction exit_direction = null;
        if (position.positionType() == PositionDto.PositionType.LONG) {
            exit_direction = OrderDto.OrderAction.SELL;
        } else {
            exit_direction = OrderDto.OrderAction.BUY;
        }

        if (OrderDto.OrderAction.valueOf(signal.action().toUpperCase()) == exit_direction) {
            return true;
        }
        return false;
    }

}
