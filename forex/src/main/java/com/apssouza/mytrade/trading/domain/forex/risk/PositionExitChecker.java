package com.apssouza.mytrade.trading.domain.forex.risk;

import com.apssouza.mytrade.common.time.MarketTimeHelper;
import com.apssouza.mytrade.feed.api.SignalDto;
import com.apssouza.mytrade.trading.domain.forex.order.OrderDto;
import com.apssouza.mytrade.trading.domain.forex.portfolio.PositionDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

class PositionExitChecker {
    private static Logger log = Logger.getLogger(PositionExitChecker.class.getSimpleName());

    public List<PositionDto> check(List<PositionDto> positions, List<SignalDto> signals) {
        if (positions.isEmpty()){
            return Collections.emptyList();
        }
        log.info("Processing exits...");
        List<PositionDto> exitedPositions = new ArrayList<>();
        for (PositionDto position  : positions) {
            PositionDto.ExitReason exit_reason = null;
            if (this.hasCounterSignal(position, signals)) {
                exit_reason = PositionDto.ExitReason.COUNTER_SIGNAL;
            }
            if (exit_reason != null) {
                log.info("Exiting position for(" + position.symbol() + " Reason " + exit_reason);
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
