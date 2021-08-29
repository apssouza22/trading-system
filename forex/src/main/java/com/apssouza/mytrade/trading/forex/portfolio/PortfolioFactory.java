package com.apssouza.mytrade.trading.forex.portfolio;

import com.apssouza.mytrade.trading.forex.execution.OrderExecution;
import com.apssouza.mytrade.trading.forex.order.OrderHandler;
import com.apssouza.mytrade.trading.forex.risk.PositionExitHandler;
import com.apssouza.mytrade.trading.forex.risk.RiskManagementHandler;
import com.apssouza.mytrade.trading.forex.session.EventNotifier;
import com.apssouza.mytrade.trading.forex.statistics.HistoryBookHandler;

import java.math.BigDecimal;

public class PortfolioFactory {
    public static PortfolioHandler factory(
            BigDecimal equity,
            OrderHandler orderHandler,
            PositionExitHandler positionExitHandler,
            OrderExecution executionHandler,
            PortfolioModel portfolio,
            HistoryBookHandler historyHandler,
            RiskManagementHandler riskManagementHandler,
            EventNotifier eventNotifier
    ) {
        var reconciliationHandler = new ReconciliationHandler(portfolio, executionHandler);
        return new PortfolioHandler(
                equity,
                orderHandler,
                positionExitHandler,
                executionHandler,
                portfolio,
                reconciliationHandler,
                historyHandler,
                riskManagementHandler,
                eventNotifier
        );
    }
}
