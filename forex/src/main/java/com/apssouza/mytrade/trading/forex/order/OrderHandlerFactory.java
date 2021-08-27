package com.apssouza.mytrade.trading.forex.order;

import com.apssouza.mytrade.trading.forex.risk.PositionSizer;

public class OrderHandlerFactory {

    public static OrderHandler factory(PositionSizer positionSizer){
        return new OrderHandler(new MemoryOrderDao(), positionSizer);
    }
}
