package com.apssouza.mytrade.trading.forex.order;

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

    public OrderDto(OrderStatus status, OrderDto order) {
        this(order.getSymbol(), order.getAction(), order.getQuantity(), order.getOrigin(), order.getTime(), order.getIdentifier(), status);
        this.id = order.getId();
    }

    public String getSymbol() {
        return this.symbol;
    }

    public OrderAction getAction() {
        return this.action;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public OrderOrigin getOrigin() {
        return this.origin;
    }

    public LocalDateTime getTime() {
        return this.time;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public OrderStatus getStatus() {
        return this.status;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof OrderDto)) {
            return false;
        }
        final OrderDto other =
                (OrderDto) o;
        if (!other.canEqual((Object) this)) {
            return false;
        }
        final Object this$symbol = this.getSymbol();
        final Object other$symbol = other.getSymbol();
        if (this$symbol == null ? other$symbol != null : !this$symbol.equals(other$symbol)) {
            return false;
        }
        final Object this$action = this.getAction();
        final Object other$action = other.getAction();
        if (this$action == null ? other$action != null : !this$action.equals(other$action)) {
            return false;
        }
        if (this.getQuantity() != other.getQuantity()) {
            return false;
        }
        final Object this$origin = this.getOrigin();
        final Object other$origin = other.getOrigin();
        if (this$origin == null ? other$origin != null : !this$origin.equals(other$origin)) {
            return false;
        }
        final Object this$time = this.getTime();
        final Object other$time = other.getTime();
        if (this$time == null ? other$time != null : !this$time.equals(other$time)) {
            return false;
        }
        final Object this$identifier = this.getIdentifier();
        final Object other$identifier = other.getIdentifier();
        if (this$identifier == null ? other$identifier != null : !this$identifier.equals(other$identifier)) {
            return false;
        }
        final Object this$status = this.getStatus();
        final Object other$status = other.getStatus();
        if (this$status == null ? other$status != null : !this$status.equals(other$status)) {
            return false;
        }
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) {
            return false;
        }
        return true;
    }

    protected boolean canEqual(Object other) {
        return other instanceof OrderDto;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $symbol = this.getSymbol();
        result = result * PRIME + ($symbol == null ? 43 : $symbol.hashCode());
        final Object $action = this.getAction();
        result = result * PRIME + ($action == null ? 43 : $action.hashCode());
        result = result * PRIME + this.getQuantity();
        final Object $origin = this.getOrigin();
        result = result * PRIME + ($origin == null ? 43 : $origin.hashCode());
        final Object $time = this.getTime();
        result = result * PRIME + ($time == null ? 43 : $time.hashCode());
        final Object $identifier = this.getIdentifier();
        result = result * PRIME + ($identifier == null ? 43 : $identifier.hashCode());
        final Object $status = this.getStatus();
        result = result * PRIME + ($status == null ? 43 : $status.hashCode());
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "OrderDto(symbol=" + this.getSymbol() + ", action=" + this.getAction() + ", quantity=" +
                this.getQuantity() + ", origin=" + this.getOrigin() + ", time=" + this.getTime() + ", identifier=" +
                this.getIdentifier() + ", status=" + this.getStatus() + ", id=" + this.getId() + ")";
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
