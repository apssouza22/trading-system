package com.apssouza.mytrade.trading.forex.portfolio;

import com.apssouza.mytrade.trading.forex.order.OrderDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FilledOrderDto {
    private final LocalDateTime time;
    private final String symbol;
    private final OrderDto.OrderAction action;
    private final int quantity;
    private final BigDecimal priceWithSpread;
    private final String identifier;
    private final Integer id;

    public FilledOrderDto(
            LocalDateTime time,
            String symbol,
            OrderDto.OrderAction action,
            int quantity,
            BigDecimal priceWithSpread,
            String identifier,
            Integer id
    ) {

        this.time = time;
        this.symbol = symbol;
        this.action = action;
        this.quantity = quantity;
        this.priceWithSpread = priceWithSpread;
        this.identifier = identifier;
        this.id = id;
    }

    public FilledOrderDto(int quantity, FilledOrderDto filledOrderDto) {
        this(
                filledOrderDto.getTime(),
                filledOrderDto.getSymbol(),
                filledOrderDto.getAction(),
                quantity,
                filledOrderDto.getPriceWithSpread(),
                filledOrderDto.getIdentifier(),
                filledOrderDto.getId()
        );
    }

    public LocalDateTime getTime() {
        return this.time;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public OrderDto.OrderAction getAction() {
        return this.action;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public BigDecimal getPriceWithSpread() {
        return this.priceWithSpread;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public Integer getId() {
        return this.id;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof FilledOrderDto)) {
            return false;
        }
        final FilledOrderDto other =
                (FilledOrderDto) o;
        if (!other.canEqual((Object) this)) {
            return false;
        }
        final Object this$time = this.getTime();
        final Object other$time = other.getTime();
        if (this$time == null ? other$time != null : !this$time.equals(other$time)) {
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
        final Object this$priceWithSpread = this.getPriceWithSpread();
        final Object other$priceWithSpread = other.getPriceWithSpread();
        if (this$priceWithSpread == null ?
                other$priceWithSpread != null : !this$priceWithSpread.equals(other$priceWithSpread)) {
            return false;
        }
        final Object this$identifier = this.getIdentifier();
        final Object other$identifier = other.getIdentifier();
        if (this$identifier == null ? other$identifier != null : !this$identifier.equals(other$identifier)) {
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
        return other instanceof FilledOrderDto;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $time = this.getTime();
        result = result * PRIME + ($time == null ? 43 : $time.hashCode());
        final Object $symbol = this.getSymbol();
        result = result * PRIME + ($symbol == null ? 43 : $symbol.hashCode());
        final Object $action = this.getAction();
        result = result * PRIME + ($action == null ? 43 : $action.hashCode());
        result = result * PRIME + this.getQuantity();
        final Object $priceWithSpread = this.getPriceWithSpread();
        result = result * PRIME + ($priceWithSpread == null ? 43 : $priceWithSpread.hashCode());
        final Object $identifier = this.getIdentifier();
        result = result * PRIME + ($identifier == null ? 43 : $identifier.hashCode());
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        return result;
    }

    public String toString() {
        return "FilledOrderDto(time=" + this.getTime() + ", symbol=" + this.getSymbol() + ", action=" +
                this.getAction() + ", quantity=" + this.getQuantity() + ", priceWithSpread=" +
                this.getPriceWithSpread() + ", identifier=" + this.getIdentifier() + ", id=" + this.getId() + ")";
    }
}
