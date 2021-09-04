package com.apssouza.mytrade.trading.domain.forex.portfolio;

import com.apssouza.mytrade.trading.domain.forex.execution.OrderExecution;
import com.apssouza.mytrade.trading.domain.forex.order.OrderHandler;
import com.apssouza.mytrade.trading.domain.forex.risk.RiskManagementHandler;
import com.apssouza.mytrade.trading.domain.forex.session.EventNotifier;

import static org.mockito.Mockito.mock;

import java.math.BigDecimal;

public class PortfolioHandlerBuilder {
    OrderHandler orderHandler = mock(OrderHandler.class);
    OrderExecution executionHandler = mock(OrderExecution.class);
    PortfolioModel portfolio = new PortfolioModel(BigDecimal.valueOf(10000));
    RiskManagementHandler riskManagementHandler = mock(RiskManagementHandler.class);

    EventNotifier eventNotifier = mock(EventNotifier.class);

    public PortfolioHandler build() {
        return PortfolioFactory.create(
                orderHandler,
                executionHandler,
                portfolio,
                riskManagementHandler,
                eventNotifier
        );
    }
}
