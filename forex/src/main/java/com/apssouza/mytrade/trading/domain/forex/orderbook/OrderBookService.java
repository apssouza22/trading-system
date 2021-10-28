package com.apssouza.mytrade.trading.domain.forex.orderbook;

import com.apssouza.mytrade.trading.domain.forex.order.OrderDto;
import com.apssouza.mytrade.trading.domain.forex.portfolio.FilledOrderDto;
import com.apssouza.mytrade.trading.domain.forex.portfolio.PositionDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderBookService {
    List<CycleHistoryDto> getTransactions();

    void startCycle(LocalDateTime time);

    void endCycle();

    void setState(TransactionDto.TransactionState exit, String identifier);

    void addPosition(PositionDto ps);

    void addOrderFilled(FilledOrderDto order);

    void addOrder(OrderDto order);

    void export(String filepath) throws IOException;
}
