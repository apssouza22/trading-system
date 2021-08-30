package com.apssouza.mytrade.trading.forex.order;

import com.apssouza.mytrade.trading.forex.risk.RiskManagementHandler;

public class OrderHandlerFactory {

    public static OrderHandler factory(RiskManagementHandler riskManagementHandler){
        return factory(riskManagementHandler, new MemoryOrderDao());
    }

    public static OrderHandler factory(RiskManagementHandler riskManagementHandler, OrderDao orderDao){
        return new OrderHandler(orderDao, riskManagementHandler);
    }
}
