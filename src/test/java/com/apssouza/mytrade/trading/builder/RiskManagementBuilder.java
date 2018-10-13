package com.apssouza.mytrade.trading.builder;

import com.apssouza.mytrade.trading.forex.order.StopOrderType;
import com.apssouza.mytrade.trading.forex.portfolio.Portfolio;
import com.apssouza.mytrade.trading.forex.risk.PositionSizer;
import com.apssouza.mytrade.trading.forex.risk.RiskManagementHandler;
import com.apssouza.mytrade.trading.forex.risk.stoporder.fixed.StopOrderCreatorFixed;
import org.mockito.Mockito;

import java.util.Optional;

public class RiskManagementBuilder {




    public RiskManagementHandler build(){
        Portfolio portfolio = Mockito.mock(Portfolio.class);
        PositionSizer positionSizer = Mockito.mock(PositionSizer.class);
        StopOrderCreatorFixed stopOrderCreatorFixed = Mockito.mock(StopOrderCreatorFixed.class);
        StopOrderBuilder stopOrderBuilder = new StopOrderBuilder();
        stopOrderBuilder.setType(StopOrderType.ENTRY_STOP);

        Mockito.when(
                stopOrderCreatorFixed.getEntryStopOrder(Mockito.any(), Mockito.any())
        ).thenReturn(
                Optional.of(stopOrderBuilder.build())
        );

        stopOrderBuilder.setType(StopOrderType.TRAILLING_STOP);
        Mockito.when(
                stopOrderCreatorFixed.getTrailingStopOrder(Mockito.any(), Mockito.any())
        ).thenReturn(
                Optional.of(stopOrderBuilder.build())
        );
        stopOrderBuilder.setType(StopOrderType.TAKE_PROFIT);
        Mockito.when(
                stopOrderCreatorFixed.getProfitStopOrder(Mockito.any())
        ).thenReturn(
                stopOrderBuilder.build()
        );
        stopOrderBuilder.setType(StopOrderType.HARD_STOP);
        Mockito.when(
                stopOrderCreatorFixed.getProfitStopOrder(Mockito.any())
        ).thenReturn(
                stopOrderBuilder.build()
        );
        return new RiskManagementHandler(portfolio, positionSizer, stopOrderCreatorFixed);
    }
}
