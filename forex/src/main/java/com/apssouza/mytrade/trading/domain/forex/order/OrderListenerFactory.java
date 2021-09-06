package com.apssouza.mytrade.trading.domain.forex.order;

import com.apssouza.mytrade.trading.domain.forex.common.observer.PropertyChangeListener;
import com.apssouza.mytrade.trading.domain.forex.execution.OrderExecution;
import com.apssouza.mytrade.trading.domain.forex.portfolio.PortfolioModel;
import com.apssouza.mytrade.trading.domain.forex.risk.RiskManagementHandler;
import com.apssouza.mytrade.trading.domain.forex.session.EventNotifier;

import java.util.ArrayList;
import java.util.List;

public class OrderListenerFactory {

    public static List<PropertyChangeListener> create(
            PortfolioModel portfolio,
            OrderHandler orderHandler,
            RiskManagementHandler riskManagementHandler,
            OrderExecution executionHandler,
            EventNotifier eventNotifier
    ) {
        var listeners = new ArrayList<PropertyChangeListener>();
        listeners.add(new FilledOrderListener(portfolio, eventNotifier));
        listeners.add(new OrderCreatedListener(orderHandler));
        listeners.add(new OrderFoundListener(
                executionHandler,
                orderHandler,
                eventNotifier,
                riskManagementHandler
        ));
        return listeners;
    }
}
