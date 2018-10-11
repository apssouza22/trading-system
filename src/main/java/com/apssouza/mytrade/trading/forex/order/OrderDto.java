package com.apssouza.mytrade.trading.forex.order;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.LocalDateTime;

public class OrderDto {

    private final String symbol;
    private final OrderAction action;
    private final int quantity;
    private final OrderOrigin origin;
    private final LocalDateTime time;
    private final String identifier;
    private final OrderStatus status;
    private Integer id;

    public OrderDto(
            String symbol,
            OrderAction action,
            int quantity,
            OrderOrigin origin,
            LocalDateTime time,
            String identifier,
            OrderStatus status
    ) {

        this.symbol = symbol;
        this.action = action;
        this.quantity = quantity;
        this.origin = origin;
        this.time = time;
        this.identifier = identifier;
        this.status = status;
    }

    public OrderDto(
            Integer id,
            OrderDto order
    ) {
        this(order.getSymbol(), order.getAction(), order.getQuantity(), order.getOrigin(), order.getTime(), order.getIdentifier(), order.getStatus());
        this.id = id;
    }

    public OrderDto(String identifierFromOrder, OrderDto order) {
        this(
                order.getSymbol(),
                order.getAction(),
                order.getQuantity(),
                order.getOrigin(),
                order.getTime(),
                identifierFromOrder,
                order.getStatus()
        );
        this.id = order.getId();
    }

    public OrderStatus getStatus() {
        return status;
    }

    public OrderDto(OrderStatus status, OrderDto order) {
        this(order.getSymbol(), order.getAction(), order.getQuantity(), order.getOrigin(), order.getTime(), order.getIdentifier(), status);
        this.id = order.getId();
    }

    public Integer getId() {
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

    public OrderOrigin getOrigin() {
        return origin;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        OrderDto orderDto = (OrderDto) o;

        return new EqualsBuilder()
                .append(identifier, orderDto.identifier)
                .append(id, orderDto.id)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(identifier)
                .append(id)
                .toHashCode();
    }
}
