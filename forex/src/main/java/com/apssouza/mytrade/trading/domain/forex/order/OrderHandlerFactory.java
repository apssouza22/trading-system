package com.apssouza.mytrade.trading.domain.forex.order;

import com.apssouza.mytrade.trading.domain.forex.common.observer.PropertyChangeListener;
import com.apssouza.mytrade.trading.domain.forex.broker.OrderExecution;
import com.apssouza.mytrade.trading.domain.forex.portfolio.PortfolioModel;
import com.apssouza.mytrade.trading.domain.forex.risk.RiskManagementHandler;
import com.apssouza.mytrade.trading.domain.forex.session.EventNotifier;

import java.util.ArrayList;
import java.util.List;

public class OrderHandlerFactory {

    public static OrderHandler create(RiskManagementHandler riskManagementHandler){
        return create(riskManagementHandler, new MemoryOrderDao());
    }

    public static OrderHandler create(RiskManagementHandler riskManagementHandler, OrderDao orderDao){
        return new OrderHandler(orderDao, riskManagementHandler);
    }

    public static List<PropertyChangeListener> createListeners(
            PortfolioModel portfolio,
            OrderHandler orderHandler,
            RiskManagementHandler riskManagementHandler,
            OrderExecution executionHandler,
            EventNotifier eventNotifier
    ) {
        var listeners = new ArrayList<PropertyChangeListener>();
        listeners.add(new PositionClosedListener(orderHandler));
        listeners.add(new OrderFoundListener(
                executionHandler,
                orderHandler,
                eventNotifier,
                riskManagementHandler
        ));
        listeners.add(new SignalCreatedListener(
                riskManagementHandler,
                orderHandler,
                eventNotifier
        ));
        return listeners;
    }
}
