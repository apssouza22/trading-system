package com.apssouza.mytrade.trading.forex.execution;

import com.apssouza.mytrade.feed.price.PriceDto;
import com.apssouza.mytrade.feed.price.PriceHandler;
import com.apssouza.mytrade.trading.forex.order.*;
import com.apssouza.mytrade.trading.forex.portfolio.FilledOrderDto;
import com.apssouza.mytrade.trading.forex.session.MultiPositionHandler;
import com.apssouza.mytrade.trading.misc.helper.config.Properties;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class SimulatedExecutionHandler implements ExecutionHandler {

    private static Logger log = Logger.getLogger(SimulatedExecutionHandler.class.getSimpleName());
    private final PriceHandler priceHandler;
    private List<StopOrderDto> stopLossOrders = new LinkedList<>();
    private List<StopOrderDto> limitOrders = new LinkedList<>();
    private LocalDateTime current_time;
    private Map<String, PriceDto> priceMap = new ConcurrentHashMap<>();
    private final StopOrderPriceMonitor stopOrderPriceMonitor;
    private Map<Integer, StopOrderDto> allStopOrders = new ConcurrentHashMap<>();
    private Map<String, FilledOrderDto> positions = new ConcurrentHashMap<>();
    private static AtomicInteger stopOrderId = new AtomicInteger();


    public SimulatedExecutionHandler(PriceHandler priceHandler) {
        this.priceHandler = priceHandler;
        stopOrderPriceMonitor = new StopOrderPriceMonitor(allStopOrders, priceMap);
    }

    public Map<String, FilledOrderDto> getPortfolio() {
        return this.positions;
    }

    public void setCurrentTime(LocalDateTime current_time) {
        this.current_time = current_time;
    }

    public void setPriceMap(Map<String, PriceDto> priceMap) {
        this.priceMap = priceMap;
    }

    public void closeAllPositions() {
        return;
    }

    public FilledOrderDto executeOrder(OrderDto order) {
        String currency_pair = order.getSymbol();
        String position_identifier = MultiPositionHandler.getIdentifierFromOrder(order);
        OrderAction action = order.getAction();
        int quantity = order.getQuantity();

        PriceDto fill_price = priceMap.get(currency_pair);
        BigDecimal close_price = fill_price.getClose();

        FilledOrderDto filled_order = new FilledOrderDto(
                this.current_time,
                order.getSymbol(),
                action,
                quantity,
                close_price,
                position_identifier,
                order.getId()
        );
        log.info("Executing order " + filled_order.toString());

        if (this.positions.containsKey(order.getSymbol())) {
            if (Properties.trading_multi_position_enabled || Properties.trading_position_edit_enabled) {
                this.handleMultiPairPositionPortfolio(action, order.getSymbol(), quantity);
            } else {
                this.positions.remove(order.getSymbol());
            }
        } else {
            this.positions.put(order.getSymbol(), filled_order);
        }
        return filled_order;
    }

    public void handleMultiPairPositionPortfolio(OrderAction action, String currency_pair, Integer quantity) {
        FilledOrderDto filledOrderDto = this.positions.get(currency_pair);
        if (action.equals(OrderAction.SELL) && filledOrderDto.equals(OrderAction.BUY)) {
            if (quantity == filledOrderDto.getQuantity()) {
                this.positions.remove(currency_pair);
            } else {
                filledOrderDto = new FilledOrderDto(filledOrderDto.getQuantity() - quantity, filledOrderDto);
            }

        } else if (action.equals(OrderAction.BUY) && filledOrderDto.getAction().equals(OrderAction.SELL)) {

            if (quantity == filledOrderDto.getQuantity()) {
                this.positions.get(currency_pair);
            } else {
                filledOrderDto = new FilledOrderDto(filledOrderDto.getQuantity() - quantity, filledOrderDto);
            }

        } else if (action.equals(OrderAction.BUY) && filledOrderDto.getAction().equals(OrderAction.SELL)) {
            filledOrderDto = new FilledOrderDto(filledOrderDto.getQuantity() + quantity, filledOrderDto);
        } else if (action.equals(OrderAction.SELL) && filledOrderDto.getAction().equals(OrderAction.BUY)) {
            filledOrderDto = new FilledOrderDto(filledOrderDto.getQuantity() + quantity, filledOrderDto);
        }
        this.positions.put(currency_pair, filledOrderDto);
    }

    public Map<Integer, StopOrderDto> getStopLossOrders() {
        this.processStopOrderWithPrices();
        return this.allStopOrders;
    }

    public Map<Integer, StopOrderDto> getLimitOrders() {
        return Collections.emptyMap();
    }

    public StopOrderDto placeStopOrder(StopOrderDto stop) {
        int id = SimulatedExecutionHandler.stopOrderId.incrementAndGet();

        StopOrderStatus status = StopOrderStatus.SUBMITTED;
        StopOrderDto stopOrderDto = new StopOrderDto(
                stop.getType(),
                id,
                status,
                stop.getAction(),
                stop.getPrice(),
                null,
                stop.getSymbol(),
                stop.getQuantity(),
                stop.getIdentifier()
        );
        this.allStopOrders.put(id, stopOrderDto);
        return stopOrderDto;
    }

    public Integer cancelOpenStopOrders() {
        this.processStopOrderWithPrices();
        Integer count = 0;
        for (Map.Entry<Integer, StopOrderDto> entry : this.allStopOrders.entrySet()) {
            StopOrderDto stop_loss = this.allStopOrders.get(entry.getKey());
            if (stop_loss.getStatus().equals(StopOrderStatus.SUBMITTED)) {
                this.allStopOrders.put(entry.getKey(), new StopOrderDto(StopOrderStatus.CANCELLED, stop_loss));
                count += 1;
            }
        }
        return count;
    }

    public Integer cancelOpenLimitOrders() {
        return 0;
    }

    public void deleteStopOrders() {
        this.allStopOrders = new ConcurrentHashMap<>();
    }

    public void processStopOrderWithPrices() {
        List<String> filled_positions = new ArrayList<>();
        stopOrderPriceMonitor.getFilledOrders();
        for (String identifier : filled_positions) {
            changeLocalPosition(this.allStopOrders.get(identifier));
        }
    }

    public void changeLocalPosition(StopOrderDto stop_order) {
        if (!this.positions.containsKey(stop_order.getSymbol())) {
            addNewPosition(stop_order);
            return;
        }

        FilledOrderDto filledOrderDto = this.positions.get(stop_order.getSymbol());
        OrderAction action = stop_order.getAction();

        if (action.equals(OrderAction.SELL) && filledOrderDto.getAction().equals(OrderAction.BUY)) {
            filledOrderDto = handleOppositeDirection(stop_order, filledOrderDto);
        }

        if (action.equals(OrderAction.BUY) && filledOrderDto.getAction().equals(OrderAction.SELL)) {
            filledOrderDto = handleOppositeDirection(stop_order, filledOrderDto);
        }

        if (action.equals(OrderAction.BUY) && filledOrderDto.getAction().equals(OrderAction.BUY)) {
            filledOrderDto = handleSameDirection(stop_order, filledOrderDto);
        }

        if (action.equals(OrderAction.SELL) && filledOrderDto.getAction().equals(OrderAction.SELL)) {
            filledOrderDto = handleSameDirection(stop_order, filledOrderDto);
        }

        if (filledOrderDto.getQuantity() < stop_order.getQuantity()) {
            throw new RuntimeException("Position has less units than stop order. position){ " + filledOrderDto + " order){ " + stop_order);
        }

    }

    private FilledOrderDto handleSameDirection(StopOrderDto stop_order, FilledOrderDto filledOrderDto) {
        filledOrderDto = new FilledOrderDto(filledOrderDto.getQuantity() + stop_order.getQuantity(), filledOrderDto);
        return filledOrderDto;
    }

    private FilledOrderDto handleOppositeDirection(StopOrderDto stop_order, FilledOrderDto filledOrderDto) {
        if (filledOrderDto.getQuantity() == stop_order.getQuantity()) {
            this.positions.remove(stop_order.getSymbol());
        } else {
            filledOrderDto = new FilledOrderDto(filledOrderDto.getQuantity() - stop_order.getQuantity(), filledOrderDto);
        }
        return filledOrderDto;
    }

    private void addNewPosition(StopOrderDto stop_order) {
        FilledOrderDto filled_order = new FilledOrderDto(
                this.current_time,
                stop_order.getSymbol(),
                stop_order.getAction(),
                stop_order.getQuantity(),
                stop_order.getFilledPrice(),
                stop_order.getIdentifier(),
                stop_order.getId()
        );
        this.positions.put(stop_order.getSymbol(), filled_order);
    }
}
