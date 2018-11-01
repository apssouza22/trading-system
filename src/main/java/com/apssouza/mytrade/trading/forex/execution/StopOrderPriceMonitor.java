package com.apssouza.mytrade.trading.forex.execution;

import com.apssouza.mytrade.feed.price.PriceDto;
import com.apssouza.mytrade.trading.forex.order.OrderAction;
import com.apssouza.mytrade.trading.forex.order.StopOrderDto;
import com.apssouza.mytrade.trading.forex.order.StopOrderStatus;
import com.apssouza.mytrade.trading.forex.order.StopOrderType;

import java.util.*;

public class StopOrderPriceMonitor {

    private final Map<Integer, StopOrderDto> allStopOrders;

    public StopOrderPriceMonitor(Map<Integer, StopOrderDto> allStopOrders) {
        this.allStopOrders = allStopOrders;
    }

    public Set<StopOrderDto> getFilledOrders(Map<String, PriceDto> priceMap) {
        Set<StopOrderDto> filled_positions = new HashSet<>();
        for (Map.Entry<Integer, StopOrderDto> entry : this.allStopOrders.entrySet()) {
            StopOrderDto stopOrder = this.allStopOrders.get(entry.getKey());
            if (filled_positions.contains(stopOrder.getIdentifier())) {
                continue;
            }

            if (!stopOrder.getStatus().equals(StopOrderStatus.SUBMITTED)) {
                continue;
            }
            PriceDto df_current_price = priceMap.get(stopOrder.getSymbol());
            filledOrderCheck(filled_positions, entry, stopOrder, df_current_price);

        }
        return filled_positions;
    }

    private void filledOrderCheck(Set<StopOrderDto> filledPositions, Map.Entry<Integer, StopOrderDto> entry, StopOrderDto stopOrder, PriceDto priceDto) {
        if (stopOrder.getAction().equals(OrderAction.BUY)) {
            if(hasFilledBuyOrder(stopOrder, priceDto)){
                updateStopOrderStatus(filledPositions, entry, stopOrder, priceDto);
            }
        }

        if (stopOrder.getAction().equals(OrderAction.SELL)) {
            if(hasFilledSellOrder(stopOrder, priceDto)){
                updateStopOrderStatus(filledPositions, entry, stopOrder, priceDto);
            }
        }
    }

    private StopOrderDto updateStopOrderStatus(Set<StopOrderDto> filledPositions, Map.Entry<Integer, StopOrderDto> entry, StopOrderDto stopOrder, PriceDto priceDto) {
        stopOrder = new StopOrderDto(StopOrderStatus.FILLED, priceDto.getClose(), stopOrder);
        this.allStopOrders.put(entry.getKey(), stopOrder);
        filledPositions.add(stopOrder);
        return stopOrder;
    }

    private boolean hasFilledSellOrder(StopOrderDto stopOrder, PriceDto priceDto) {
        if (stopOrder.getType().equals(StopOrderType.TAKE_PROFIT)) {
            if (priceDto.getHigh().compareTo(stopOrder.getPrice()) >= 0) {
               return true;
            }
        }
        if (stopOrder.getType().equals(StopOrderType.STOP_LOSS)) {
            if (priceDto.getLow().compareTo(stopOrder.getPrice()) <= 0) {
                return true;
            }
        }
        return false;
    }

    private boolean hasFilledBuyOrder( StopOrderDto stopOrder, PriceDto priceDto) {
        if (stopOrder.getType().equals(StopOrderType.TAKE_PROFIT)) {
            if (priceDto.getLow().compareTo(stopOrder.getPrice()) <= 0) {
                return true;
            }
        }
        if (stopOrder.getType().equals(StopOrderType.STOP_LOSS)) {
            if (priceDto.getHigh().compareTo(stopOrder.getPrice()) >= 0) {
                return true;
            }
        }
        return false;
    }
}
