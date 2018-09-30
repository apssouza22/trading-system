package com.apssouza.mytrade.trading.forex.execution;

import com.apssouza.mytrade.feed.price.PriceHandler;
import com.apssouza.mytrade.trading.forex.order.StopOrderDto;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

public class SimulatedExecutionHandler implements ExecutionHandler {

    private final PriceHandler priceHandler;
    private List<StopOrderDto> stopLossOrders = new LinkedList<>();
    private List<StopOrderDto> limitOrders = new LinkedList<>();

    public SimulatedExecutionHandler(PriceHandler priceHandler) {
        this.priceHandler = priceHandler;
    }

    @Override
    public void closeAllPositions() {

    }

    @Override
    public int cancelOpenLimitOrders() {
        return 0;
    }

    @Override
    public int cancelOpenStopOrders() {
        return 0;
    }

    @Override
    public void setCurrentTime(LocalDateTime currentTime) {

    }

    @Override
    public List<StopOrderDto> getStopLossOrders() {
        return this.stopLossOrders;
    }

    @Override
    public List<StopOrderDto> getLimitOrders() {
        return this.limitOrders;
    }
}
