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

    public void setType(StopOrderType type) {
        this.type = type;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setStatus(StopOrderStatus status) {
        this.status = status;
    }

    public void setAction(OrderAction action) {
        this.action = action;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setFilledPrice(BigDecimal filledPrice) {
        this.filledPrice = filledPrice;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setQtd(Integer qtd) {
        this.qtd = qtd;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public StopOrderDto build() {
        return new StopOrderDto(type, id, status, action, price, filledPrice, symbol, qtd, identifier);
    }
}
