package com.apssouza.mytrade.trading.domain.forex.session;

import com.apssouza.mytrade.trading.domain.forex.order.OrderDto;
import com.apssouza.mytrade.trading.domain.forex.portfolio.FilledOrderDto;
import com.apssouza.mytrade.trading.domain.forex.portfolio.PositionDto;

import java.time.LocalDateTime;
import java.util.Objects;

public class TransactionDto {
    private final LocalDateTime time;
    private final String identifier;
    private OrderDto order;
    private PositionDto position;
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

    public PositionDto getPosition() {
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

    public void setPosition(PositionDto position) {
        this.position = position;
    }

    public void setFilledOrder(FilledOrderDto filledOrder) {
        this.filledOrder = filledOrder;
    }

    public void setState(TransactionState state) {
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TransactionDto)) {
            return false;
        }
        final TransactionDto other =(TransactionDto) o;
        if (!other.canEqual( this)) {
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
        return true;
    }

    protected boolean canEqual(Object other) {
        return other instanceof TransactionDto;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this);
    }

    public enum TransactionState {
        ENTRY, REMOVE_QTD, ADD_QTD, EXIT
    }
}
