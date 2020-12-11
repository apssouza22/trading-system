package com.apssouza.mytrade.trading.forex.execution;

import com.apssouza.mytrade.feed.api.PriceDto;
import com.apssouza.mytrade.trading.forex.order.OrderAction;
import com.apssouza.mytrade.trading.forex.order.StopOrderDto;
import com.apssouza.mytrade.trading.forex.order.StopOrderStatus;
import com.apssouza.mytrade.trading.forex.order.StopOrderType;

import java.util.*;

public class StopOrderPriceMonitor {
    

    public Set<StopOrderDto> getFilledOrders(Map<String, PriceDto> priceMap, Map<Integer, StopOrderDto> allStopOrders) {
        Set<StopOrderDto> filledOrders = new HashSet<>();
        for (Map.Entry<Integer, StopOrderDto> entry : allStopOrders.entrySet()) {
            StopOrderDto stopOrder = allStopOrders.get(entry.getKey());
            if (filledOrders.contains(stopOrder.getIdentifier())) {
                continue;
            }

            if (!stopOrder.getStatus().equals(StopOrderStatus.SUBMITTED)) {
                continue;
            }
            PriceDto df_current_price = priceMap.get(stopOrder.getSymbol());
            filledOrderCheck(filledOrders, stopOrder, df_current_price);

        }
        return filledOrders;
    }

    private void filledOrderCheck(
            Set<StopOrderDto> filledPositions,
            StopOrderDto stopOrder, PriceDto priceDto
    ) {
        if (stopOrder.getAction().equals(OrderAction.BUY)) {
            if(hasFilledBuyOrder(stopOrder, priceDto)){
                filledPositions.add(stopOrder);
            }
        }

        if (stopOrder.getAction().equals(OrderAction.SELL)) {
            if(hasFilledSellOrder(stopOrder, priceDto)){
                filledPositions.add(stopOrder);
            }
        }
    }


    private boolean hasFilledSellOrder(StopOrderDto stopOrder, PriceDto priceDto) {
        if (stopOrder.getType().equals(StopOrderType.TAKE_PROFIT)) {
            if (priceDto.high().compareTo(stopOrder.getPrice()) >= 0) {
               return true;
            }
        }
        if (stopOrder.getType().equals(StopOrderType.STOP_LOSS)) {
            if (priceDto.high().compareTo(stopOrder.getPrice()) <= 0) {
                return true;
            }
        }
        return false;
    }

    private boolean hasFilledBuyOrder( StopOrderDto stopOrder, PriceDto priceDto) {
        if (stopOrder.getType().equals(StopOrderType.TAKE_PROFIT)) {
            if (priceDto.low().compareTo(stopOrder.getPrice()) <= 0) {
                return true;
            }
        }
        if (stopOrder.getType().equals(StopOrderType.STOP_LOSS)) {
            if (priceDto.high().compareTo(stopOrder.getPrice()) >= 0) {
                return true;
            }
        }
        return false;
    }
}
