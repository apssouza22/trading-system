package com.apssouza.mytrade.trading.domain.forex.portfolio;

import com.apssouza.mytrade.trading.domain.forex.execution.OrderExecution;
import com.apssouza.mytrade.trading.domain.forex.order.OrderHandler;
import com.apssouza.mytrade.trading.domain.forex.risk.RiskManagementHandler;
import com.apssouza.mytrade.trading.domain.forex.session.EventNotifier;

public class PortfolioFactory {
    public static PortfolioHandler factory(
            OrderHandler orderHandler,
            OrderExecution executionHandler,
            PortfolioModel portfolio,
            RiskManagementHandler riskManagementHandler,
            EventNotifier eventNotifier
    ) {
        var reconciliationHandler = new PortfoliosChecker(portfolio, executionHandler);
        return new PortfolioHandler(
                orderHandler,
                executionHandler,
                portfolio,
                reconciliationHandler,
                riskManagementHandler,
                eventNotifier
        );
    }
}
