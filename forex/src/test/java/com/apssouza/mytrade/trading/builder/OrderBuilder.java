package com.apssouza.mytrade.trading.builder;

import com.apssouza.mytrade.trading.forex.order.OrderAction;
import com.apssouza.mytrade.trading.forex.order.OrderDto;
import com.apssouza.mytrade.trading.forex.order.OrderOrigin;
import com.apssouza.mytrade.trading.forex.order.OrderStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderBuilder {
    private String symbol = "AUDUSD";
    private String identifier = "AUDUSD";
    private OrderAction action = OrderAction.BUY;
    private int qtd = 1000;
    private OrderOrigin origin = OrderOrigin.SIGNAL;
    private LocalDateTime time = LocalDateTime.MIN;
    private OrderStatus status = OrderStatus.CREATED;

    public OrderBuilder withSymbol(String symbol) {
        this.symbol = symbol;
        return this;
    }

    public OrderBuilder withIdentifier(String identifier) {
        this.identifier = identifier;
        return this;
    }

    public OrderBuilder withAction(OrderAction action) {
        this.action = action;
        return this;
    }

    public OrderBuilder withQtd(int qtd) {
        this.qtd = qtd;
        return this;
    }

    public void withOrigin(OrderOrigin origin) {
        this.origin = origin;
    }

    public void withTime(LocalDateTime time) {
        this.time = time;
    }

    public OrderBuilder withStatus(OrderStatus status) {
        this.status = status;
        return this;
    }

    List<OrderDto> orders = new ArrayList<>();

    public OrderBuilder withOrder(LocalDateTime time, OrderAction action, OrderStatus status) {
        orders.add(new OrderDto(
                symbol,
                action,
                qtd,
                origin,
                time,
                identifier,
                status
        ));
        return this;
    }

    public List<OrderDto> buildList() {
        return orders;
    }

    public OrderDto build() {
        if (orders.isEmpty()){
            return new OrderDto(
                    symbol,
                    action,
                    qtd,
                    origin,
                    time,
                    identifier,
                    status
            );
        }
        return orders.get(0);
    }
}
