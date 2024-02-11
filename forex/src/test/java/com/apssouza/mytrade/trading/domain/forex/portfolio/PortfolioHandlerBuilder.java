package com.apssouza.mytrade.trading.domain.forex.portfolio;

import com.apssouza.mytrade.trading.domain.forex.brokerintegration.BrokerIntegrationService;
import com.apssouza.mytrade.trading.domain.forex.order.OrderService;
import com.apssouza.mytrade.trading.domain.forex.riskmanagement.RiskManagementService;
import com.apssouza.mytrade.trading.domain.forex.common.observerinfra.EventNotifier;

import static org.mockito.Mockito.mock;

public class PortfolioHandlerBuilder {
    OrderService orderService = mock(OrderService.class);
    BrokerIntegrationService brokerIntegrationService = mock(BrokerIntegrationService.class);
    RiskManagementService riskManagementService = mock(RiskManagementService.class);

    EventNotifier eventNotifier = mock(EventNotifier.class);

    public PortfolioService build() {
        return PortfolioFactory.create(
                orderService,
                brokerIntegrationService,
                riskManagementService,
                eventNotifier
        );
    }
}
