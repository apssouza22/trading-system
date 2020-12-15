package com.apssouza.mytrade.trading.forex.execution;

import com.apssouza.mytrade.feed.api.PriceDto;
import com.apssouza.mytrade.trading.forex.order.OrderDto;
import com.apssouza.mytrade.trading.forex.order.StopOrderDto;
import com.apssouza.mytrade.trading.forex.portfolio.FilledOrderDto;

import java.time.LocalDateTime;
import java.util.Map;

public interface OrderExecution {
    void closeAllPositions();

    Integer cancelOpenLimitOrders();

    Integer cancelOpenStopOrders();

    void setCurrentTime(LocalDateTime currentTime);

    Map<Integer, StopOrderDto> getStopLossOrders();

    Map<Integer, StopOrderDto> getLimitOrders();

    FilledOrderDto executeOrder(OrderDto order);

    void setPriceMap(Map<String, PriceDto> priceMap);

    void deleteStopOrders();

    StopOrderDto placeStopOrder(StopOrderDto stopLoss);

    void processStopOrders();

    Map<String, FilledOrderDto>  getPositions();
}
