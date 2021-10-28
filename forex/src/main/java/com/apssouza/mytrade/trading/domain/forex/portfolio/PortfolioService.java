package com.apssouza.mytrade.trading.domain.forex.portfolio;

import com.apssouza.mytrade.feed.api.PriceDto;
import com.apssouza.mytrade.feed.api.SignalDto;
import com.apssouza.mytrade.trading.domain.forex.common.events.Event;
import com.apssouza.mytrade.trading.domain.forex.common.events.PriceChangedEvent;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface PortfolioService {

    PositionDto addPositionQtd(String identifier, int qtd, BigDecimal price) throws PortfolioException ;
    PositionDto getPosition(final String identifier);
    PositionDto addNewPosition(PositionDto.PositionType positionType, FilledOrderDto filledOrder);
    List<PositionDto> getPositions();
    List<PositionDto> closeAllPositions(PositionDto.ExitReason reason, Event event) ;

    void createStopOrder(Event event);
    void handleStopOrder(Event event);
    void updatePositionsPrices(Map<String, PriceDto> price) ;
    void checkExits(PriceChangedEvent event, List<SignalDto> signals) ;
    /**
     * Check if the local portfolio is in sync with the portfolio on the broker
     */
    void processReconciliation(Event e) ;
    boolean isEmpty();
    int size();
    boolean removePositionQtd(String identfier, int qtd) throws PortfolioException;
    boolean contains(String identifier);
    boolean closePosition(String identifier, PositionDto.ExitReason reason);
    void printPortfolio();

}
