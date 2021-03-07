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

/**
 * Handle different orders(Stop loss, limit profit, buy, sell)
 */
class StopOrderExecutionHandler {
    private final Map<String, FilledOrderDto> positions;
    private final StopOrderPriceMonitor stopOrderPriceMonitor;
    private ConcurrentHashMap<Integer, StopOrderDto> allStopOrders = new ConcurrentHashMap<>();

    private static AtomicInteger stopOrderId = new AtomicInteger();

    public StopOrderExecutionHandler(Map<String, FilledOrderDto> positions) {
        this.positions = positions;
        this.stopOrderPriceMonitor = new StopOrderPriceMonitor();
    }

    /**
     * Place stop orders to be executed when the price match the target
     *
     * @param stop
     * @return filled stop order info
     */
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

    /**
     * Delete all stop orders
     */
    public void deleteStopOrders() {
        this.allStopOrders = new ConcurrentHashMap<>();
    }

    /**
     * Cancel all open stop orders
     *
     * @return return the number of cancelled orders
     */
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

    private void changeLocalPosition(StopOrderDto stopOrderDto, LocalDateTime currentTime) {
        if (!this.positions.containsKey(stopOrderDto.getSymbol())) {
            addNewPosition(stopOrderDto, currentTime);
            return;
        }

        FilledOrderDto filledOrderDto = this.positions.get(stopOrderDto.getSymbol());
        OrderAction action = stopOrderDto.getAction();

        if (action.equals(OrderAction.SELL) && filledOrderDto.getAction().equals(OrderAction.BUY)) {
            filledOrderDto = handleOppositeDirection(stopOrderDto, filledOrderDto);
        }

        if (action.equals(OrderAction.BUY) && filledOrderDto.getAction().equals(OrderAction.SELL)) {
            filledOrderDto = handleOppositeDirection(stopOrderDto, filledOrderDto);
        }

        if (action.equals(OrderAction.BUY) && filledOrderDto.getAction().equals(OrderAction.BUY)) {
            filledOrderDto = handleSameDirection(stopOrderDto, filledOrderDto);
        }

        if (action.equals(OrderAction.SELL) && filledOrderDto.getAction().equals(OrderAction.SELL)) {
            filledOrderDto = handleSameDirection(stopOrderDto, filledOrderDto);
        }

        if (filledOrderDto.getQuantity() < stopOrderDto.getQuantity()) {
            throw new RuntimeException("Position has less units than stop order. position){ " + filledOrderDto + " order){ " + stopOrderDto);
        }

    }

    private FilledOrderDto handleSameDirection(StopOrderDto stopOrderDto, FilledOrderDto filledOrderDto) {
        filledOrderDto = new FilledOrderDto(filledOrderDto.getQuantity() + stopOrderDto.getQuantity(), filledOrderDto);
        positions.put(filledOrderDto.getIdentifier(), filledOrderDto);
        return filledOrderDto;
    }

    private FilledOrderDto handleOppositeDirection(StopOrderDto stopOrderDto, FilledOrderDto filledOrderDto) {
        if (filledOrderDto.getQuantity() == stopOrderDto.getQuantity()) {
            this.positions.remove(stopOrderDto.getSymbol());
        } else {
            filledOrderDto = new FilledOrderDto(filledOrderDto.getQuantity() - stopOrderDto.getQuantity(), filledOrderDto);
            this.positions.put(filledOrderDto.getIdentifier(), filledOrderDto);
        }
        return filledOrderDto;
    }

    private void addNewPosition(StopOrderDto stopOrderDto, LocalDateTime currentTime) {
        FilledOrderDto filledOrder = new FilledOrderDto(
                currentTime,
                stopOrderDto.getSymbol(),
                stopOrderDto.getAction(),
                stopOrderDto.getQuantity(),
                stopOrderDto.getFilledPrice(),
                stopOrderDto.getIdentifier(),
                stopOrderDto.getId()
        );
        this.positions.put(stopOrderDto.getSymbol(), filledOrder);
    }

    public Map<Integer, StopOrderDto> getStopOrders() {
        return this.allStopOrders;
    }
}
