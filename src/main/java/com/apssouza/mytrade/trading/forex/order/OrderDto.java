package com.apssouza.mytrade.trading.forex.order;

import com.apssouza.mytrade.trading.forex.statistics.TransactionState;

import java.time.LocalDateTime;

public class OrderDto {

    private final String symbol;
    private final OrderAction action;
    private final int quantity;
    private final TransactionState state;
    private final LocalDateTime time;
    private final String identifier;
    private final OrderStatus status;
    private Integer id;

    public OrderDto(
            String symbol,
            OrderAction action,
            int quantity,
            TransactionState state,
            LocalDateTime time,
            String identifier,
            OrderStatus status
    ) {

        this.symbol = symbol;
        this.action = action;
        this.quantity = quantity;
        this.state = state;
        this.time = time;
        this.identifier = identifier;
        this.status = status;
    }

    public OrderDto(
            Integer id,
            OrderDto order
    ) {
        this(order.getSymbol(), order.getAction(), order.getQuantity(), order.getState(), order.getTime(), order.getIdentifier(), order.getStatus());
        this.id = id;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public OrderDto(OrderStatus status, OrderDto order) {
        this(order.getSymbol(), order.getAction(), order.getQuantity(), order.getState(), order.getTime(), order.getIdentifier(), status);
        this.id = order.getId();
    }

    private Integer getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }

    public OrderAction getAction() {
        return action;
    }

    public int getQuantity() {
        return quantity;
    }

    public TransactionState getState() {
        return state;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public String getIdentifier() {
        return identifier;
    }
}
