package com.apssouza.mytrade.trading.forex.session;

import com.apssouza.mytrade.feed.signal.SignalDto;
import com.apssouza.mytrade.trading.forex.order.OrderDto;
import com.apssouza.mytrade.trading.forex.portfolio.FilledOrderDto;
import com.apssouza.mytrade.trading.forex.portfolio.Position;
import com.apssouza.mytrade.trading.forex.statistics.TransactionState;
import lombok.Data;

@Data
public class TransactionDto {
    private OrderDto order;
    private Position position;
    private FilledOrderDto filledOrder;
    private TransactionState state;
}
