package com.apssouza.mytrade.trading.domain.forex.broker;

import com.apssouza.mytrade.feed.api.PriceDto;
import com.apssouza.mytrade.trading.domain.forex.order.OrderDto;
import com.apssouza.mytrade.trading.domain.forex.risk.stoporder.StopOrderDto;
import com.apssouza.mytrade.trading.domain.forex.portfolio.FilledOrderDto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation to execute orders against the Broker called interactive broker
 */
class InteractiveBrokerOrderExecution implements OrderExecution {

    private final String brokerHost;
    private final String brokerPort;
    private final String brokerClientId;

    public InteractiveBrokerOrderExecution(String brokerHost, String brokerPort, String brokerClientId) {
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
    public Map<String, FilledOrderDto> getPortfolio() {
        return new HashMap<>();
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

    @Override
    public Map<String, FilledOrderDto> getPositions() {
        return null;
    }

}
