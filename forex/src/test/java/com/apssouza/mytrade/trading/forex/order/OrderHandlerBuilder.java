package com.apssouza.mytrade.trading.forex.order;

import com.apssouza.mytrade.trading.forex.risk.PositionSizerFixed;

import org.mockito.Mockito;

public class OrderHandlerBuilder {

    MemoryOrderDao memoryOrderDao = new MemoryOrderDao();

    public void setMemoryOrderDao(MemoryOrderDao memoryOrderDao){
        this.memoryOrderDao = memoryOrderDao;
    }

    public OrderHandler build(){
        OrderHandler orderHandler = new OrderHandler(
                memoryOrderDao,
                new PositionSizerFixed()
        );
        return orderHandler;
    }
}
