package com.apssouza.mytrade.trading.forex.risk.stoporder.fixed;

import com.apssouza.mytrade.feed.PriceDto;
import com.apssouza.mytrade.trading.forex.order.StopOrderDto;
import com.apssouza.mytrade.trading.forex.portfolio.Position;
import com.apssouza.mytrade.trading.forex.portfolio.PositionType;
import com.apssouza.mytrade.trading.forex.risk.stoporder.PriceDistanceObject;
import com.apssouza.mytrade.trading.forex.risk.stoporder.StopOrderCreator;
import com.apssouza.mytrade.trading.forex.session.event.Event;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

public class StopOrderCreatorFixed implements StopOrderCreator {
    private  CreatorContext creatorContext;
    private final PriceDistanceObject priceDistance;

    public StopOrderCreatorFixed(PriceDistanceObject priceDistance) {
        this.priceDistance = priceDistance;
    }

    @Override
    public void createContext(PositionType type){
        if (type == PositionType.LONG) {
            this.creatorContext = new CreatorContext(new LongPositionStrategy(priceDistance));
        }else{
            this.creatorContext = new CreatorContext(new ShortPositionStrategy(priceDistance));
        }
    }

    @Override
    public StopOrderDto getHardStopLoss(Position position) {
        return creatorContext.getHardStopLoss(position);
    }

    @Override
    public StopOrderDto getProfitStopOrder(Position position) {
        return creatorContext.getProfitStopOrder(position);
    }

    @Override
    public Optional<StopOrderDto> getEntryStopOrder(Position position, Event event) {
        Map<String, PriceDto> price = event.getPrice();
        BigDecimal priceClose = price.get(position.getSymbol()).getClose();
        return creatorContext.getEntryStopOrder(position, priceClose);
    }

    @Override
    public Optional<StopOrderDto> getTrailingStopOrder(Position position, Event event) {
        Map<String, PriceDto> price = event.getPrice();
        BigDecimal priceClose = price.get(position.getSymbol()).getClose();
        return creatorContext.getTrailingStopOrder(position, priceClose);
    }

}
