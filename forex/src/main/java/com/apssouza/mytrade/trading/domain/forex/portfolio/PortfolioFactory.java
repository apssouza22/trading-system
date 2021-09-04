package com.apssouza.mytrade.trading.domain.forex.portfolio;

import com.apssouza.mytrade.trading.domain.forex.common.observer.PropertyChangeListener;
import com.apssouza.mytrade.trading.domain.forex.execution.OrderExecution;
import com.apssouza.mytrade.trading.domain.forex.order.OrderHandler;
import com.apssouza.mytrade.trading.domain.forex.risk.RiskManagementHandler;
import com.apssouza.mytrade.trading.domain.forex.session.EventNotifier;

import java.util.Collections;
import java.util.List;

public class PortfolioFactory {

    public static PortfolioHandler create(
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

    public static List<PropertyChangeListener> createListeners(PortfolioHandler portfolioHandler){
        return Collections.singletonList(new PortfolioChangedListener(portfolioHandler));
    }
}
