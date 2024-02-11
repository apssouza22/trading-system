package com.apssouza.mytrade.trading.domain.forex.riskmanagement.stopordercreation;

import com.apssouza.mytrade.trading.domain.forex.portfolio.PositionDto;
import com.apssouza.mytrade.trading.domain.forex.common.NumberHelper;

import java.math.BigDecimal;
import java.util.Optional;

class LongPositionStrategy implements CreatorStrategy {
    private final StopOrderConfigDto distanceObject;

    public LongPositionStrategy(StopOrderConfigDto distanceObject) {
        this.distanceObject = distanceObject;
    }

    @Override
    public BigDecimal getHardStopPrice(PositionDto position) {
        return position.initPrice().subtract(distanceObject.hardStopDistance());
    }

    @Override
    public BigDecimal getEntryStopPrice(PositionDto position, BigDecimal priceClose) {
        BigDecimal stopPrice = null;
        if (priceClose.compareTo(position.initPrice().add(distanceObject.entryStopDistance())) > 0) {
            stopPrice = position.initPrice();
        }
        return stopPrice;
    }

    @Override
    public BigDecimal getProfitStopPrice(PositionDto position) {
        return position.initPrice().add(distanceObject.takeProfitDistance());
    }

    @Override
    public Optional<BigDecimal> getTrailingStopPrice(PositionDto position, BigDecimal last_close) {
        BigDecimal stopPrice = null;
        //           if price is high enough to warrant creating trailing stop loss:
        BigDecimal tsPrice = position.initPrice().add(distanceObject.traillingStopDistance());
        if (last_close.compareTo(tsPrice) > 0) {
            return Optional.empty();
        }
        if (position.getPlacedStopLoss() == null){
            return Optional.empty();
        }
        if (!position.getPlacedStopLoss().type().equals(StopOrderDto.StopOrderType.TRAILLING_STOP)) {
            stopPrice = last_close.subtract(distanceObject.traillingStopDistance());
        } else {
            stopPrice = position.getPlacedStopLoss().price().subtract(distanceObject.traillingStopDistance());
            stopPrice = position.getPlacedStopLoss().price().compareTo(stopPrice) > 0 ? position.getPlacedStopLoss().price() : stopPrice;
        }
        stopPrice = NumberHelper.roundSymbolPrice(position.symbol(), stopPrice);
        return Optional.of(stopPrice);
    }

}
