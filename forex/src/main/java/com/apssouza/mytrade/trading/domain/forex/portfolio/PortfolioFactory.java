package com.apssouza.mytrade.trading.domain.forex.portfolio;

import com.apssouza.mytrade.trading.domain.forex.broker.BrokerService;
import com.apssouza.mytrade.trading.domain.forex.common.observerinfra.Observer;
import com.apssouza.mytrade.trading.domain.forex.order.OrderService;
import com.apssouza.mytrade.trading.domain.forex.risk.RiskManagementService;
import com.apssouza.mytrade.trading.domain.forex.common.observerinfra.EventNotifier;

import java.util.Arrays;
import java.util.List;

public class PortfolioFactory {

    public static PortfolioService create(
            OrderService orderService,
            BrokerService executionHandler,
            RiskManagementService riskManagementService,
            EventNotifier eventNotifier
    ) {
        var reconciliationHandler = new PortfolioChecker(executionHandler);
        return new PortfolioService(
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
