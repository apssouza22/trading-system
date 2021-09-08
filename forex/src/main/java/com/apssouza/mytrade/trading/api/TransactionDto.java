package com.apssouza.mytrade.trading.api;

import com.apssouza.mytrade.trading.domain.forex.order.OrderDto;
import com.apssouza.mytrade.trading.domain.forex.portfolio.FilledOrderDto;
import com.apssouza.mytrade.trading.domain.forex.portfolio.Position;

import java.time.LocalDateTime;

public record TransactionDto(
        LocalDateTime time,
        String identifier,
        OrderDto order,
        FilledOrderDto filledOrder,
        Position position,
        String state
) {
}
