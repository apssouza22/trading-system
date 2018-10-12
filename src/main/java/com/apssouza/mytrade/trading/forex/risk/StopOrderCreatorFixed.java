package com.apssouza.mytrade.trading.forex.risk;

import com.apssouza.mytrade.feed.price.PriceDto;
import com.apssouza.mytrade.trading.forex.order.OrderAction;
import com.apssouza.mytrade.trading.forex.order.StopOrderDto;
import com.apssouza.mytrade.trading.forex.order.StopOrderStatus;
import com.apssouza.mytrade.trading.forex.order.StopOrderType;
import com.apssouza.mytrade.trading.forex.portfolio.Position;
import com.apssouza.mytrade.trading.forex.portfolio.PositionType;
import com.apssouza.mytrade.trading.misc.helper.NumberHelper;
import com.apssouza.mytrade.trading.misc.helper.TradingHelper;
import com.apssouza.mytrade.trading.misc.loop.LoopEvent;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

public class StopOrderCreatorFixed implements StopOrderCreator {
    private  BigDecimal hardStopDistance;
    private  BigDecimal takeProfitDistance;
    private  BigDecimal entryStopDistance;
    private  BigDecimal traillingStopDistance;

    public StopOrderCreatorFixed(
            double hardStopDistance,
            double takeProfitDistance,
            double entryStopDistance,
            double traillingStopDistance
    ) {
        this.hardStopDistance = BigDecimal.valueOf(hardStopDistance);
        this.takeProfitDistance = BigDecimal.valueOf(takeProfitDistance);
        this.entryStopDistance = BigDecimal.valueOf(entryStopDistance);
        this.traillingStopDistance = BigDecimal.valueOf(traillingStopDistance);
    }

    @Override
    public StopOrderDto getHardStopLoss(Position position) {
        BigDecimal stopPrice;
        OrderAction action = TradingHelper.getExitOrderActionFromPosition(position);
        if (position.getPositionType() == PositionType.LONG) {
            stopPrice = position.getInitPrice().subtract(hardStopDistance);
        } else {
            stopPrice = position.getInitPrice().add(hardStopDistance);
        }
        return new StopOrderDto(
                StopOrderType.HARD_STOP,
                null,
                StopOrderStatus.CREATED,
                action,
                NumberHelper.roundSymbolPrice(position.getSymbol(), stopPrice),
                null,
                position.getSymbol(),
                position.getQuantity(),
                position.getIdentifier()
        );
    }

    @Override
    public StopOrderDto getProfitStopOrder(Position position) {
        BigDecimal stopPrice = null;

        OrderAction action = TradingHelper.getExitOrderActionFromPosition(position);
        if (position.getPositionType() == PositionType.LONG) {
            stopPrice = position.getInitPrice().add(takeProfitDistance);
        }
        if (position.getPositionType() == PositionType.SHORT) {
            stopPrice = position.getInitPrice().subtract(takeProfitDistance);
        }
        stopPrice = NumberHelper.roundSymbolPrice(position.getSymbol(), stopPrice);
        return new StopOrderDto(
                StopOrderType.TAKE_PROFIT,
                null,
                StopOrderStatus.CREATED,
                action,
                stopPrice,
                null,
                position.getSymbol(),
                position.getQuantity(),
                position.getIdentifier()
        );
    }

    @Override
    public Optional<StopOrderDto> getEntryStopOrder(Position position, LoopEvent event) {
        Map<String, PriceDto> price = event.getPrice();

        BigDecimal priceClose = price.get(position.getSymbol()).getClose();
        BigDecimal entryPrice = position.getInitPrice();
        OrderAction action = TradingHelper.getExitOrderActionFromPosition(position);
        
        BigDecimal stopPrice = null;
        if (position.getPositionType().equals(PositionType.LONG)) {
            if (priceClose.compareTo(entryPrice.add(entryStopDistance)) > 0) {
                stopPrice = entryPrice;
            }
        }
        if (position.getPositionType().equals(PositionType.SHORT)) {
            if (priceClose.compareTo(entryPrice.subtract(entryStopDistance)) < 0) {
                stopPrice = entryPrice;
            }
        }
        if (stopPrice == null) {
            return Optional.empty();
        }

        return Optional.of(new StopOrderDto(
                StopOrderType.ENTRY_STOP,
                null,
                StopOrderStatus.CREATED,
                action,
                NumberHelper.roundSymbolPrice(position.getSymbol(), stopPrice),
                null,
                position.getSymbol(),
                position.getQuantity(),
                position.getIdentifier()
        ));

    }

    @Override
    public Optional<StopOrderDto> getTrailingStopOrder(Position position, LoopEvent event) {
        Map<String, PriceDto> price = event.getPrice();
        BigDecimal last_close = price.get(position.getSymbol()).getClose();
        OrderAction action = TradingHelper.getExitOrderActionFromPosition(position);

        BigDecimal stopPrice = null;
        if (position.getPositionType().equals(PositionType.LONG)) {
            stopPrice = getLongTraillingStopPrice(position, last_close, traillingStopDistance);
        }
        if (position.getPositionType().equals(PositionType.SHORT)) {
            stopPrice = getShortTrallingStopPrice(position, last_close, traillingStopDistance);
        }

        if (stopPrice == null) {
            return Optional.empty();
        }

        return Optional.of(new StopOrderDto(
                StopOrderType.TRAILLING_STOP,
                null,
                StopOrderStatus.CREATED,
                action,
                stopPrice,
                null,
                position.getSymbol(),
                position.getQuantity(),
                position.getIdentifier()
        ));
    }

    private BigDecimal getLongTraillingStopPrice(Position position, BigDecimal last_close, BigDecimal traillingStopDistance) {
        BigDecimal stopPrice = null;
        //           if price is high enough to warrant creating trailing stop loss:
        if (last_close.compareTo(position.getInitPrice().add(traillingStopDistance)) > 0) {
            return stopPrice;
        }
        if (!position.getPlacedStopLoss().getType().equals(StopOrderType.TRAILLING_STOP)) {
            stopPrice = last_close.subtract(traillingStopDistance);
        } else {
            stopPrice = position.getPlacedStopLoss().getPrice().subtract(traillingStopDistance);
            stopPrice = position.getPlacedStopLoss().getPrice().compareTo(stopPrice) > 0 ? position.getPlacedStopLoss().getPrice() : stopPrice;
        }
        stopPrice = NumberHelper.roundSymbolPrice(position.getSymbol(), stopPrice);
        return stopPrice;
    }

    private BigDecimal getShortTrallingStopPrice(Position position, BigDecimal last_close, BigDecimal traillingStopDistance) {
        BigDecimal stopPrice = null;
        //           if price is low enough to warrant creating trailing stop loss:
        if (last_close.compareTo(position.getInitPrice().subtract(traillingStopDistance)) >= 0) {
            return stopPrice;
        }
        if (!position.getPlacedStopLoss().getType().equals(StopOrderType.TRAILLING_STOP)) {
            stopPrice = last_close.add(traillingStopDistance);
        } else {
            stopPrice = position.getPlacedStopLoss().getPrice().subtract(traillingStopDistance);
            stopPrice = position.getPlacedStopLoss().getPrice().compareTo(stopPrice) < 0 ? position.getPlacedStopLoss().getPrice() : stopPrice;
        }
        stopPrice = NumberHelper.roundSymbolPrice(position.getSymbol(), stopPrice);
        return stopPrice;
    }
}
