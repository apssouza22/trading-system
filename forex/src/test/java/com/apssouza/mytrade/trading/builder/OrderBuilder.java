package com.apssouza.mytrade.trading.builder;

import com.apssouza.mytrade.trading.forex.order.OrderDto;

import static com.apssouza.mytrade.trading.forex.order.OrderDto.OrderAction.*;
import static com.apssouza.mytrade.trading.forex.order.OrderDto.OrderOrigin.*;
import static com.apssouza.mytrade.trading.forex.order.OrderDto.OrderStatus.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderBuilder {
    private String symbol = "AUDUSD";
    private String identifier = "AUDUSD";
    private OrderDto.OrderAction action = BUY;
    private int qtd = 1000;
    private OrderDto.OrderOrigin origin = SIGNAL;
    private LocalDateTime time = LocalDateTime.MIN;
    private OrderDto.OrderStatus status = CREATED;

    public OrderBuilder withSymbol(String symbol) {
        this.symbol = symbol;
        return this;
    }

    public OrderBuilder withIdentifier(String identifier) {
        this.identifier = identifier;
        return this;
    }

    public OrderBuilder withAction(OrderDto.OrderAction action) {
        this.action = action;
        return this;
    }

    public OrderBuilder withQtd(int qtd) {
        this.qtd = qtd;
        return this;
    }

    public void withOrigin(OrderDto.OrderOrigin origin) {
        this.origin = origin;
    }

    public void withTime(LocalDateTime time) {
        this.time = time;
    }

    public OrderBuilder withStatus(OrderDto.OrderStatus status) {
        this.status = status;
        return this;
    }

    List<OrderDto> orders = new ArrayList<>();

    public OrderBuilder withOrder(LocalDateTime time, OrderDto.OrderAction action, OrderDto.OrderStatus status) {
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
