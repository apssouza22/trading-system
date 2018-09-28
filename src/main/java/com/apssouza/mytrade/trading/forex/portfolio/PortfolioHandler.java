package com.apssouza.mytrade.trading.forex.portfolio;

import com.apssouza.mytrade.feed.price.PriceHandler;
import com.apssouza.mytrade.feed.signal.SignalDao;
import com.apssouza.mytrade.feed.signal.SignalDto;
import com.apssouza.mytrade.trading.forex.execution.ExecutionHandler;
import com.apssouza.mytrade.trading.forex.order.OrderDto;
import com.apssouza.mytrade.trading.forex.order.OrderHandler;
import com.apssouza.mytrade.trading.forex.risk.PositionExitHandler;
import com.apssouza.mytrade.trading.forex.risk.PositionSizer;
import com.apssouza.mytrade.trading.forex.session.HistoryBookHandler;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class PortfolioHandler {
    private final BigDecimal equity;
    private final PriceHandler priceHandler;
    private final OrderHandler orderHandler;
    private final PositionSizer positionSizer;
    private final PositionExitHandler positionExitHandler;
    private final ExecutionHandler executionHandler;
    private final StopOrderHandler stopOrderHandler;
    private final Portfolio portfolio;
    private final ReconciliationHandler reconciliationHandler;
    private final HistoryBookHandler historyHandler;

    public PortfolioHandler(
            BigDecimal equity,
            PriceHandler priceHandler,
            OrderHandler orderHandler,
            PositionSizer positionSizer,
            PositionExitHandler positionExitHandler,
            ExecutionHandler executionHandler,
            StopOrderHandler stopOrderHandler,
            Portfolio portfolio,
            ReconciliationHandler reconciliationHandler,
            HistoryBookHandler historyHandler
    ) {

        this.equity = equity;
        this.priceHandler = priceHandler;
        this.orderHandler = orderHandler;
        this.positionSizer = positionSizer;
        this.positionExitHandler = positionExitHandler;
        this.executionHandler = executionHandler;
        this.stopOrderHandler = stopOrderHandler;
        this.portfolio = portfolio;
        this.reconciliationHandler = reconciliationHandler;
        this.historyHandler = historyHandler;
    }

    public void updatePortfolioValue(LocalDateTime currentTime) {

    }

    public void createStopOrder(LocalDateTime currentTime) {

    }

    public void processReconciliation() {

    }

    public void onOrder(List<OrderDto> orders) {

    }

    public void stopOrderHandle(LocalDateTime currentTime) {

    }

    public void processExits(LocalDateTime currentTime, List<SignalDto> signals) {

    }

    public void onSignal(List<SignalDto> signals, LocalDateTime currentTime) {

    }
}
