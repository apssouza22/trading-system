package com.apssouza.mytrade.trading.builder;

import com.apssouza.mytrade.feed.price.PriceHandler;
import com.apssouza.mytrade.trading.forex.portfolio.Portfolio;
import com.apssouza.mytrade.trading.forex.portfolio.Position;
import com.apssouza.mytrade.trading.forex.portfolio.PositionStatus;
import com.apssouza.mytrade.trading.forex.portfolio.PositionType;
import com.apssouza.mytrade.trading.forex.risk.PositionExitHandler;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class PositionExitBuilder {

    Map<String, Position> positionMap = new HashMap<>();


    public PositionExitBuilder addPosition(PositionType type){
        positionMap.put("AUDUSD", new Position(
                type,
                "AUDUSD",
                1000,
                BigDecimal.valueOf(1.004),
                LocalDateTime.now(),
                "AUDUSD",
                null,
                null,
                PositionStatus.FILLED
        ));
        return this;
    }

    public PositionExitHandler build(){
        Portfolio portfolio = Mockito.mock(Portfolio.class);
        PriceHandler priceHandler = Mockito.mock(PriceHandler.class);

        Mockito.when(portfolio.getPositions()).thenReturn(positionMap);
        return new PositionExitHandler(portfolio, priceHandler);
    }
}
