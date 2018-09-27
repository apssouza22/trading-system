package com.apssouza.mytrade.trading.forex.order;

import com.apssouza.mytrade.feed.price.PriceHandler;
import com.apssouza.mytrade.trading.forex.portfolio.Portfolio;
import com.apssouza.mytrade.trading.forex.risk.PositionSizer;

import java.math.BigDecimal;

public class OrderHandler {
    public OrderHandler(
            MemoryOrderDao orderDao,
            PositionSizer positionSizer,
            BigDecimal equity,
            PriceHandler priceHandler,
            Portfolio portfolio
    ) {

    }
}
