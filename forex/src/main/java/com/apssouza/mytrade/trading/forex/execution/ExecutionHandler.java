package com.apssouza.mytrade.trading.forex.execution;

import com.apssouza.mytrade.feed.price.PriceDto;
import com.apssouza.mytrade.trading.forex.order.OrderDto;
import com.apssouza.mytrade.trading.forex.order.StopOrderDto;
import com.apssouza.mytrade.trading.forex.portfolio.FilledOrderDto;
import com.apssouza.mytrade.trading.forex.portfolio.Position;

import java.time.LocalDateTime;
import java.util.Map;

public interface ExecutionHandler {
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
