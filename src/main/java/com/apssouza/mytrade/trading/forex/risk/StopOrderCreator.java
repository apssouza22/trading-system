package com.apssouza.mytrade.trading.forex.risk;

import com.apssouza.mytrade.trading.forex.order.StopOrderDto;
import com.apssouza.mytrade.trading.forex.portfolio.Position;
import com.apssouza.mytrade.trading.misc.loop.LoopEvent;

import java.util.Optional;

public interface StopOrderCreator {

    StopOrderDto getHardStopLoss(Position position);

    StopOrderDto getProfitStopOrder(Position position);

    Optional<StopOrderDto> getEntryStopOrder(Position position, LoopEvent event);

    Optional<StopOrderDto> getTrailingStopOrder(Position position, LoopEvent event);
}
