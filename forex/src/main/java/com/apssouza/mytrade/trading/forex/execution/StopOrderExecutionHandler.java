package com.apssouza.mytrade.trading.forex.execution;

import com.apssouza.mytrade.feed.api.PriceDto;
import com.apssouza.mytrade.trading.forex.order.OrderAction;
import com.apssouza.mytrade.trading.forex.order.StopOrderDto;
import com.apssouza.mytrade.trading.forex.order.StopOrderStatus;
import com.apssouza.mytrade.trading.forex.portfolio.FilledOrderDto;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

class StopOrderExecutionHandler {
    private final Map<String, FilledOrderDto> positions;
    private final StopOrderPriceMonitor stopOrderPriceMonitor;
    private ConcurrentHashMap<Integer, StopOrderDto> allStopOrders = new ConcurrentHashMap<>();

    private static AtomicInteger stopOrderId = new AtomicInteger();

    public StopOrderDto placeStopOrder(StopOrderDto stop) {
        int id = StopOrderExecutionHandler.stopOrderId.incrementAndGet();

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

    public void closeAllPositions() {
        return;
    }

    public StopOrderExecutionHandler(Map<String, FilledOrderDto> positions) {
        this.positions = positions;
        this.stopOrderPriceMonitor = new StopOrderPriceMonitor();
    }

    public void deleteStopOrders() {
        this.allStopOrders = new ConcurrentHashMap<>();
    }

    public Integer cancelOpenStopOrders() {
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

    /**
     * Check if the stop orders has been filled. Change the position based in the result
     */
    public void processStopOrders(LocalDateTime currentTime, Map<String, PriceDto> priceMap) {
        Set<StopOrderDto> filled_positions = stopOrderPriceMonitor.getFilledOrders(priceMap, allStopOrders);
        for (StopOrderDto stop : filled_positions) {
            updateStopOrderStatus(stop, priceMap.get(stop.getSymbol()));
            changeLocalPosition(stop, currentTime);
        }
    }

    private void updateStopOrderStatus(StopOrderDto stopOrder, PriceDto priceDto) {
        stopOrder = new StopOrderDto(StopOrderStatus.FILLED, priceDto.close(), stopOrder);
        this.allStopOrders.put(stopOrder.getId(), stopOrder);
    }

    private void changeLocalPosition(StopOrderDto stop_order, LocalDateTime currentTime) {
        if (!this.positions.containsKey(stop_order.getSymbol())) {
            addNewPosition(stop_order, currentTime);
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
        positions.put(filledOrderDto.getIdentifier(), filledOrderDto);
        return filledOrderDto;
    }

    private FilledOrderDto handleOppositeDirection(StopOrderDto stop_order, FilledOrderDto filledOrderDto) {
        if (filledOrderDto.getQuantity() == stop_order.getQuantity()) {
            this.positions.remove(stop_order.getSymbol());
        } else {
            filledOrderDto = new FilledOrderDto(filledOrderDto.getQuantity() - stop_order.getQuantity(), filledOrderDto);
            this.positions.put(filledOrderDto.getIdentifier(), filledOrderDto);
        }
        return filledOrderDto;
    }

    private void addNewPosition(StopOrderDto stop_order, LocalDateTime currentTime) {
        FilledOrderDto filled_order = new FilledOrderDto(
                currentTime,
                stop_order.getSymbol(),
                stop_order.getAction(),
                stop_order.getQuantity(),
                stop_order.getFilledPrice(),
                stop_order.getIdentifier(),
                stop_order.getId()
        );
        this.positions.put(stop_order.getSymbol(), filled_order);
    }

    public Map<Integer, StopOrderDto> getStopOrders() {
        return this.allStopOrders;
    }
}
