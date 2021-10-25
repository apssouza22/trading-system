package com.apssouza.mytrade.trading.domain.forex.risk.stopordercreation;

import com.apssouza.mytrade.feed.api.PriceDto;
import com.apssouza.mytrade.trading.domain.forex.portfolio.PositionDto;
import com.apssouza.mytrade.trading.domain.forex.common.events.Event;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

class StopOrderCreatorFixed implements StopOrderCreator {
    private CreatorContext creatorContext;
    private final StopOrderConfigDto priceDistance;

    public StopOrderCreatorFixed(StopOrderConfigDto priceDistance) {
        this.priceDistance = priceDistance;
    }

    @Override
    public void createContext(PositionDto.PositionType type) {
        if (type == PositionDto.PositionType.LONG) {
            this.creatorContext = new CreatorContext(new LongPositionStrategy(priceDistance));
            return;
        }
        this.creatorContext = new CreatorContext(new ShortPositionStrategy(priceDistance));

    }

    @Override
    public StopOrderDto getHardStopLoss(PositionDto position) {
        return creatorContext.getHardStopLoss(position);
    }

    @Override
    public StopOrderDto getProfitStopOrder(PositionDto position) {
        return creatorContext.getProfitStopOrder(position);
    }

    @Override
    public Optional<StopOrderDto> getEntryStopOrder(PositionDto position, Event event) {
        Map<String, PriceDto> price = event.getPrice();
        BigDecimal priceClose = price.get(position.symbol()).close();
        return creatorContext.getEntryStopOrder(position, priceClose);
    }

    @Override
    public Optional<StopOrderDto> getTrailingStopOrder(PositionDto position, Event event) {
        Map<String, PriceDto> price = event.getPrice();
        BigDecimal priceClose = price.get(position.symbol()).close();
        return creatorContext.getTrailingStopOrder(position, priceClose);
    }

}
