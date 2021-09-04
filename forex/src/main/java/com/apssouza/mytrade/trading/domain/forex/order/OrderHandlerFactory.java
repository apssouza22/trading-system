package com.apssouza.mytrade.trading.domain.forex.order;

import com.apssouza.mytrade.trading.domain.forex.risk.RiskManagementHandler;

public class OrderHandlerFactory {

    public static OrderHandler create(RiskManagementHandler riskManagementHandler){
        return create(riskManagementHandler, new MemoryOrderDao());
    }

    public static OrderHandler create(RiskManagementHandler riskManagementHandler, OrderDao orderDao){
        return new OrderHandler(orderDao, riskManagementHandler);
    }
}
