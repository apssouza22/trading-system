package com.apssouza.mytrade.trading.forex.risk.stoporder;

import com.apssouza.mytrade.trading.forex.order.StopOrderDto;
import com.apssouza.mytrade.trading.forex.portfolio.Position;
import com.apssouza.mytrade.trading.forex.portfolio.PositionType;
import com.apssouza.mytrade.trading.misc.loop.LoopEvent;

import java.util.Optional;

public interface StopOrderCreator {

    void createContext(PositionType type);

    StopOrderDto getHardStopLoss(Position position);

    StopOrderDto getProfitStopOrder(Position position);

    Optional<StopOrderDto> getEntryStopOrder(Position position, LoopEvent event);

    Optional<StopOrderDto> getTrailingStopOrder(Position position, LoopEvent event);
}
