package com.apssouza.mytrade.trading.domain.forex.portfolio;

import com.apssouza.mytrade.trading.domain.forex.common.observer.PropertyChangeListener;
import com.apssouza.mytrade.trading.domain.forex.broker.BrokerService;
import com.apssouza.mytrade.trading.domain.forex.order.OrderService;
import com.apssouza.mytrade.trading.domain.forex.risk.RiskManagementService;
import com.apssouza.mytrade.trading.domain.forex.session.EventNotifier;

import java.util.Arrays;
import java.util.List;

public class PortfolioFactory {

    public static PortfolioService create(
            OrderService orderService,
            BrokerService executionHandler,
            PortfolioModel portfolio,
            RiskManagementService riskManagementService,
            EventNotifier eventNotifier
    ) {
        var reconciliationHandler = new PortfoliosChecker(portfolio, executionHandler);
        return new PortfolioService(
                orderService,
                executionHandler,
                portfolio,
                reconciliationHandler,
                riskManagementService,
                eventNotifier
        );
    }

    public static List<PropertyChangeListener> createListeners(
            PortfolioService portfolioService,
            PortfolioModel portfolio,
            EventNotifier eventNotifier
    ) {
        return Arrays.asList(
                new FilledOrderListener(portfolio, portfolioService),
                new StopOrderFilledListener(portfolio, portfolioService,eventNotifier),
                new EndedTradingDayListener(portfolioService)
        );
    }
}
