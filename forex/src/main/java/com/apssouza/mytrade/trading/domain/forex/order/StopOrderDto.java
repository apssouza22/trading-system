package com.apssouza.mytrade.trading.domain.forex.order;

import java.math.BigDecimal;

public class StopOrderDto {
    private StopOrderType type;
    private Integer id;
    private StopOrderStatus status;
    private OrderDto.OrderAction action;
    private BigDecimal price;
    private BigDecimal filledPrice;
    private String symbol;
    private int quantity;
    private final String identifier;

    public StopOrderDto(
            StopOrderType type,
            Integer id,
            StopOrderStatus status,
            OrderDto.OrderAction action,
            BigDecimal price,
            BigDecimal filledPrice,
            String symbol,
            int quantity,
            String identifier
    ) {
        this.type = type;
        this.id = id;
        this.status = status;
        this.action = action;
        this.price = price;
        this.filledPrice = filledPrice;
        this.symbol = symbol;
        this.quantity = quantity;
        this.identifier = identifier;
    }

    public StopOrderDto(StopOrderStatus status, StopOrderDto stop) {
        this(
                stop.getType(),
                stop.getId(),
                status,
                stop.getAction(),
                stop.getPrice(),
                null,
                stop.getSymbol(),
                stop.getQuantity(),
                stop.getIdentifier()
        );
    }

    public StopOrderDto(StopOrderStatus status, BigDecimal filledPrice, StopOrderDto stop) {
        this(
                stop.getType(),
                stop.getId(),
                status,
                stop.getAction(),
                stop.getPrice(),
                filledPrice,
                stop.getSymbol(),
                stop.getQuantity(),
                stop.getIdentifier()
        );

    }

    public StopOrderDto(String identifier) {
        this.identifier = identifier;
    }

    public StopOrderType getType() {
        return this.type;
    }

    public Integer getId() {
        return this.id;
    }

    public StopOrderStatus getStatus() {
        return this.status;
    }

    public OrderDto.OrderAction getAction() {
        return this.action;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public BigDecimal getFilledPrice() {
        return this.filledPrice;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public void setType(StopOrderType type) {
        this.type = type;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setStatus(StopOrderStatus status) {
        this.status = status;
    }

    public void setAction(OrderDto.OrderAction action) {
        this.action = action;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setFilledPrice(BigDecimal filledPrice) {
        this.filledPrice = filledPrice;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof StopOrderDto)) {
            return false;
        }
        final StopOrderDto other =
                (StopOrderDto) o;
        if (!other.canEqual((Object) this)) {
            return false;
        }
        final Object this$type = this.getType();
        final Object other$type = other.getType();
        if (this$type == null ? other$type != null : !this$type.equals(other$type)) {
            return false;
        }
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) {
            return false;
        }
        final Object this$status = this.getStatus();
        final Object other$status = other.getStatus();
        if (this$status == null ? other$status != null : !this$status.equals(other$status)) {
            return false;
        }
        final Object this$action = this.getAction();
        final Object other$action = other.getAction();
        if (this$action == null ? other$action != null : !this$action.equals(other$action)) {
            return false;
        }
        final Object this$price = this.getPrice();
        final Object other$price = other.getPrice();
        if (this$price == null ? other$price != null : !this$price.equals(other$price)) {
            return false;
        }
        final Object this$filledPrice = this.getFilledPrice();
        final Object other$filledPrice = other.getFilledPrice();
        if (this$filledPrice == null ? other$filledPrice != null : !this$filledPrice.equals(other$filledPrice)) {
            return false;
        }
        final Object this$symbol = this.getSymbol();
        final Object other$symbol = other.getSymbol();
        if (this$symbol == null ? other$symbol != null : !this$symbol.equals(other$symbol)) {
            return false;
        }
        if (this.getQuantity() != other.getQuantity()) {
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
        return other instanceof StopOrderDto;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $type = this.getType();
        result = result * PRIME + ($type == null ? 43 : $type.hashCode());
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $status = this.getStatus();
        result = result * PRIME + ($status == null ? 43 : $status.hashCode());
        final Object $action = this.getAction();
        result = result * PRIME + ($action == null ? 43 : $action.hashCode());
        final Object $price = this.getPrice();
        result = result * PRIME + ($price == null ? 43 : $price.hashCode());
        final Object $filledPrice = this.getFilledPrice();
        result = result * PRIME + ($filledPrice == null ? 43 : $filledPrice.hashCode());
        final Object $symbol = this.getSymbol();
        result = result * PRIME + ($symbol == null ? 43 : $symbol.hashCode());
        result = result * PRIME + this.getQuantity();
        final Object $identifier = this.getIdentifier();
        result = result * PRIME + ($identifier == null ? 43 : $identifier.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "StopOrderDto(type=" + this.getType() + ", id=" + this.getId() + ", status=" + this.getStatus() +
                ", action=" + this.getAction() + ", price=" + this.getPrice() + ", filledPrice=" +
                this.getFilledPrice() + ", symbol=" + this.getSymbol() + ", quantity=" + this.getQuantity() +
                ", identifier=" + this.getIdentifier() + ")";
    }

    public enum StopOrderType {
        HARD_STOP, ENTRY_STOP, TRAILLING_STOP, STOP_LOSS, TAKE_PROFIT
    }

    public enum StopOrderStatus {
        FILLED, CANCELLED, OPENED,SUBMITTED, CREATED;
    }
}
