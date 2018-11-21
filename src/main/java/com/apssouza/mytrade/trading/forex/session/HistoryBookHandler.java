package com.apssouza.mytrade.trading.forex.session;

import com.apssouza.mytrade.trading.forex.order.OrderDto;
import com.apssouza.mytrade.trading.forex.portfolio.FilledOrderDto;
import com.apssouza.mytrade.trading.forex.portfolio.Position;
import com.apssouza.mytrade.trading.forex.statistics.TransactionState;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HistoryBookHandler {
    public List<CycleHistory> transactions = new ArrayList<>();

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
}
