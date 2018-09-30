package com.apssouza.mytrade.trading.forex.execution;

import com.apssouza.mytrade.trading.forex.order.StopOrderDto;

import java.time.LocalDateTime;
import java.util.List;

public interface ExecutionHandler {
    void closeAllPositions();

    int cancelOpenLimitOrders();

    int cancelOpenStopOrders();

    void setCurrentTime(LocalDateTime currentTime);

    List<StopOrderDto> getStopLossOrders();

    List<StopOrderDto> getLimitOrders();
}
