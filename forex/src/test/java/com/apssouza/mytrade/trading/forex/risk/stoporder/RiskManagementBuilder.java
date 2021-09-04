package com.apssouza.mytrade.trading.forex.risk.stoporder;

import com.apssouza.mytrade.trading.forex.order.StopOrderBuilder;
import com.apssouza.mytrade.trading.forex.order.StopOrderDto;
import com.apssouza.mytrade.trading.forex.portfolio.PortfolioModel;
import com.apssouza.mytrade.trading.forex.risk.RiskManagementFactory;
import com.apssouza.mytrade.trading.forex.risk.RiskManagementHandler;

import org.mockito.Mockito;

import java.util.Optional;

public class RiskManagementBuilder {

    public RiskManagementHandler build(){
        PortfolioModel portfolio = Mockito.mock(PortfolioModel.class);
        StopOrderCreatorFixed stopOrderCreatorFixed = Mockito.mock(StopOrderCreatorFixed.class);
        var stopOrderBuilder = new StopOrderBuilder();
        stopOrderBuilder.withType(StopOrderDto.StopOrderType.ENTRY_STOP);

        Mockito.when(
                stopOrderCreatorFixed.getEntryStopOrder(Mockito.any(), Mockito.any())
        ).thenReturn(
                Optional.of(stopOrderBuilder.build())
        );

        stopOrderBuilder.withType(StopOrderDto.StopOrderType.TRAILLING_STOP);
        Mockito.when(
                stopOrderCreatorFixed.getTrailingStopOrder(Mockito.any(), Mockito.any())
        ).thenReturn(
                Optional.of(stopOrderBuilder.build())
        );
        stopOrderBuilder.withType(StopOrderDto.StopOrderType.TAKE_PROFIT);
        Mockito.when(
                stopOrderCreatorFixed.getProfitStopOrder(Mockito.any())
        ).thenReturn(
                stopOrderBuilder.build()
        );
        stopOrderBuilder.withType(StopOrderDto.StopOrderType.HARD_STOP);
        Mockito.when(
                stopOrderCreatorFixed.getProfitStopOrder(Mockito.any())
        ).thenReturn(
                stopOrderBuilder.build()
        );
        return RiskManagementFactory.create(portfolio, stopOrderCreatorFixed);
    }
}
