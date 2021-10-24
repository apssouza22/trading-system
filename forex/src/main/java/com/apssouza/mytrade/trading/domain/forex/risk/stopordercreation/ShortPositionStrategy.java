package com.apssouza.mytrade.trading.domain.forex.risk.stopordercreation;

import com.apssouza.mytrade.trading.domain.forex.portfolio.Position;
import com.apssouza.mytrade.trading.domain.forex.common.NumberHelper;

import java.math.BigDecimal;
import java.util.Optional;

class ShortPositionStrategy implements CreatorStrategy {

    private final StopOrderConfigDto distanceObject;

    public ShortPositionStrategy(StopOrderConfigDto distanceObject) {
        this.distanceObject = distanceObject;
    }

    @Override
    public BigDecimal getEntryStopPrice(Position position, BigDecimal priceClose) {
        BigDecimal stopPrice = null;
        if (priceClose.compareTo(position.getInitPrice().subtract(distanceObject.entryStopDistance())) < 0) {
            stopPrice = position.getInitPrice();
        }
        return stopPrice;
    }

    @Override
    public BigDecimal getProfitStopPrice(Position position) {
        return position.getInitPrice().subtract(distanceObject.takeProfitDistance());
    }

    @Override
    public BigDecimal getHardStopPrice(Position position) {
        return position.getInitPrice().add(distanceObject.hardStopDistance());
    }

    @Override
    public Optional<BigDecimal> getTrailingStopPrice(Position position, BigDecimal last_close) {
        BigDecimal stopPrice;
        BigDecimal tsPrice = position.getInitPrice().subtract(distanceObject.traillingStopDistance());
        if (last_close.compareTo(tsPrice) < 0) {
            return Optional.empty();
        }
        if (position.getPlacedStopLoss() == null){
            return Optional.empty();
        }
        if (!position.getPlacedStopLoss().type().equals(StopOrderDto.StopOrderType.TRAILLING_STOP)) {
            stopPrice = last_close.add(distanceObject.traillingStopDistance());
        } else {
            stopPrice = position.getPlacedStopLoss().price().subtract(distanceObject.traillingStopDistance());
            stopPrice = position.getPlacedStopLoss().price().compareTo(stopPrice) < 0 ? position.getPlacedStopLoss().price() : stopPrice;
        }
        stopPrice = NumberHelper.roundSymbolPrice(position.getSymbol(), stopPrice);
        return Optional.of(stopPrice);
    }
}
