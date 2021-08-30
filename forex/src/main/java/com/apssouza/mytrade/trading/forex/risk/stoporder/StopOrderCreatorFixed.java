package com.apssouza.mytrade.trading.forex.risk.stoporder;

import com.apssouza.mytrade.feed.api.PriceDto;
import com.apssouza.mytrade.trading.forex.portfolio.Position;
import com.apssouza.mytrade.trading.forex.session.event.Event;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

class StopOrderCreatorFixed implements StopOrderCreator {
    private  CreatorContext creatorContext;
    private final StopOrderDto priceDistance;

    public StopOrderCreatorFixed(StopOrderDto priceDistance) {
        this.priceDistance = priceDistance;
    }

    @Override
    public void createContext(Position.PositionType type){
        if (type == Position.PositionType.LONG) {
            this.creatorContext = new CreatorContext(new LongPositionStrategy(priceDistance));
        }else{
            this.creatorContext = new CreatorContext(new ShortPositionStrategy(priceDistance));
        }
    }

    @Override
    public com.apssouza.mytrade.trading.forex.order.StopOrderDto getHardStopLoss(Position position) {
        return creatorContext.getHardStopLoss(position);
    }

    @Override
    public com.apssouza.mytrade.trading.forex.order.StopOrderDto getProfitStopOrder(Position position) {
        return creatorContext.getProfitStopOrder(position);
    }

    @Override
    public Optional<com.apssouza.mytrade.trading.forex.order.StopOrderDto> getEntryStopOrder(Position position, Event event) {
        Map<String, PriceDto> price = event.getPrice();
        BigDecimal priceClose = price.get(position.getSymbol()).close();
        return creatorContext.getEntryStopOrder(position, priceClose);
    }

    @Override
    public Optional<com.apssouza.mytrade.trading.forex.order.StopOrderDto> getTrailingStopOrder(Position position, Event event) {
        Map<String, PriceDto> price = event.getPrice();
        BigDecimal priceClose = price.get(position.getSymbol()).close();
        return creatorContext.getTrailingStopOrder(position, priceClose);
    }

}
