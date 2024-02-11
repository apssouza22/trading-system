package com.apssouza.mytrade.trading.domain.forex.order;

import com.apssouza.mytrade.trading.domain.forex.brokerintegration.BrokerIntegrationService;
import com.apssouza.mytrade.trading.domain.forex.common.observerinfra.EventNotifier;
import com.apssouza.mytrade.trading.domain.forex.common.observerinfra.Observer;
import com.apssouza.mytrade.trading.domain.forex.portfolio.PortfolioService;
import com.apssouza.mytrade.trading.domain.forex.riskmanagement.RiskManagementService;

import java.util.ArrayList;
import java.util.List;

public class OrderFactory {

    public static OrderService create(RiskManagementService riskManagementService){
        return create(riskManagementService, new MemoryOrderDao());
    }

    public static OrderService create(RiskManagementService riskManagementService, OrderDao orderDao){
        return new OrderServiceImpl(orderDao, riskManagementService);
    }

    public static List<Observer> createListeners(
            OrderService orderService,
            RiskManagementService riskManagementService,
            BrokerIntegrationService executionHandler,
            EventNotifier eventNotifier,
            PortfolioService portfolioService
    ) {
        var listeners = new ArrayList<Observer>();
        listeners.add(new PositionClosedListener(orderService));
        listeners.add(new OrderFoundListener(
                executionHandler,
                orderService,
                eventNotifier,
                riskManagementService
        ));
        listeners.add(new SignalCreatedListener(
                riskManagementService,
                orderService,
                eventNotifier,
                portfolioService
        ));
        return listeners;
    }
}
