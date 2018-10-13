package com.apssouza.mytrade.trading.forex.risk.stoporder.fixed;

import com.apssouza.mytrade.feed.price.PriceDto;
import com.apssouza.mytrade.trading.forex.order.OrderAction;
import com.apssouza.mytrade.trading.forex.order.StopOrderDto;
import com.apssouza.mytrade.trading.forex.order.StopOrderStatus;
import com.apssouza.mytrade.trading.forex.order.StopOrderType;
import com.apssouza.mytrade.trading.forex.portfolio.Position;
import com.apssouza.mytrade.trading.misc.helper.NumberHelper;
import com.apssouza.mytrade.trading.misc.helper.TradingHelper;
import com.apssouza.mytrade.trading.misc.loop.LoopEvent;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

public class CreatorContext {

    private final CreatorStrategy strategy;

    public CreatorContext(CreatorStrategy strategy) {
        this.strategy = strategy;
    }


    public StopOrderDto getHardStopLoss(Position position) {
        OrderAction action = TradingHelper.getExitOrderActionFromPosition(position);
        BigDecimal stopPrice = strategy.getHardStopPrice(position);
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

    public Optional<StopOrderDto> getTrailingStopOrder(Position position, BigDecimal priceClose) {
        OrderAction action = TradingHelper.getExitOrderActionFromPosition(position);
        BigDecimal stopPrice = strategy.getTrailingStopPrice(position, priceClose);

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

    public Optional<StopOrderDto> getEntryStopOrder(Position position, BigDecimal priceClose) {
        OrderAction action = TradingHelper.getExitOrderActionFromPosition(position);
        BigDecimal stopPrice = strategy.getEntryStopPrice(position, priceClose);

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


    public StopOrderDto getProfitStopOrder(Position position) {
        BigDecimal stopPrice = strategy.getProfitStopPrice(position);
        OrderAction action = TradingHelper.getExitOrderActionFromPosition(position);
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
}
