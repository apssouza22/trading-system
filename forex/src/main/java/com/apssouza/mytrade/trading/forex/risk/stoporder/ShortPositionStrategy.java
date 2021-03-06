package com.apssouza.mytrade.trading.forex.risk.stoporder;

import com.apssouza.mytrade.trading.forex.order.StopOrderType;
import com.apssouza.mytrade.trading.forex.portfolio.Position;
import com.apssouza.mytrade.trading.misc.helper.NumberHelper;

import java.math.BigDecimal;
import java.util.Optional;

class ShortPositionStrategy implements CreatorStrategy {

    private final StopOrderDto distanceObject;

    public ShortPositionStrategy(StopOrderDto distanceObject) {
        this.distanceObject = distanceObject;
    }

    public BigDecimal getEntryStopPrice(Position position, BigDecimal priceClose) {
        BigDecimal stopPrice = null;
        if (priceClose.compareTo(position.getInitPrice().subtract(distanceObject.getEntryStopDistance())) < 0) {
            stopPrice = position.getInitPrice();
        }
        return stopPrice;
    }

    public BigDecimal getProfitStopPrice(Position position) {
        return position.getInitPrice().subtract(distanceObject.getTakeProfitDistance());
    }

    public BigDecimal getHardStopPrice(Position position) {
        return position.getInitPrice().add(distanceObject.getHardStopDistance());
    }

    public Optional<BigDecimal> getTrailingStopPrice(Position position, BigDecimal last_close) {
        BigDecimal stopPrice;
        BigDecimal tsPrice = position.getInitPrice().subtract(distanceObject.getTraillingStopDistance());
        if (last_close.compareTo(tsPrice) < 0) {
            return Optional.empty();
        }
        if (position.getPlacedStopLoss() == null){
            return Optional.empty();
        }
        if (!position.getPlacedStopLoss().getType().equals(StopOrderType.TRAILLING_STOP)) {
            stopPrice = last_close.add(distanceObject.getTraillingStopDistance());
        } else {
            stopPrice = position.getPlacedStopLoss().getPrice().subtract(distanceObject.getTraillingStopDistance());
            stopPrice = position.getPlacedStopLoss().getPrice().compareTo(stopPrice) < 0 ? position.getPlacedStopLoss().getPrice() : stopPrice;
        }
        stopPrice = NumberHelper.roundSymbolPrice(position.getSymbol(), stopPrice);
        return Optional.of(stopPrice);
    }
}
