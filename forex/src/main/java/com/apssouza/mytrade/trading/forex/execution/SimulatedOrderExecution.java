package com.apssouza.mytrade.trading.forex.execution;

import com.apssouza.mytrade.feed.api.PriceDto;
import com.apssouza.mytrade.trading.forex.order.OrderDto;
import com.apssouza.mytrade.trading.forex.order.StopOrderDto;
import com.apssouza.mytrade.trading.forex.portfolio.FilledOrderDto;
import com.apssouza.mytrade.trading.forex.session.MultiPositionHandler;
import com.apssouza.mytrade.trading.forex.common.NumberHelper;
import com.apssouza.mytrade.trading.forex.common.TradingParams;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Simulated order execution simulates interactions with a broker
 */
class SimulatedOrderExecution implements OrderExecution {

    private static final Logger log = Logger.getLogger(SimulatedOrderExecution.class.getSimpleName());
    private final MultiPositionPerCPairHandler multiPositionPerCPairHandler;
    private final StopOrderExecutionHandler stopOrderHandler;
    private Map<Integer, StopOrderDto> limitOrders = new LinkedHashMap<>();
    private LocalDateTime currentTime;
    private Map<String, PriceDto> priceMap = new LinkedHashMap<>();
    private Map<String, FilledOrderDto> positions = new ConcurrentHashMap<>();


    public SimulatedOrderExecution() {
        multiPositionPerCPairHandler = new MultiPositionPerCPairHandler(this.positions);
        stopOrderHandler = new StopOrderExecutionHandler(positions);
    }

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
        String currency_pair = order.getSymbol();
        String position_identifier = MultiPositionHandler.getIdentifierFromOrder(order);
        OrderDto.OrderAction action = order.getAction();
        int quantity = order.getQuantity();

        PriceDto fill_price = priceMap.get(currency_pair);
        BigDecimal close_price = NumberHelper.roundSymbolPrice(currency_pair, fill_price.close());

        FilledOrderDto filled_order = new FilledOrderDto(
                this.currentTime,
                order.getSymbol(),
                action,
                quantity,
                close_price,
                position_identifier,
                order.getId()
        );
        log.info("Executing order " + filled_order.toString());

        if (this.positions.containsKey(order.getSymbol())) {
            handleExistingPosition(order, action, quantity);
        } else {
            this.positions.put(order.getSymbol(), filled_order);
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
            this.multiPositionPerCPairHandler.handle(action, order.getSymbol(), quantity);
        } else {
            FilledOrderDto filledOrderDto = this.positions.get(order.getSymbol());
            if (filledOrderDto.getAction().equals(order.getAction())) {
                throw new RuntimeException("trading_position_edit_enabled is not enabled");
            }
            this.positions.remove(order.getSymbol());
        }
    }

    public Integer cancelOpenStopOrders() {
        return stopOrderHandler.cancelOpenStopOrders();
    }

    public Integer cancelOpenLimitOrders() {
        return 0;
    }

    public void deleteStopOrders() {
        stopOrderHandler.deleteStopOrders();
    }

    public void processStopOrders() {
        stopOrderHandler.processStopOrders(this.currentTime, this.priceMap);
    }

    @Override
    public Map<String, FilledOrderDto> getPositions() {
        return positions;
    }

}
