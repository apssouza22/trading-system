package com.apssouza.mytrade.trading.builder;

import com.apssouza.mytrade.trading.forex.order.OrderAction;
import com.apssouza.mytrade.trading.forex.order.StopOrderDto;
import com.apssouza.mytrade.trading.forex.order.StopOrderType;
import com.apssouza.mytrade.trading.forex.portfolio.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.EnumMap;

public class FilledOrderBuilder {

    private LocalDateTime time = LocalDateTime.MIN;
    private String symbol = "AUDUSD";
    private OrderAction action = OrderAction.BUY;
    private int quantity = 100;
    private BigDecimal priceWithSpread = BigDecimal.TEN;
    private String identifier = "AUDUSD";
    private Integer id = 1;

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setAction(OrderAction action) {
        this.action = action;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPriceWithSpread(BigDecimal priceWithSpread) {
        this.priceWithSpread = priceWithSpread;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public FilledOrderDto build() {
        return new FilledOrderDto(time, symbol, action, quantity, priceWithSpread, identifier, id);
    }

}
