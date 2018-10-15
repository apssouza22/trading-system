package com.apssouza.mytrade.trading.builder;

import com.apssouza.mytrade.feed.price.PriceHandler;
import com.apssouza.mytrade.trading.forex.order.MemoryOrderDao;
import com.apssouza.mytrade.trading.forex.order.OrderHandler;
import com.apssouza.mytrade.trading.forex.portfolio.Portfolio;
import com.apssouza.mytrade.trading.forex.risk.PositionSizerFixed;
import org.mockito.Mockito;

import java.math.BigDecimal;

public class OrderHandlerBuilder {

    MemoryOrderDao memoryOrderDao = Mockito.mock(MemoryOrderDao.class);

    public void setMemoryOrderDao(MemoryOrderDao memoryOrderDao){
        this.memoryOrderDao = memoryOrderDao;
    }

    public OrderHandler build(){
        PositionSizerFixed positionSizerFixed = new PositionSizerFixed();
        PriceHandler priceHandler = Mockito.mock(PriceHandler.class);
        Portfolio portfolio = Mockito.mock(Portfolio.class);
        OrderHandler orderHandler = new OrderHandler(
                memoryOrderDao,
                positionSizerFixed,
                BigDecimal.valueOf(1000),
                priceHandler,
                portfolio
        );
        return orderHandler;
    }
}
