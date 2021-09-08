package com.apssouza.mytrade.trading.domain.forex.order;

import java.time.LocalDateTime;

public record OrderDto(
        String symbol,
        OrderAction action,
        int quantity,
        OrderOrigin origin,
        LocalDateTime time,
        String identifier,
        OrderStatus status,
        int id
) {
    public OrderDto(
            String symbol,
            OrderAction action,
            int quantity,
            OrderOrigin origin,
            LocalDateTime time,
            String identifier,
            OrderStatus status
    ){
        this(symbol, action, quantity, origin, time, identifier, status, 0);
    }

    public OrderDto(
            Integer id,
            OrderDto order
    ) {
        this(order.symbol(), order.action(), order.quantity(), order.origin(), order.time(), order.identifier(), order.status(), id);
    }

    public OrderDto(String identifierFromOrder, OrderDto order) {
        this(order.symbol(), order.action(), order.quantity(), order.origin(), order.time(), identifierFromOrder, order.status(), order.id());
    }

    public OrderDto(OrderStatus status, OrderDto order) {
        this(order.symbol(), order.action(), order.quantity(), order.origin(), order.time(), order.identifier(), status, order.id());
    }

    public enum OrderOrigin {
        STOP_ORDER, EXITS, SIGNAL
    }

    public enum OrderStatus {
        CREATED, FILLED, FAILED, EXECUTED, PROCESSING, CANCELLED
    }

    public enum OrderAction {
        BUY, SELL
    }
}
