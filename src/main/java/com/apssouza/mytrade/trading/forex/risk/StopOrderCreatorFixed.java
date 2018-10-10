package com.apssouza.mytrade.trading.forex.risk;

import com.apssouza.mytrade.feed.price.PriceDto;
import com.apssouza.mytrade.trading.forex.order.OrderAction;
import com.apssouza.mytrade.trading.forex.order.StopOrderDto;
import com.apssouza.mytrade.trading.forex.order.StopOrderStatus;
import com.apssouza.mytrade.trading.forex.order.StopOrderType;
import com.apssouza.mytrade.trading.forex.portfolio.Position;
import com.apssouza.mytrade.trading.forex.portfolio.PositionType;
import com.apssouza.mytrade.trading.misc.helper.NumberHelper;
import com.apssouza.mytrade.trading.misc.helper.config.Properties;
import com.apssouza.mytrade.trading.misc.loop.LoopEvent;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

public class StopOrderCreatorFixed implements StopOrderCreator {
    private final double hardStopDistance;

    public StopOrderCreatorFixed(double hardStopDistance) {
        this.hardStopDistance = hardStopDistance;
    }

    @Override
    public StopOrderDto getHardStopLoss(Position position) {
        BigDecimal stopPrice;
        OrderAction action = OrderAction.BUY;
        if (position.getPositionType() == PositionType.LONG) {
            action = OrderAction.SELL;
        }
        if (position.getPositionType().equals(PositionType.LONG)) {
            stopPrice = position.getInitPrice().subtract(BigDecimal.valueOf(hardStopDistance));
        } else {
            stopPrice = position.getInitPrice().add(BigDecimal.valueOf(hardStopDistance));
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
        BigDecimal profit_stop_distance = BigDecimal.valueOf(Properties.take_profit_distance_fixed);
        BigDecimal stopPrice = null;

        OrderAction action = OrderAction.BUY;
        if (position.getPositionType() == PositionType.LONG) {
            action = OrderAction.SELL;
        }
        if (position.getPositionType().equals(PositionType.LONG)) {
            stopPrice = position.getInitPrice().add(profit_stop_distance);
        }
        if (position.getPositionType().equals(PositionType.SHORT)) {
            stopPrice = position.getInitPrice().subtract(profit_stop_distance);
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

        BigDecimal last_close = price.get(position.getSymbol()).getClose();
        BigDecimal entry_price = position.getInitPrice();
        OrderAction action = OrderAction.BUY;
        if (position.getPositionType() == PositionType.LONG) {
            action = OrderAction.SELL;
        }

        BigDecimal entry_stop_loss_distance = BigDecimal.valueOf(Properties.entry_stop_loss_distance_fixed);
        BigDecimal stopPrice = null;
        if (position.getPositionType().equals(PositionType.LONG)) {
            if (last_close.compareTo(entry_price.add(entry_stop_loss_distance)) > 0) {
                stopPrice = entry_price;
            }
        }
        if (position.getPositionType().equals(PositionType.SHORT)) {
            if (last_close.compareTo(entry_price.subtract(entry_stop_loss_distance)) < 0) {
                stopPrice = entry_price;
            }
        }
        if (stopPrice == null) {
            return Optional.empty();
        }

        return Optional.of(new StopOrderDto(
                StopOrderType.TAKE_PROFIT,
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
        OrderAction action = OrderAction.BUY;
        if (position.getPositionType() == PositionType.LONG) {
            action = OrderAction.SELL;
        }
        BigDecimal trailing_stop_loss_distance = BigDecimal.valueOf(Properties.trailing_stop_loss_distance);

        BigDecimal stopPrice = null;
        if (position.getPositionType().equals(PositionType.LONG)) {
            stopPrice = getLongTraillingStopPrice(position, last_close, trailing_stop_loss_distance);
        }
        if (position.getPositionType().equals(PositionType.SHORT)) {
            stopPrice = getShortTrallingStopPrice(position, last_close, trailing_stop_loss_distance);
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

    private BigDecimal getLongTraillingStopPrice(Position position, BigDecimal last_close, BigDecimal trailing_stop_loss_distance) {
        BigDecimal stopPrice = null;
        //           if price is high enough to warrant creating trailing stop loss:
        if (last_close.compareTo(position.getInitPrice().add(trailing_stop_loss_distance)) > 0) {
            return stopPrice;
        }
        if (!position.getPlacedStopLoss().getType().equals(StopOrderType.TRAILLING_STOP)) {
            stopPrice = last_close.subtract(trailing_stop_loss_distance);
        } else {
            stopPrice = position.getPlacedStopLoss().getPrice().subtract(trailing_stop_loss_distance);
            stopPrice = position.getPlacedStopLoss().getPrice().compareTo(stopPrice) > 0 ? position.getPlacedStopLoss().getPrice() : stopPrice;
        }
        stopPrice = NumberHelper.roundSymbolPrice(position.getSymbol(), stopPrice);
        return stopPrice;
    }

    private BigDecimal getShortTrallingStopPrice(Position position, BigDecimal last_close, BigDecimal trailing_stop_loss_distance) {
        BigDecimal stopPrice = null;
        //           if price is low enough to warrant creating trailing stop loss:
        if (last_close.compareTo(position.getInitPrice().subtract(trailing_stop_loss_distance)) >= 0) {
            return stopPrice;
        }
        if (!position.getPlacedStopLoss().getType().equals(StopOrderType.TRAILLING_STOP)) {
            stopPrice = last_close.add(trailing_stop_loss_distance);
        } else {
            stopPrice = position.getPlacedStopLoss().getPrice().subtract(trailing_stop_loss_distance);
            stopPrice = position.getPlacedStopLoss().getPrice().compareTo(stopPrice) < 0 ? position.getPlacedStopLoss().getPrice() : stopPrice;
        }
        stopPrice = NumberHelper.roundSymbolPrice(position.getSymbol(), stopPrice);
        return stopPrice;
    }
}
