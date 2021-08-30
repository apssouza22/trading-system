package com.apssouza.mytrade.trading.forex.portfolio;

import com.apssouza.mytrade.trading.forex.execution.OrderExecution;
import com.apssouza.mytrade.trading.forex.order.OrderHandler;
import com.apssouza.mytrade.trading.forex.risk.RiskManagementHandler;
import com.apssouza.mytrade.trading.forex.session.EventNotifier;
import com.apssouza.mytrade.trading.forex.statistics.HistoryBookHandler;

public class PortfolioFactory {
    public static PortfolioHandler factory(
            OrderHandler orderHandler,
            OrderExecution executionHandler,
            PortfolioModel portfolio,
            HistoryBookHandler historyHandler,
            RiskManagementHandler riskManagementHandler,
            EventNotifier eventNotifier
    ) {
        var reconciliationHandler = new ReconciliationHandler(portfolio, executionHandler);
        return new PortfolioHandler(
                orderHandler,
                executionHandler,
                portfolio,
                reconciliationHandler,
                historyHandler,
                riskManagementHandler,
                eventNotifier
        );
    }
}
