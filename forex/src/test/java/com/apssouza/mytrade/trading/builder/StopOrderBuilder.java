package com.apssouza.mytrade.trading.builder;

import com.apssouza.mytrade.trading.forex.order.OrderAction;
import com.apssouza.mytrade.trading.forex.order.StopOrderDto;
import com.apssouza.mytrade.trading.forex.order.StopOrderStatus;
import com.apssouza.mytrade.trading.forex.order.StopOrderType;

import java.math.BigDecimal;

public class StopOrderBuilder {

    StopOrderType type = StopOrderType.HARD_STOP;
    Integer id = 0;
    StopOrderStatus status = StopOrderStatus.CREATED;
    OrderAction action = OrderAction.BUY;
    BigDecimal price = BigDecimal.ONE;
    BigDecimal filledPrice = null;
    String symbol = "AUDUSD";
    Integer qtd = 100;
    String identifier = "AUDUSD";

    public void withType(StopOrderType type) {
        this.type = type;
    }

    public void withId(Integer id) {
        this.id = id;
    }

    public void withStatus(StopOrderStatus status) {
        this.status = status;
    }

    public void withAction(OrderAction action) {
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
