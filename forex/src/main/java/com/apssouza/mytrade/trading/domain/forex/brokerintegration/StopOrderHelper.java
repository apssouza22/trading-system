package com.apssouza.mytrade.trading.domain.forex.brokerintegration;

import com.apssouza.mytrade.feed.api.PriceDto;
import com.apssouza.mytrade.trading.domain.forex.order.OrderDto;
import com.apssouza.mytrade.trading.domain.forex.riskmanagement.stopordercreation.StopOrderDto;
import com.apssouza.mytrade.trading.domain.forex.portfolio.FilledOrderDto;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Handle different orders(Stop loss, limit profit, buy, sell)
 */
class StopOrderHelper {
    private final Map<String, FilledOrderDto> positions;
    private final StopOrderPriceMonitor stopOrderPriceMonitor;
    private ConcurrentHashMap<Integer, StopOrderDto> allStopOrders = new ConcurrentHashMap<>();

    private static AtomicInteger stopOrderId = new AtomicInteger();

    public StopOrderHelper(Map<String, FilledOrderDto> positions) {
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
        int id = StopOrderHelper.stopOrderId.incrementAndGet();

        StopOrderDto.StopOrderStatus status = StopOrderDto.StopOrderStatus.SUBMITTED;
        StopOrderDto stopOrderDto = new StopOrderDto(
                stop.type(),
                id,
                status,
                stop.action(),
                stop.price(),
                null,
                stop.symbol(),
                stop.quantity(),
                stop.identifier()
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
            if (stop_loss.status().equals(StopOrderDto.StopOrderStatus.SUBMITTED)) {
                this.allStopOrders.put(entry.getKey(), new StopOrderDto(StopOrderDto.StopOrderStatus.CANCELLED, stop_loss));
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
            updateStopOrderStatus(stop, priceMap.get(stop.symbol()));
            changeLocalPosition(stop, currentTime);
        }
    }

    private void updateStopOrderStatus(StopOrderDto stopOrder, PriceDto priceDto) {
        stopOrder = new StopOrderDto(StopOrderDto.StopOrderStatus.FILLED, priceDto.close(), stopOrder);
        this.allStopOrders.put(stopOrder.id(), stopOrder);
    }

    private void changeLocalPosition(StopOrderDto stopOrderDto, LocalDateTime currentTime) {
        if (!this.positions.containsKey(stopOrderDto.symbol())) {
            addNewPosition(stopOrderDto, currentTime);
            return;
        }

        FilledOrderDto filledOrderDto = this.positions.get(stopOrderDto.symbol());
        OrderDto.OrderAction action = stopOrderDto.action();

        if (action.equals(OrderDto.OrderAction.SELL) && filledOrderDto.action().equals(OrderDto.OrderAction.BUY)) {
            filledOrderDto = handleOppositeDirection(stopOrderDto, filledOrderDto);
        }

        if (action.equals(OrderDto.OrderAction.BUY) && filledOrderDto.action().equals(OrderDto.OrderAction.SELL)) {
            filledOrderDto = handleOppositeDirection(stopOrderDto, filledOrderDto);
        }

        if (action.equals(OrderDto.OrderAction.BUY) && filledOrderDto.action().equals(OrderDto.OrderAction.BUY)) {
            filledOrderDto = handleSameDirection(stopOrderDto, filledOrderDto);
        }

        if (action.equals(OrderDto.OrderAction.SELL) && filledOrderDto.action().equals(OrderDto.OrderAction.SELL)) {
            filledOrderDto = handleSameDirection(stopOrderDto, filledOrderDto);
        }

        if (filledOrderDto.quantity() < stopOrderDto.quantity()) {
            throw new RuntimeException("Position has less units than stop order. position){ " + filledOrderDto + " order){ " + stopOrderDto);
        }

    }

    private FilledOrderDto handleSameDirection(StopOrderDto stopOrderDto, FilledOrderDto filledOrderDto) {
        filledOrderDto = new FilledOrderDto(filledOrderDto.quantity() + stopOrderDto.quantity(), filledOrderDto);
        positions.put(filledOrderDto.identifier(), filledOrderDto);
        return filledOrderDto;
    }

    private FilledOrderDto handleOppositeDirection(StopOrderDto stopOrderDto, FilledOrderDto filledOrderDto) {
        if (filledOrderDto.quantity() == stopOrderDto.quantity()) {
            this.positions.remove(stopOrderDto.symbol());
        } else {
            filledOrderDto = new FilledOrderDto(filledOrderDto.quantity() - stopOrderDto.quantity(), filledOrderDto);
            this.positions.put(filledOrderDto.identifier(), filledOrderDto);
        }
        return filledOrderDto;
    }

    private void addNewPosition(StopOrderDto stopOrderDto, LocalDateTime currentTime) {
        FilledOrderDto filledOrder = new FilledOrderDto(
                currentTime,
                stopOrderDto.symbol(),
                stopOrderDto.action(),
                stopOrderDto.quantity(),
                stopOrderDto.filledPrice(),
                stopOrderDto.identifier(),
                stopOrderDto.id()
        );
        this.positions.put(stopOrderDto.symbol(), filledOrder);
    }

    public Map<Integer, StopOrderDto> getStopOrders() {
        return this.allStopOrders;
    }
}
