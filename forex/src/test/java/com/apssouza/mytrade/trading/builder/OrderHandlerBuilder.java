package com.apssouza.mytrade.trading.builder;

import com.apssouza.mytrade.trading.forex.order.MemoryOrderDao;
import com.apssouza.mytrade.trading.forex.order.OrderHandler;
import com.apssouza.mytrade.trading.forex.risk.PositionSizerFixed;

import org.mockito.Mockito;

public class OrderHandlerBuilder {

    MemoryOrderDao memoryOrderDao = Mockito.mock(MemoryOrderDao.class);

    public void setMemoryOrderDao(MemoryOrderDao memoryOrderDao){
        this.memoryOrderDao = memoryOrderDao;
    }

    public OrderHandler build(){
        PositionSizerFixed positionSizerFixed = new PositionSizerFixed();
        OrderHandler orderHandler = new OrderHandler(
                memoryOrderDao,
                positionSizerFixed
        );
        return orderHandler;
    }
}
