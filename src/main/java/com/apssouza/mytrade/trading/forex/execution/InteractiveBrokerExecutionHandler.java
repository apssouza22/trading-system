package com.apssouza.mytrade.trading.forex.execution;

import com.apssouza.mytrade.trading.forex.order.OrderDto;
import com.apssouza.mytrade.trading.forex.order.StopOrderDto;
import com.apssouza.mytrade.trading.forex.portfolio.FilledOrderDto;

import java.time.LocalDateTime;
import java.util.List;

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
        return null;
    }

    @Override
    public List<StopOrderDto> getLimitOrders() {
        return null;
    }

    @Override
    public FilledOrderDto executeOrder(OrderDto order) {
        return null;
    }
}
