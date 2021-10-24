package com.apssouza.mytrade.trading.domain.forex.broker;

import com.apssouza.mytrade.feed.api.PriceDto;
import com.apssouza.mytrade.trading.domain.forex.order.OrderDto;
import com.apssouza.mytrade.trading.domain.forex.risk.stopordercreation.StopOrderDto;
import com.apssouza.mytrade.trading.domain.forex.portfolio.FilledOrderDto;
import com.apssouza.mytrade.trading.domain.forex.session.MultiPositionHandler;
import com.apssouza.mytrade.trading.domain.forex.common.NumberHelper;
import com.apssouza.mytrade.trading.domain.forex.common.TradingParams;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Simulated order execution simulates interactions with a broker
 */
class SimulatedOrderExecution implements BrokerService {

    private static final Logger log = Logger.getLogger(SimulatedOrderExecution.class.getSimpleName());
    private final MultiPositionPerCPairHandler multiPositionPerCPairHandler;
    private final StopOrderHelper stopOrderHandler;
    private Map<Integer, StopOrderDto> limitOrders = new LinkedHashMap<>();
    private LocalDateTime currentTime;
    private Map<String, PriceDto> priceMap = new LinkedHashMap<>();
    private Map<String, FilledOrderDto> positions = new ConcurrentHashMap<>();


    public SimulatedOrderExecution() {
        multiPositionPerCPairHandler = new MultiPositionPerCPairHandler(this.positions);
        stopOrderHandler = new StopOrderHelper(positions);
    }

    @Override
    public Map<String, FilledOrderDto> getPortfolio() {
        return this.positions;
    }

    @Override
    public void setCurrentTime(LocalDateTime current_time) {
        this.currentTime = current_time;
    }

    @Override
    public void setPriceMap(Map<String, PriceDto> priceMap) {
        this.priceMap = priceMap;
    }

    @Override
    public void closeAllPositions() {
        //TODO implement close all position
    }

    @Override
    public FilledOrderDto executeOrder(OrderDto order) {
        String currency_pair = order.symbol();
        String position_identifier = MultiPositionHandler.getIdentifierFromOrder(order);
        OrderDto.OrderAction action = order.action();
        int quantity = order.quantity();

        PriceDto fill_price = priceMap.get(currency_pair);
        BigDecimal close_price = NumberHelper.roundSymbolPrice(currency_pair, fill_price.close());

        FilledOrderDto filled_order = new FilledOrderDto(
                this.currentTime,
                order.symbol(),
                action,
                quantity,
                close_price,
                position_identifier,
                order.id()
        );
        log.info("Executing order " + filled_order);

        if (this.positions.containsKey(order.symbol())) {
            handleExistingPosition(order, action, quantity);
        } else {
            this.positions.put(order.symbol(), filled_order);
        }
        return filled_order;
    }

    @Override
    public Map<Integer, StopOrderDto> getStopLossOrders() {
        return stopOrderHandler.getStopOrders();
    }

    @Override
    public Map<Integer, StopOrderDto> getLimitOrders() {
        return limitOrders;
    }

    @Override
    public StopOrderDto placeStopOrder(StopOrderDto stop) {
        return stopOrderHandler.placeStopOrder(stop);
    }

    private void handleExistingPosition(OrderDto order, OrderDto.OrderAction action, int quantity) {
        if (TradingParams.trading_multi_position_enabled || TradingParams.trading_position_edit_enabled) {
            this.multiPositionPerCPairHandler.handle(action, order.symbol(), quantity);
        } else {
            FilledOrderDto filledOrderDto = this.positions.get(order.symbol());
            if (filledOrderDto.action().equals(order.action())) {
                throw new RuntimeException("trading_position_edit_enabled is not enabled");
            }
            this.positions.remove(order.symbol());
        }
    }

    @Override
    public Integer cancelOpenStopOrders() {
        return stopOrderHandler.cancelOpenStopOrders();
    }

    @Override
    public Integer cancelOpenLimitOrders() {
        return 0;
    }

    @Override
    public void deleteStopOrders() {
        stopOrderHandler.deleteStopOrders();
    }

    @Override
    public void processStopOrders() {
        stopOrderHandler.processStopOrders(this.currentTime, this.priceMap);
    }

    @Override
    public Map<String, FilledOrderDto> getPositions() {
        return positions;
    }

}
