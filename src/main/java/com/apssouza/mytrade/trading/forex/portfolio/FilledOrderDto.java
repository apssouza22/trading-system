package com.apssouza.mytrade.trading.forex.portfolio;

import com.apssouza.mytrade.trading.forex.order.OrderAction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FilledOrderDto {
    private final LocalDateTime time;
    private final String symbol;
    private final OrderAction action;
    private final int quantity;
    private final BigDecimal priceWithSpread;
    private final String identifier;
    private final Integer id;
    private final BigDecimal spread;

    public FilledOrderDto(
            LocalDateTime time,
            String symbol,
            OrderAction action,
            int quantity,
            BigDecimal priceWithSpread,
            String identifier,
            Integer id,
            BigDecimal spread
    ) {


        this.time = time;
        this.symbol = symbol;
        this.action = action;
        this.quantity = quantity;
        this.priceWithSpread = priceWithSpread;
        this.identifier = identifier;
        this.id = id;
        this.spread = spread;
    }
}
