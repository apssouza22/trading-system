package com.apssouza.mytrade.trading.domain.forex.portfolio;

import com.apssouza.mytrade.trading.domain.forex.order.OrderDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FilledOrderDto (
        LocalDateTime time,
        String symbol,
        OrderDto.OrderAction action,
        int quantity,
        BigDecimal priceWithSpread,
        String identifier,
        int id
){

    public FilledOrderDto(int quantity, FilledOrderDto filledOrderDto) {
        this(
                filledOrderDto.time(),
                filledOrderDto.symbol(),
                filledOrderDto.action(),
                quantity,
                filledOrderDto.priceWithSpread(),
                filledOrderDto.identifier(),
                filledOrderDto.id()
        );
    }

}
