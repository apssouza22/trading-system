package com.apssouza.mytrade.trading.forex.session;

import com.apssouza.mytrade.trading.forex.order.OrderDto;
import com.apssouza.mytrade.trading.forex.portfolio.FilledOrderDto;
import com.apssouza.mytrade.trading.forex.portfolio.Position;
import com.apssouza.mytrade.trading.forex.statistics.TransactionState;
import com.apssouza.mytrade.trading.misc.helper.file.CSVHelper;
import com.apssouza.mytrade.trading.misc.helper.file.WriteFileHelper;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

    public void export(String filepath) {
        for (CycleHistory item : transactions) {
            for (Map.Entry<String, TransactionDto> trans : item.getTransactions().entrySet()) {
                List<String> line = Arrays.asList(
                        trans.getValue().getIdentifier(),
                        trans.getValue().getPosition() != null ? trans.getValue().getPosition().getInitPrice().toString() : "",
                        trans.getValue().getPosition() != null ? trans.getValue().getPosition().getQuantity().toString() : "",
                        trans.getValue().getPosition() != null ? trans.getValue().getPosition().getInitPrice().multiply(BigDecimal.valueOf(trans.getValue().getPosition().getQuantity())).toString() : "",
                        trans.getValue().getOrder() != null ? trans.getValue().getOrder().getAction().toString() : "",
                        trans.getValue().getPosition() != null ? trans.getValue().getPosition().getTimestamp().toString() : "",
                        //trans.getValue().getPosition() != null ? trans.getValue().getPosition().getPlacedStopLoss().getPrice().toString(): "",
                        //trans.getValue().getPosition() != null ? trans.getValue().getPosition().getTakeProfitOrder().getPrice().toString(): "",
                        trans.getValue().getState() != null ? trans.getValue().getState().toString() : ""
                );
                WriteFileHelper.append(filepath, String.join(",", line));

            }

        }

    }


}
