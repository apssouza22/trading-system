package com.apssouza.mytrade.trading.forex.order;

import com.apssouza.mytrade.trading.forex.risk.PositionSizer;

public class OrderHandlerFactory {

    public static OrderHandler factory(PositionSizer positionSizer){
        return factory(positionSizer, new MemoryOrderDao());
    }

    public static OrderHandler factory(PositionSizer positionSizer, OrderDao orderDao){
        return new OrderHandler(orderDao, positionSizer);
    }
}
