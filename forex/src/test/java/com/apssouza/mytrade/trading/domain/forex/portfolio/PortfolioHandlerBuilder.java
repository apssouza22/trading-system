package com.apssouza.mytrade.trading.domain.forex.portfolio;

import com.apssouza.mytrade.trading.domain.forex.broker.BrokerService;
import com.apssouza.mytrade.trading.domain.forex.order.OrderService;
import com.apssouza.mytrade.trading.domain.forex.risk.RiskManagementService;
import com.apssouza.mytrade.trading.domain.forex.common.observerinfra.EventNotifier;

import static org.mockito.Mockito.mock;

import java.math.BigDecimal;

public class PortfolioHandlerBuilder {
    OrderService orderService = mock(OrderService.class);
    BrokerService brokerService = mock(BrokerService.class);
    PortfolioModel portfolio = new PortfolioModel(BigDecimal.valueOf(10000));
    RiskManagementService riskManagementService = mock(RiskManagementService.class);

    EventNotifier eventNotifier = mock(EventNotifier.class);

    public PortfolioService build() {
        return PortfolioFactory.create(
                orderService,
                brokerService,
                portfolio,
                riskManagementService,
                eventNotifier
        );
    }
}
