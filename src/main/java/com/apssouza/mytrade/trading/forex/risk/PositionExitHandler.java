package com.apssouza.mytrade.trading.forex.risk;

import com.apssouza.mytrade.feed.price.PriceHandler;
import com.apssouza.mytrade.feed.signal.SignalDto;
import com.apssouza.mytrade.trading.forex.order.OrderAction;
import com.apssouza.mytrade.trading.forex.portfolio.ExitReason;
import com.apssouza.mytrade.trading.forex.portfolio.Portfolio;
import com.apssouza.mytrade.trading.forex.portfolio.Position;
import com.apssouza.mytrade.trading.forex.portfolio.PositionType;
import com.apssouza.mytrade.trading.misc.helper.time.MarketTimeHelper;
import com.apssouza.mytrade.trading.forex.session.event.LoopEvent;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class PositionExitHandler {
    private final Portfolio portfolio;
    private final PriceHandler priceHandler;
    private static Logger log = Logger.getLogger(PositionExitHandler.class.getName());

    public PositionExitHandler(Portfolio portfolio, PriceHandler priceHandler) {
        this.portfolio = portfolio;
        this.priceHandler = priceHandler;
    }

    public List<Position> process(LoopEvent event, List<SignalDto> signals) {
        log.info("Processing exits...");
        List<Position> exitedPositions = new ArrayList<>();
        for (Map.Entry<String, Position> entry : this.portfolio.getPositions().entrySet()) {
            Position position = entry.getValue();
            ExitReason exit_reason = null;
            if (this.isEndOfDay(event.getTimestamp())) {
                exit_reason = ExitReason.END_OF_DAY;
            }
            if (this.hasCounterSignal(position, signals)) {
                exit_reason = ExitReason.COUNTER_SIGNAL;
            }
            if (exit_reason != null) {
                log.info("Exiting position for(" + position.getSymbol() + " Reason " + exit_reason);
                position.closePosition(exit_reason);
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
            if (signal.getSymbol().equals(symbol)) {
                return signal;
            }
        }
        return null;
    }


    private boolean hasCounterSignal(Position position, List<SignalDto> signals) {
        SignalDto signal = getSignalBySymbol(position.getSymbol(), signals);
        if (signal == null)
            return false;

        OrderAction exit_direction = null;
        if (position.getPositionType() == PositionType.LONG) {
            exit_direction = OrderAction.SELL;
        } else {
            exit_direction = OrderAction.BUY;
        }

        if (OrderAction.valueOf(signal.getAction().toUpperCase()) == exit_direction) {
            return true;
        }
        return false;
    }

}
