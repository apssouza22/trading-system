package com.apssouza.mytrade.trading.domain.forex.orderbook;

import com.apssouza.mytrade.trading.domain.forex.order.OrderDto;
import com.apssouza.mytrade.trading.domain.forex.portfolio.FilledOrderDto;
import com.apssouza.mytrade.trading.domain.forex.portfolio.PositionDto;
import com.apssouza.mytrade.trading.domain.forex.session.CycleHistory;
import com.apssouza.mytrade.trading.domain.forex.session.TransactionDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BookHistoryService {
    private final TransactionsExporter transactionsExporter;
    public List<CycleHistory> transactions = new CopyOnWriteArrayList<>();

    public BookHistoryService(TransactionsExporter transactionsExporter) {
        this.transactionsExporter = transactionsExporter;
    }

    private CycleHistory cycle;

    public List<CycleHistory> getTransactions() {
        return this.transactions;
    }

    public void startCycle(LocalDateTime time) {
        this.cycle = new CycleHistory(time);
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
