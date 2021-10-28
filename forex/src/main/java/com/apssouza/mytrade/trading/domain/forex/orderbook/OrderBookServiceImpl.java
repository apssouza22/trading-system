package com.apssouza.mytrade.trading.domain.forex.orderbook;

import com.apssouza.mytrade.trading.domain.forex.order.OrderDto;
import com.apssouza.mytrade.trading.domain.forex.portfolio.FilledOrderDto;
import com.apssouza.mytrade.trading.domain.forex.portfolio.PositionDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

class OrderBookServiceImpl implements OrderBookService {
    private final TransactionsExporter transactionsExporter;
    public List<CycleHistoryDto> transactions = new CopyOnWriteArrayList<>();

    public OrderBookServiceImpl(TransactionsExporter transactionsExporter) {
        this.transactionsExporter = transactionsExporter;
    }

    private CycleHistoryDto cycle;

    public List<CycleHistoryDto> getTransactions() {
        return this.transactions;
    }

    public void startCycle(LocalDateTime time) {
        this.cycle = new CycleHistoryDto(time);
    }

    public void endCycle() {
        transactions.add(this.cycle);
    }

    public void setState(TransactionDto.TransactionState exit, String identifier) {
        this.cycle.setState(exit, identifier);
    }

    public void addPosition(PositionDto ps) {
        this.cycle.addPosition(ps);
    }

    public void addOrderFilled(FilledOrderDto order) {
        this.cycle.addOrderFilled(order);
    }

    public void addOrder(OrderDto order) {
        this.cycle.addOrder(order);
    }

    public void export(String filepath) throws IOException {
        transactionsExporter.exportCsv(transactions, filepath);
    }

}
