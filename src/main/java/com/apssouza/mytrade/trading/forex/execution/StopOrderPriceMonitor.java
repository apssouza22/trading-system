package com.apssouza.mytrade.trading.forex.execution;

import com.apssouza.mytrade.feed.price.PriceDto;
import com.apssouza.mytrade.trading.forex.order.OrderAction;
import com.apssouza.mytrade.trading.forex.order.StopOrderDto;
import com.apssouza.mytrade.trading.forex.order.StopOrderStatus;
import com.apssouza.mytrade.trading.forex.order.StopOrderType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StopOrderPriceMonitor {

    private final Map<Integer, StopOrderDto> allStopOrders;
    private final Map<String, PriceDto> priceMap;

    public StopOrderPriceMonitor(Map<Integer, StopOrderDto> allStopOrders, Map<String, PriceDto> priceMap) {
        this.allStopOrders = allStopOrders;
        this.priceMap = priceMap;
    }

    public List<String> getFilledOrders() {
        List<String> filled_positions = new ArrayList<>();
        for (Map.Entry<Integer, StopOrderDto> entry : this.allStopOrders.entrySet()) {
            StopOrderDto stop_order = this.allStopOrders.get(entry.getKey());
            if (filled_positions.contains(stop_order.getIdentifier())) {
                continue;
            }

            if (!stop_order.getStatus().equals(StopOrderStatus.SUBMITTED)) {
                continue;
            }
            PriceDto df_current_price = priceMap.get(stop_order.getSymbol());


            if (stop_order.getAction().equals(OrderAction.BUY)) {
                if(hasFilledBuyOrder(stop_order, df_current_price)){
                    stop_order = new StopOrderDto(StopOrderStatus.FILLED, df_current_price.getClose(), stop_order);
                    this.allStopOrders.put(entry.getKey(), stop_order);
                    filled_positions.add(stop_order.getIdentifier());
                }
            }

            if (stop_order.getAction().equals(OrderAction.SELL)) {
                if(hasFilledSellOrder(stop_order, df_current_price)){
                    stop_order = new StopOrderDto(StopOrderStatus.FILLED, df_current_price.getClose(), stop_order);
                    this.allStopOrders.put(entry.getKey(), stop_order);
                    filled_positions.add(stop_order.getIdentifier());
                }
            }

        }
        return filled_positions;
    }

    private boolean hasFilledSellOrder(StopOrderDto stop_order, PriceDto df_current_price) {
        if (stop_order.getType().equals(StopOrderType.TAKE_PROFIT)) {
            if (df_current_price.getHigh().compareTo(stop_order.getPrice()) >= 0) {
               return true;
            }
        }
        if (stop_order.getType().equals(StopOrderType.STOP_LOSS)) {
            if (df_current_price.getLow().compareTo(stop_order.getPrice()) <= 0) {
                return true;
            }
        }
        return false;
    }

    private boolean hasFilledBuyOrder( StopOrderDto stop_order, PriceDto df_current_price) {
        if (stop_order.getType().equals(StopOrderType.TAKE_PROFIT)) {
            if (df_current_price.getLow().compareTo(stop_order.getPrice()) <= 0) {
                return true;
            }
        }
        if (stop_order.getType().equals(StopOrderType.STOP_LOSS)) {
            if (df_current_price.getHigh().compareTo(stop_order.getPrice()) >= 0) {
                return true;
            }
        }
        return false;
    }
}
