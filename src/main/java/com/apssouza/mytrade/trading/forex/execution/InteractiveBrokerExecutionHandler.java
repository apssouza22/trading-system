package com.apssouza.mytrade.trading.forex.execution;

import com.apssouza.mytrade.feed.price.PriceDto;
import com.apssouza.mytrade.trading.forex.order.OrderDto;
import com.apssouza.mytrade.trading.forex.order.StopOrderDto;
import com.apssouza.mytrade.trading.forex.portfolio.FilledOrderDto;

import java.time.LocalDateTime;
import java.util.Map;

public class InteractiveBrokerExecutionHandler implements ExecutionHandler {

    private final String brokerHost;
    private final String brokerPort;
    private final String brokerClientId;

    public InteractiveBrokerExecutionHandler(String brokerHost, String brokerPort, String brokerClientId) {
        this.brokerHost = brokerHost;
        this.brokerPort = brokerPort;
        this.brokerClientId = brokerClientId;
    }

    @Override
    public void closeAllPositions() {

    }

    @Override
    public Integer cancelOpenLimitOrders() {
        return 0;
    }

    @Override
    public Integer cancelOpenStopOrders() {
        return 0;
    }

    @Override
    public void setCurrentTime(LocalDateTime currentTime) {

    }

    @Override
    public Map<Integer, StopOrderDto> getStopLossOrders() {
        return null;
    }

    @Override
    public Map<Integer, StopOrderDto> getLimitOrders() {
        return null;
    }

    @Override
    public FilledOrderDto executeOrder(OrderDto order) {
        return null;
    }

    @Override
    public void setPriceMap(Map<String, PriceDto> priceMap) {

    }

    @Override
    public void deleteStopOrders() {

    }

    @Override
    public StopOrderDto placeStopOrder(StopOrderDto stopLoss) {
        return null;
    }

    @Override
    public void processStopOrders() {

    }

}
