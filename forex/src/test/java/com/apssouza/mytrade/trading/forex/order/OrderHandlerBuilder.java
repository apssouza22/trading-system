package com.apssouza.mytrade.trading.forex.order;

import com.apssouza.mytrade.trading.forex.common.TradingParams;
import com.apssouza.mytrade.trading.forex.portfolio.PortfolioModel;
import com.apssouza.mytrade.trading.forex.risk.RiskManagementFactory;
import com.apssouza.mytrade.trading.forex.risk.stoporder.StopOrderCreator;
import com.apssouza.mytrade.trading.forex.risk.stoporder.StopOrderDto;
import com.apssouza.mytrade.trading.forex.risk.stoporder.StopOrderFactory;

import java.math.BigDecimal;

public class OrderHandlerBuilder {

    MemoryOrderDao memoryOrderDao = new MemoryOrderDao();

    public void setMemoryOrderDao(MemoryOrderDao memoryOrderDao){
        this.memoryOrderDao = memoryOrderDao;
    }

    public OrderHandler build(){
        StopOrderCreator stopOrderCreator = StopOrderFactory.factory(new StopOrderDto(
                TradingParams.hard_stop_loss_distance,
                TradingParams.take_profit_distance_fixed,
                TradingParams.entry_stop_loss_distance_fixed,
                TradingParams.trailing_stop_loss_distance
        ));
        var riskManagementHandler = RiskManagementFactory.create(new PortfolioModel(BigDecimal.TEN), stopOrderCreator);
        OrderHandler orderHandler = new OrderHandler(
                memoryOrderDao,
                riskManagementHandler
        );
        return orderHandler;
    }
}
