package com.apssouza.mytrade.trading.forex.order;

import java.math.BigDecimal;

public class StopOrderDto {
    private StopOrderType type;
    private Integer id;
    private StopOrderStatus status;
    private OrderAction action;
    private BigDecimal price;
    private BigDecimal filledPrice;
    private BigDecimal spread;
    private String symbol;
    private int qtd;

    public StopOrderType getType() {
        return this.type;
    }

    public Integer getId() {
        return this.id;
    }

    public StopOrderStatus getStatus() {
        return this.status;
    }

    public OrderAction getAction() {
        return this.action;
    }

    public BigDecimal getPrice() {
        return this.price;
    }


    public BigDecimal getSpread() {
        return this.spread;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public int getQuantity() {
        return this.qtd;
    }
}
