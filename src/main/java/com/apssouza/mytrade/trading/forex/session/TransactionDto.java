package com.apssouza.mytrade.trading.forex.session;

import com.apssouza.mytrade.trading.forex.order.OrderDto;
import com.apssouza.mytrade.trading.forex.portfolio.FilledOrderDto;
import com.apssouza.mytrade.trading.forex.portfolio.Position;
import com.apssouza.mytrade.trading.forex.statistics.TransactionState;
import lombok.Data;

import java.time.LocalDateTime;

@Data
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
}
