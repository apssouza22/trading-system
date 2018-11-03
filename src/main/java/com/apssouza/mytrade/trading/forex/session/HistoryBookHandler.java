package com.apssouza.mytrade.trading.forex.session;

import com.apssouza.mytrade.feed.price.PriceHandler;
import com.apssouza.mytrade.feed.signal.SignalDto;
import com.apssouza.mytrade.trading.forex.order.OrderDto;
import com.apssouza.mytrade.trading.forex.portfolio.FilledOrderDto;
import com.apssouza.mytrade.trading.forex.portfolio.Portfolio;
import com.apssouza.mytrade.trading.forex.portfolio.Position;
import com.apssouza.mytrade.trading.forex.session.event.Event;
import com.apssouza.mytrade.trading.forex.statistics.TransactionState;
import com.apssouza.mytrade.trading.forex.session.event.PriceChangedEvent;

import java.util.LinkedList;
import java.util.List;

public class HistoryBookHandler {

    private final Portfolio portfolio;
    private final PriceHandler priceHandler;
    public List<TransactionDto> transactions = new LinkedList<>();

    public HistoryBookHandler(Portfolio portfolio, PriceHandler priceHandler) {
        this.portfolio = portfolio;
        this.priceHandler = priceHandler;
    }

    public List<TransactionDto> getTransactions() {
        return this.transactions;
    }

    public void addSignal(SignalDto signal) {

    }

    public void process(Event event) {

    }

    public void setState(TransactionState exit, String identifier) {

    }

    public void addPosition(Position ps) {

    }

    public void addOrderFilled(FilledOrderDto filledOrderDto){

    }

    public void addOrder(OrderDto order) {

    }
}
