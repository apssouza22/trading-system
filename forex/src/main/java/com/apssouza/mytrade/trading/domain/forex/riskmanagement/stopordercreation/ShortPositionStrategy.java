package com.apssouza.mytrade.trading.domain.forex.riskmanagement.stopordercreation;

import com.apssouza.mytrade.trading.domain.forex.portfolio.PositionDto;
import com.apssouza.mytrade.trading.domain.forex.common.NumberHelper;

import java.math.BigDecimal;
import java.util.Optional;

class ShortPositionStrategy implements CreatorStrategy {

    private final StopOrderConfigDto distanceObject;

    public ShortPositionStrategy(StopOrderConfigDto distanceObject) {
        this.distanceObject = distanceObject;
    }

    @Override
    public BigDecimal getEntryStopPrice(PositionDto position, BigDecimal priceClose) {
        BigDecimal stopPrice = null;
        if (priceClose.compareTo(position.initPrice().subtract(distanceObject.entryStopDistance())) < 0) {
            stopPrice = position.initPrice();
        }
        return stopPrice;
    }

    @Override
    public BigDecimal getProfitStopPrice(PositionDto position) {
        return position.initPrice().subtract(distanceObject.takeProfitDistance());
    }

    @Override
    public BigDecimal getHardStopPrice(PositionDto position) {
        return position.initPrice().add(distanceObject.hardStopDistance());
    }

    @Override
    public Optional<BigDecimal> getTrailingStopPrice(PositionDto position, BigDecimal last_close) {
        BigDecimal stopPrice;
        BigDecimal tsPrice = position.initPrice().subtract(distanceObject.traillingStopDistance());
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
        stopPrice = NumberHelper.roundSymbolPrice(position.symbol(), stopPrice);
        return Optional.of(stopPrice);
    }
}
