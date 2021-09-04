package com.apssouza.mytrade.trading.domain.forex.risk.stoporder;

import com.apssouza.mytrade.trading.domain.forex.portfolio.Position;
import com.apssouza.mytrade.trading.domain.forex.common.Event;

import java.util.Optional;

public interface StopOrderCreator {

    void createContext(Position.PositionType type);

    StopOrderDto getHardStopLoss(Position position);

    StopOrderDto getProfitStopOrder(Position position);

    Optional<StopOrderDto> getEntryStopOrder(Position position, Event event);

    Optional<StopOrderDto> getTrailingStopOrder(Position position, Event event);
}
