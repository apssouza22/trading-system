package com.apssouza.mytrade.trading.domain.forex.order;

import com.apssouza.mytrade.trading.domain.forex.risk.stoporder.StopOrderDto;

import java.math.BigDecimal;

public class StopOrderBuilder {

    StopOrderDto.StopOrderType type = StopOrderDto.StopOrderType.HARD_STOP;
    Integer id = 0;
    StopOrderDto.StopOrderStatus status = StopOrderDto.StopOrderStatus.CREATED;
    OrderDto.OrderAction action = OrderDto.OrderAction.BUY;
    BigDecimal price = BigDecimal.ONE;
    BigDecimal filledPrice = null;
    String symbol = "AUDUSD";
    Integer qtd = 100;
    String identifier = "AUDUSD";

    public void withType(StopOrderDto.StopOrderType type) {
        this.type = type;
    }

    public void withId(Integer id) {
        this.id = id;
    }

    public StopOrderBuilder withStatus(StopOrderDto.StopOrderStatus status) {
        this.status = status;
        return this;
    }

    public void withAction(OrderDto.OrderAction action) {
        this.action = action;
    }

    public void withPrice(BigDecimal price) {
        this.price = price;
    }

    public void withFilledPrice(BigDecimal filledPrice) {
        this.filledPrice = filledPrice;
    }

    public void withSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void withQtd(Integer qtd) {
        this.qtd = qtd;
    }

    public void withIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public StopOrderDto build() {
        return new StopOrderDto(type, id, status, action, price, filledPrice, symbol, qtd, identifier);
    }
}
