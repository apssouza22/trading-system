package com.apssouza.mytrade.trading.forex.risk.stoporder;

import com.apssouza.mytrade.trading.forex.portfolio.Position;
import com.apssouza.mytrade.trading.forex.common.NumberHelper;

import java.math.BigDecimal;
import java.util.Optional;

class LongPositionStrategy implements CreatorStrategy {
    private final StopOrderConfigDto distanceObject;

    public LongPositionStrategy(StopOrderConfigDto distanceObject) {
        this.distanceObject = distanceObject;
    }

    @Override
    public BigDecimal getHardStopPrice(Position position) {
        return position.getInitPrice().subtract(distanceObject.getHardStopDistance());
    }

    @Override
    public BigDecimal getEntryStopPrice(Position position, BigDecimal priceClose) {
        BigDecimal stopPrice = null;
        if (priceClose.compareTo(position.getInitPrice().add(distanceObject.getEntryStopDistance())) > 0) {
            stopPrice = position.getInitPrice();
        }
        return stopPrice;
    }

    @Override
    public BigDecimal getProfitStopPrice(Position position) {
        return position.getInitPrice().add(distanceObject.getTakeProfitDistance());
    }

    @Override
    public Optional<BigDecimal> getTrailingStopPrice(Position position, BigDecimal last_close) {
        BigDecimal stopPrice = null;
        //           if price is high enough to warrant creating trailing stop loss:
        BigDecimal tsPrice = position.getInitPrice().add(distanceObject.getTraillingStopDistance());
        if (last_close.compareTo(tsPrice) > 0) {
            return Optional.empty();
        }
        if (position.getPlacedStopLoss() == null){
            return Optional.empty();
        }
        if (!position.getPlacedStopLoss().getType().equals(com.apssouza.mytrade.trading.forex.order.StopOrderDto.StopOrderType.TRAILLING_STOP)) {
            stopPrice = last_close.subtract(distanceObject.getTraillingStopDistance());
        } else {
            stopPrice = position.getPlacedStopLoss().getPrice().subtract(distanceObject.getTraillingStopDistance());
            stopPrice = position.getPlacedStopLoss().getPrice().compareTo(stopPrice) > 0 ? position.getPlacedStopLoss().getPrice() : stopPrice;
        }
        stopPrice = NumberHelper.roundSymbolPrice(position.getSymbol(), stopPrice);
        return Optional.of(stopPrice);
    }

}
