package com.apssouza.mytrade.trading.domain.forex.risk.stoporder;

import com.apssouza.mytrade.trading.domain.forex.order.OrderDto;

import java.math.BigDecimal;

public record StopOrderDto (
        StopOrderType type,
        Integer id,
        StopOrderStatus status,
        OrderDto.OrderAction action,
        BigDecimal price,
        BigDecimal filledPrice,
        String symbol,
        int quantity,
        String identifier
){

    public StopOrderDto(StopOrderStatus status, StopOrderDto stop) {
        this(
                stop.type(),
                stop.id(),
                status,
                stop.action(),
                stop.price(),
                null,
                stop.symbol(),
                stop.quantity(),
                stop.identifier()
        );
    }

    public StopOrderDto(StopOrderStatus status, BigDecimal filledPrice, StopOrderDto stop) {
        this(
                stop.type(),
                stop.id(),
                status,
                stop.action(),
                stop.price(),
                filledPrice,
                stop.symbol(),
                stop.quantity(),
                stop.identifier()
        );

    }

    public enum StopOrderType {
        HARD_STOP, ENTRY_STOP, TRAILLING_STOP, STOP_LOSS, TAKE_PROFIT
    }

    public enum StopOrderStatus {
        FILLED, CANCELLED, OPENED,SUBMITTED, CREATED;
    }
}
