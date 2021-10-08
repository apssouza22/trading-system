package com.apssouza.mytrade.trading.domain.forex.execution;

import com.apssouza.mytrade.feed.api.PriceDto;
import com.apssouza.mytrade.trading.domain.forex.order.OrderDto;
import com.apssouza.mytrade.trading.domain.forex.risk.stoporder.StopOrderDto;

import java.util.*;

class StopOrderPriceMonitor {
    

    public Set<StopOrderDto> getFilledOrders(Map<String, PriceDto> priceMap, Map<Integer, StopOrderDto> allStopOrders) {
        Set<StopOrderDto> filledOrders = new HashSet<>();
        for (Map.Entry<Integer, StopOrderDto> entry : allStopOrders.entrySet()) {
            StopOrderDto stopOrder = allStopOrders.get(entry.getKey());
            if (filledOrders.contains(stopOrder)) {
                continue;
            }

            if (!stopOrder.status().equals(StopOrderDto.StopOrderStatus.SUBMITTED)) {
                continue;
            }
            PriceDto df_current_price = priceMap.get(stopOrder.symbol());
            filledOrderCheck(filledOrders, stopOrder, df_current_price);

        }
        return filledOrders;
    }

    private void filledOrderCheck(
            Set<StopOrderDto> filledPositions,
            StopOrderDto stopOrder, PriceDto priceDto
    ) {
        if (stopOrder.action().equals(OrderDto.OrderAction.BUY)) {
            if(hasFilledBuyOrder(stopOrder, priceDto)){
                filledPositions.add(stopOrder);
            }
        }

        if (stopOrder.action().equals(OrderDto.OrderAction.SELL)) {
            if(hasFilledSellOrder(stopOrder, priceDto)){
                filledPositions.add(stopOrder);
            }
        }
    }


    private boolean hasFilledSellOrder(StopOrderDto stopOrder, PriceDto priceDto) {
        if (stopOrder.type().equals(StopOrderDto.StopOrderType.TAKE_PROFIT)) {
            if (priceDto.high().compareTo(stopOrder.price()) >= 0) {
               return true;
            }
        }
        if (stopOrder.type().equals(StopOrderDto.StopOrderType.STOP_LOSS)) {
            if (priceDto.high().compareTo(stopOrder.price()) <= 0) {
                return true;
            }
        }
        return false;
    }

    private boolean hasFilledBuyOrder( StopOrderDto stopOrder, PriceDto priceDto) {
        if (stopOrder.type().equals(StopOrderDto.StopOrderType.TAKE_PROFIT)) {
            if (priceDto.low().compareTo(stopOrder.price()) <= 0) {
                return true;
            }
        }
        if (stopOrder.type().equals(StopOrderDto.StopOrderType.STOP_LOSS)) {
            if (priceDto.high().compareTo(stopOrder.price()) >= 0) {
                return true;
            }
        }
        return false;
    }
}
