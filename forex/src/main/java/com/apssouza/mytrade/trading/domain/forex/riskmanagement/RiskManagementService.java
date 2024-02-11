package com.apssouza.mytrade.trading.domain.forex.riskmanagement;

import com.apssouza.mytrade.feed.api.SignalDto;
import com.apssouza.mytrade.trading.domain.forex.common.events.Event;
import com.apssouza.mytrade.trading.domain.forex.order.OrderDto;
import com.apssouza.mytrade.trading.domain.forex.portfolio.PositionDto;
import com.apssouza.mytrade.trading.domain.forex.riskmanagement.stopordercreation.StopOrderDto;

import java.util.EnumMap;
import java.util.List;

public interface RiskManagementService {

    EnumMap<StopOrderDto.StopOrderType, StopOrderDto> createStopOrders(PositionDto position, Event event);
    List<PositionDto> getExitPositions(List<PositionDto> positions, List<SignalDto> signals);
    boolean canCreateOrder(OrderDto order, List<PositionDto> openPositions);
    boolean canExecuteOrder(Event event, OrderDto order, List<String> processedOrders, List<String> exitedPositions);
    int getPositionSize();
}
