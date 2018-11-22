package com.apssouza.mytrade.trading.forex.portfolio;

import com.apssouza.mytrade.trading.forex.order.OrderAction;

public enum PositionType {
    LONG, SHORT;

    public OrderAction getOrderAction(){
        if (this == LONG){
            return OrderAction.BUY;
        }
        return OrderAction.SELL;
    }
}
