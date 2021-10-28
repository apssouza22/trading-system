package com.apssouza.mytrade.trading.domain.forex.brokerintegration;

import com.apssouza.mytrade.feed.api.PriceDto;
import com.apssouza.mytrade.trading.domain.forex.order.OrderDto;
import com.apssouza.mytrade.trading.domain.forex.risk.stopordercreation.StopOrderDto;
import com.apssouza.mytrade.trading.domain.forex.portfolio.FilledOrderDto;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Execute orders. This the interface communication with Brokers
 */
public interface BrokerIntegrationService {
    /**
     * Close all open positions
     */
    void closeAllPositions();

    /**
     * Cancel open limit order
     *
     * @return number of closed orders
     */
    Integer cancelOpenLimitOrders();

    /**
     * Cancel open stop order
     *
     * @return number of closed orders
     */
    Integer cancelOpenStopOrders();

    /**
     * Set the current time ticker
     */
    void setCurrentTime(LocalDateTime currentTime);

    /**
     * Get a map of open stop loss orders
     *
     * @return a map of id order -> stop order
     */
    Map<Integer, StopOrderDto> getStopLossOrders();

    /**
     * Get a map of open stop limit orders
     *
     * @return a map of id order -> stop order
     */
    Map<Integer, StopOrderDto> getLimitOrders();

    /**
     * Execute a buy/sell order
     *
     * @return the filed order data
     */
    FilledOrderDto executeOrder(OrderDto order);

    /**
     * Set a currency pair price
     *
     * @param priceMap
     */
    void setPriceMap(Map<String, PriceDto> priceMap);

    /**
     * Delete stop orders
     */
    void deleteStopOrders();

    /**
     * Place the stop order to be executed when match the price
     *
     * @param stopLoss
     * @return stop order filled info
     */
    StopOrderDto placeStopOrder(StopOrderDto stopLoss);

    /**
     * Process placed stop orders
     */
    void processStopOrders();

    /**
     * Get open position in the broker
     *
     * @return return a list of open position
     */
    Map<String, FilledOrderDto> getPositions();

    Map<String, FilledOrderDto> getPortfolio();
}
