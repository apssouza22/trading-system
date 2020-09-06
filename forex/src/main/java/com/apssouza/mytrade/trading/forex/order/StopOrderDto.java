package com.apssouza.mytrade.trading.forex.order;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StopOrderDto {
    private StopOrderType type;
    private Integer id;
    private StopOrderStatus status;
    private OrderAction action;
    private BigDecimal price;
    private BigDecimal filledPrice;
    private String symbol;
    private int quantity;
    private final String identifier;

    public StopOrderDto(
            StopOrderType type,
            Integer id,
            StopOrderStatus status,
            OrderAction action,
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

}
