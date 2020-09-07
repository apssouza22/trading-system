package com.apssouza.mytrade.trading.forex.session;

import com.apssouza.mytrade.trading.forex.order.OrderDto;
import com.apssouza.mytrade.trading.forex.portfolio.FilledOrderDto;
import com.apssouza.mytrade.trading.forex.portfolio.Position;
import com.apssouza.mytrade.trading.forex.statistics.TransactionState;

import java.time.LocalDateTime;

public class TransactionDto {
    private final LocalDateTime time;
    private final String identifier;
    private OrderDto order;
    private Position position;
    private FilledOrderDto filledOrder;
    private TransactionState state;

    public TransactionDto(LocalDateTime time, String identifier) {
        this.time = time;
        this.identifier = identifier;
    }

    public LocalDateTime getTime() {
        return this.time;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public OrderDto getOrder() {
        return this.order;
    }

    public Position getPosition() {
        return this.position;
    }

    public FilledOrderDto getFilledOrder() {
        return this.filledOrder;
    }

    public TransactionState getState() {
        return this.state;
    }

    public void setOrder(OrderDto order) {
        this.order = order;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public void setFilledOrder(FilledOrderDto filledOrder) {
        this.filledOrder = filledOrder;
    }

    public void setState(TransactionState state) {
        this.state = state;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TransactionDto)) {
            return false;
        }
        final TransactionDto other =
                (TransactionDto) o;
        if (!other.canEqual((Object) this)) {
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
        final Object this$order = this.getOrder();
        final Object other$order = other.getOrder();
        if (this$order == null ? other$order != null : !this$order.equals(other$order)) {
            return false;
        }
        final Object this$position = this.getPosition();
        final Object other$position = other.getPosition();
        if (this$position == null ? other$position != null : !this$position.equals(other$position)) {
            return false;
        }
        final Object this$filledOrder = this.getFilledOrder();
        final Object other$filledOrder = other.getFilledOrder();
        if (this$filledOrder == null ? other$filledOrder != null : !this$filledOrder.equals(other$filledOrder)) {
            return false;
        }
        final Object this$state = this.getState();
        final Object other$state = other.getState();
        if (this$state == null ? other$state != null : !this$state.equals(other$state)) {
            return false;
        }
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof TransactionDto;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $time = this.getTime();
        result = result * PRIME + ($time == null ? 43 : $time.hashCode());
        final Object $identifier = this.getIdentifier();
        result = result * PRIME + ($identifier == null ? 43 : $identifier.hashCode());
        final Object $order = this.getOrder();
        result = result * PRIME + ($order == null ? 43 : $order.hashCode());
        final Object $position = this.getPosition();
        result = result * PRIME + ($position == null ? 43 : $position.hashCode());
        final Object $filledOrder = this.getFilledOrder();
        result = result * PRIME + ($filledOrder == null ? 43 : $filledOrder.hashCode());
        final Object $state = this.getState();
        result = result * PRIME + ($state == null ? 43 : $state.hashCode());
        return result;
    }

    public String toString() {
        return "TransactionDto(time=" + this.getTime() + ", identifier=" + this.getIdentifier() + ", order=" +
                this.getOrder() + ", position=" + this.getPosition() + ", filledOrder=" + this.getFilledOrder() +
                ", state=" + this.getState() + ")";
    }
}
