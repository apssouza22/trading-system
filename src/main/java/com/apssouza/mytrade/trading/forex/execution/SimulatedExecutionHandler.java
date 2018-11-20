package com.apssouza.mytrade.trading.forex.execution;

import com.apssouza.mytrade.feed.price.PriceDto;
import com.apssouza.mytrade.trading.forex.order.OrderAction;
import com.apssouza.mytrade.trading.forex.order.OrderDto;
import com.apssouza.mytrade.trading.forex.order.StopOrderDto;
import com.apssouza.mytrade.trading.forex.portfolio.FilledOrderDto;
import com.apssouza.mytrade.trading.forex.session.MultiPositionHandler;
import com.apssouza.mytrade.trading.misc.helper.config.Properties;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class SimulatedExecutionHandler implements ExecutionHandler {

    private static Logger log = Logger.getLogger(SimulatedExecutionHandler.class.getSimpleName());
    private final MultiPositionPerCPairHandler multiPositionPerCPairHandler;
    private final StopOrderHandler stopOrderHandler;
    private Map<Integer, StopOrderDto> limitOrders = new LinkedHashMap<>();
    private LocalDateTime currentTime;
    private Map<String, PriceDto> priceMap = new LinkedHashMap<>();
    private Map<String, FilledOrderDto> positions = new ConcurrentHashMap<>();


    public SimulatedExecutionHandler() {
        multiPositionPerCPairHandler = new MultiPositionPerCPairHandler(this.positions);
        stopOrderHandler = new StopOrderHandler(positions);
    }

    public Map<String, FilledOrderDto> getPortfolio() {
        return this.positions;
    }

    public void setCurrentTime(LocalDateTime current_time) {
        this.currentTime = current_time;
    }

    public void setPriceMap(Map<String, PriceDto> priceMap) {
        this.priceMap = priceMap;
    }

    public void closeAllPositions() {
        stopOrderHandler.closeAllPositions();
    }

    public FilledOrderDto executeOrder(OrderDto order) {
        String currency_pair = order.getSymbol();
        String position_identifier = MultiPositionHandler.getIdentifierFromOrder(order);
        OrderAction action = order.getAction();
        int quantity = order.getQuantity();

        PriceDto fill_price = priceMap.get(currency_pair);
        BigDecimal close_price = fill_price.getClose();

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

    private void handleExistingPosition(OrderDto order, OrderAction action, int quantity) {
        if (Properties.trading_multi_position_enabled || Properties.trading_position_edit_enabled) {
            this.multiPositionPerCPairHandler.handle(action, order.getSymbol(), quantity);
        } else {
            FilledOrderDto filledOrderDto = this.positions.get(order.getSymbol());
            if (filledOrderDto.getAction().equals(order.getAction())) {
                throw new RuntimeException("trading_position_edit_enabled is not enabled");
            }
            this.positions.remove(order.getSymbol());
        }
    }

    public Map<Integer, StopOrderDto> getStopLossOrders() {
        return stopOrderHandler.getStopOrders();
    }

    public Map<Integer, StopOrderDto> getLimitOrders() {
        return limitOrders;
    }

    public StopOrderDto placeStopOrder(StopOrderDto stop) {
        return stopOrderHandler.placeStopOrder(stop);
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

}
