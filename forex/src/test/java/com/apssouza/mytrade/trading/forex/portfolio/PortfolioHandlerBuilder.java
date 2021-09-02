package com.apssouza.mytrade.trading.forex.portfolio;

import com.apssouza.mytrade.trading.forex.execution.OrderExecution;
import com.apssouza.mytrade.trading.forex.order.OrderHandler;
import com.apssouza.mytrade.trading.forex.risk.RiskManagementHandler;
import com.apssouza.mytrade.trading.forex.session.EventNotifier;
import com.apssouza.mytrade.trading.forex.statistics.HistoryBookHandler;

import static org.mockito.Mockito.mock;

import java.math.BigDecimal;

public class PortfolioHandlerBuilder {
    OrderHandler orderHandler = mock(OrderHandler.class);
    OrderExecution executionHandler = mock(OrderExecution.class);
    PortfolioModel portfolio = new PortfolioModel(BigDecimal.valueOf(10000));
    RiskManagementHandler riskManagementHandler = mock(RiskManagementHandler.class);

    EventNotifier eventNotifier = mock(EventNotifier.class);

    public PortfolioHandler build() {
        return PortfolioFactory.factory(
                orderHandler,
                executionHandler,
                portfolio,
                riskManagementHandler,
                eventNotifier
        );
    }
}
