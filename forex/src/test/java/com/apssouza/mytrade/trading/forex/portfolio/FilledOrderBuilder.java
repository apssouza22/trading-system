package com.apssouza.mytrade.trading.forex.portfolio;

import com.apssouza.mytrade.trading.forex.order.OrderDto;
import com.apssouza.mytrade.trading.forex.portfolio.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FilledOrderBuilder {

    private LocalDateTime time = LocalDateTime.MIN;
    private String symbol = "AUDUSD";
    private OrderDto.OrderAction action = OrderDto.OrderAction.BUY;
    private int quantity = 100;
    private BigDecimal priceWithSpread = BigDecimal.TEN;
    private String identifier = "AUDUSD";
    private Integer id = 1;

    public void withTime(LocalDateTime time) {
        this.time = time;
    }

    public void withSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void withAction(OrderDto.OrderAction action) {
        this.action = action;
    }

    public void withQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void withPriceWithSpread(BigDecimal priceWithSpread) {
        this.priceWithSpread = priceWithSpread;
    }

    public void withIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void withId(Integer id) {
        this.id = id;
    }

    public FilledOrderDto build() {
        return new FilledOrderDto(time, symbol, action, quantity, priceWithSpread, identifier, id);
    }

}
