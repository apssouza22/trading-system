package com.apssouza.mytrade.trading.forex.risk.stoporder.fixed;

import com.apssouza.mytrade.trading.forex.order.StopOrderType;
import com.apssouza.mytrade.trading.forex.portfolio.Position;
import com.apssouza.mytrade.trading.forex.risk.stoporder.PriceDistanceObject;
import com.apssouza.mytrade.trading.misc.helper.NumberHelper;

import java.math.BigDecimal;

class LongPositionStrategy implements CreatorStrategy {
    private final PriceDistanceObject distanceObject;

    public LongPositionStrategy(PriceDistanceObject distanceObject) {
        this.distanceObject = distanceObject;
    }


    public BigDecimal getHardStopPrice(Position position) {
        return position.getInitPrice().subtract(distanceObject.getHardStopDistance());
    }

    public BigDecimal getEntryStopPrice(Position position, BigDecimal priceClose) {
        BigDecimal stopPrice = null;
        if (priceClose.compareTo(position.getInitPrice().add(distanceObject.getEntryStopDistance())) > 0) {
            stopPrice = position.getInitPrice();
        }
        return stopPrice;
    }

    public BigDecimal getProfitStopPrice(Position position) {
        return position.getInitPrice().add(distanceObject.getTakeProfitDistance());
    }

    public BigDecimal getTrailingStopPrice(Position position, BigDecimal last_close) {
        BigDecimal stopPrice = null;
        //           if price is high enough to warrant creating trailing stop loss:
        BigDecimal tsPrice = position.getInitPrice().add(distanceObject.getTraillingStopDistance());
        if (last_close.compareTo(tsPrice) > 0) {
            return stopPrice;
        }
        if (!position.getPlacedStopLoss().getType().equals(StopOrderType.TRAILLING_STOP)) {
            stopPrice = last_close.subtract(distanceObject.getTraillingStopDistance());
        } else {
            stopPrice = position.getPlacedStopLoss().getPrice().subtract(distanceObject.getTraillingStopDistance());
            stopPrice = position.getPlacedStopLoss().getPrice().compareTo(stopPrice) > 0 ? position.getPlacedStopLoss().getPrice() : stopPrice;
        }
        stopPrice = NumberHelper.roundSymbolPrice(position.getSymbol(), stopPrice);
        return stopPrice;
    }

}
