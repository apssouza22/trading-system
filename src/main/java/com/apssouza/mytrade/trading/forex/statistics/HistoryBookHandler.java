package com.apssouza.mytrade.trading.forex.statistics;

import com.apssouza.mytrade.trading.forex.order.OrderDto;
import com.apssouza.mytrade.trading.forex.portfolio.FilledOrderDto;
import com.apssouza.mytrade.trading.forex.portfolio.Position;
import com.apssouza.mytrade.trading.forex.session.CycleHistory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HistoryBookHandler {
    private final TransactionsExporter transactionsExporter;
    public List<CycleHistory> transactions = new ArrayList<>();

    public HistoryBookHandler(TransactionsExporter transactionsExporter ) {
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

    public void setState(TransactionState exit, String identifier) {
        this.cycle.setState(exit, identifier);
    }

    public void addPosition(Position ps) {
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
