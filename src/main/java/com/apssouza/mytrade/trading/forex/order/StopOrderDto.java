package com.apssouza.mytrade.trading.forex.order;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.math.BigDecimal;

public class StopOrderDto {
    private StopOrderType type;
    private Integer id;
    private StopOrderStatus status;
    private OrderAction action;
    private BigDecimal price;
    private BigDecimal filledPrice;
    private String symbol;
    private int qtd;
    private final String identifier;

    public StopOrderDto(
            StopOrderType type,
            Integer id,
            StopOrderStatus status,
            OrderAction action,
            BigDecimal price,
            BigDecimal filledPrice,
            String symbol,
            int qtd,
            String identifier
    ) {
        this.type = type;
        this.id = id;
        this.status = status;
        this.action = action;
        this.price = price;
        this.filledPrice = filledPrice;
        this.symbol = symbol;
        this.qtd = qtd;
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

    public BigDecimal getFilledPrice() {
        return filledPrice;
    }

    public int getQtd() {
        return qtd;
    }

    public String getIdentifier() {
        return identifier;
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

    public OrderAction getAction() {
        return this.action;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public int getQuantity() {
        return this.qtd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        StopOrderDto that = (StopOrderDto) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(identifier, that.identifier)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(identifier)
                .toHashCode();
    }
}
