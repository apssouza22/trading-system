package com.apssouza.mytrade.trading.domain.forex.portfolio;

import com.apssouza.mytrade.trading.domain.forex.brokerintegration.BrokerIntegrationService;
import com.apssouza.mytrade.trading.domain.forex.common.observerinfra.Observer;
import com.apssouza.mytrade.trading.domain.forex.order.OrderService;
import com.apssouza.mytrade.trading.domain.forex.riskmanagement.RiskManagementService;
import com.apssouza.mytrade.trading.domain.forex.common.observerinfra.EventNotifier;

import java.util.Arrays;
import java.util.List;

public class PortfolioFactory {

    public static PortfolioService create(
            OrderService orderService,
            BrokerIntegrationService executionHandler,
            RiskManagementService riskManagementService,
            EventNotifier eventNotifier
    ) {
        var reconciliationHandler = new PortfolioChecker(executionHandler);
        return new PortfolioServiceImpl(
                orderService,
                executionHandler,
                reconciliationHandler,
                riskManagementService,
                eventNotifier
        );
    }

    public static List<Observer> createListeners(
            PortfolioService portfolioService,
            EventNotifier eventNotifier
    ) {
        return Arrays.asList(
                new FilledOrderListener(portfolioService),
                new StopOrderFilledListener(portfolioService,eventNotifier),
                new EndedTradingDayListener(portfolioService)
        );
    }
}
