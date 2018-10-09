package com.apssouza.mytrade.trading.forex.risk;

import com.apssouza.mytrade.feed.price.PriceDto;
import com.apssouza.mytrade.trading.forex.order.OrderAction;
import com.apssouza.mytrade.trading.forex.order.StopOrderDto;
import com.apssouza.mytrade.trading.forex.order.StopOrderStatus;
import com.apssouza.mytrade.trading.forex.order.StopOrderType;
import com.apssouza.mytrade.trading.forex.portfolio.Position;
import com.apssouza.mytrade.trading.forex.portfolio.PositionType;
import com.apssouza.mytrade.trading.misc.helper.config.Properties;
import com.apssouza.mytrade.trading.misc.loop.LoopEvent;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
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
            BigDecimal bd = position.getInitPrice().subtract(BigDecimal.valueOf(hardStopDistance));
            MathContext mc = new MathContext(
                    Properties.currency_pair_significant_digits_in_price.get(position.getSymbol()),
                    RoundingMode.HALF_UP
            );
            stopPrice = bd.round(mc);
        } else {
            BigDecimal bd = position.getInitPrice().add(BigDecimal.valueOf(hardStopDistance));
            MathContext mc = new MathContext(
                    Properties.currency_pair_significant_digits_in_price.get(position.getSymbol()),
                    RoundingMode.HALF_UP
            );
            stopPrice = bd.round(mc);
        }
        return new StopOrderDto(
                StopOrderType.HARD_STOP,
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
    public StopOrderDto getProfitStopOrder(Position position) {
        BigDecimal profit_stop_distance = BigDecimal.valueOf(Properties.take_profit_distance_fixed);
        BigDecimal stopPrice =  null;
        MathContext mc = new MathContext(
                Properties.currency_pair_significant_digits_in_price.get(position.getSymbol()),
                RoundingMode.HALF_UP
        );

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
        stopPrice = stopPrice.round(mc);
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
        BigDecimal entry_stop_loss_price = null;
        if (position.getPositionType().equals(PositionType.LONG)){
            if (last_close.compareTo(entry_price.add(entry_stop_loss_distance)) > 0) {
                entry_stop_loss_price = entry_price;
            }
        }
        if (position.getPositionType().equals(PositionType.SHORT)) {
            if (last_close.compareTo(entry_price.subtract(entry_stop_loss_distance)) < 0) {
                entry_stop_loss_price = entry_price;
            }
        }
        if (entry_stop_loss_price == null){
            return  Optional.empty();
        }

        MathContext mc = new MathContext(
                Properties.currency_pair_significant_digits_in_price.get(position.getSymbol()),
                RoundingMode.HALF_UP
        );

        return Optional.of(new StopOrderDto(
                StopOrderType.TAKE_PROFIT,
                null,
                StopOrderStatus.CREATED,
                action,
                entry_stop_loss_price.round(mc),
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
        BigDecimal entry_price = position.getInitPrice();
        OrderAction action = OrderAction.BUY;
        if (position.getPositionType() == PositionType.LONG) {
            action = OrderAction.SELL;
        }
        BigDecimal trailing_stop_loss_distance = BigDecimal.valueOf(Properties.trailing_stop_loss_distance);
        MathContext mc = new MathContext(
                Properties.currency_pair_significant_digits_in_price.get(position.getSymbol()),
                RoundingMode.HALF_UP
        );

        BigDecimal stopPrice = null;
        if (position.getPositionType().equals(PositionType.LONG)) {
//           if price is high enough to warrant creating trailing stop loss:
            if (last_close.compareTo(entry_price.add(trailing_stop_loss_distance)) > 0) {
//              if no TSL already exists, create one at the parameter specified distance:
                if (!position.getPlacedStopLoss().getType().equals(StopOrderType.TRAILLING_STOP)) {
                    stopPrice = last_close.subtract(trailing_stop_loss_distance);
                } else {
                    stopPrice = position.getPlacedStopLoss().getPrice().subtract(trailing_stop_loss_distance);
                    stopPrice = position.getPlacedStopLoss().getPrice().compareTo(stopPrice) > 0 ? position.getPlacedStopLoss().getPrice() : stopPrice;
                }
                stopPrice = stopPrice.round(mc);
            }
        }
        if (position.getPositionType().equals(PositionType.SHORT)) {
//           if price is low enough to warrant creating trailing stop loss:
            if (last_close.compareTo(entry_price.subtract(trailing_stop_loss_distance)) < 0) {
                if (!position.getPlacedStopLoss().getType().equals(StopOrderType.TRAILLING_STOP)) {
                    stopPrice = last_close.add(trailing_stop_loss_distance);
                } else {
                    stopPrice= position.getPlacedStopLoss().getPrice().subtract(trailing_stop_loss_distance);
                    stopPrice = position.getPlacedStopLoss().getPrice().compareTo(stopPrice) < 0 ? position.getPlacedStopLoss().getPrice() : stopPrice;
                }
                stopPrice = stopPrice.round(mc);
            }
        }

        if (stopPrice == null){
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
}
