package com.apssouza.mytrade.trading.domain.forex.risk.stopordercreation;

import com.apssouza.mytrade.trading.domain.forex.order.OrderDto;
import com.apssouza.mytrade.trading.domain.forex.portfolio.PositionDto;
import com.apssouza.mytrade.trading.domain.forex.common.NumberHelper;
import com.apssouza.mytrade.trading.domain.forex.common.TradingHelper;

import java.math.BigDecimal;
import java.util.Optional;

class CreatorContext {

    private final CreatorStrategy strategy;

    public CreatorContext(CreatorStrategy strategy) {
        this.strategy = strategy;
    }


    public StopOrderDto getHardStopLoss(PositionDto position) {
        OrderDto.OrderAction action = TradingHelper.getExitOrderActionFromPosition(position);
        BigDecimal stopPrice = strategy.getHardStopPrice(position);
        return new StopOrderDto(
                StopOrderDto.StopOrderType.HARD_STOP,
                null,
                StopOrderDto.StopOrderStatus.CREATED,
                action,
                NumberHelper.roundSymbolPrice(position.symbol(), stopPrice),
                null,
                position.symbol(),
                position.quantity(),
                position.identifier()
        );
    }

    public Optional<StopOrderDto> getTrailingStopOrder(PositionDto position, BigDecimal priceClose) {
        OrderDto.OrderAction action = TradingHelper.getExitOrderActionFromPosition(position);
        Optional<BigDecimal> stopPrice = strategy.getTrailingStopPrice(position, priceClose);

        if (!stopPrice.isPresent()) {
            return Optional.empty();
        }

        return Optional.of(new StopOrderDto(
                StopOrderDto.StopOrderType.TRAILLING_STOP,
                null,
                StopOrderDto.StopOrderStatus.CREATED,
                action,
                stopPrice.get(),
                null,
                position.symbol(),
                position.quantity(),
                position.identifier()
        ));
    }

    public Optional<StopOrderDto> getEntryStopOrder(PositionDto position, BigDecimal priceClose) {
        OrderDto.OrderAction action = TradingHelper.getExitOrderActionFromPosition(position);
        BigDecimal stopPrice = strategy.getEntryStopPrice(position, priceClose);

        if (stopPrice == null) {
            return Optional.empty();
        }

        return Optional.of(new StopOrderDto(
                StopOrderDto.StopOrderType.ENTRY_STOP,
                null,
                StopOrderDto.StopOrderStatus.CREATED,
                action,
                NumberHelper.roundSymbolPrice(position.symbol(), stopPrice),
                null,
                position.symbol(),
                position.quantity(),
                position.identifier()
        ));

    }


    public StopOrderDto getProfitStopOrder(PositionDto position) {
        BigDecimal stopPrice = strategy.getProfitStopPrice(position);
        OrderDto.OrderAction action = TradingHelper.getExitOrderActionFromPosition(position);
        stopPrice = NumberHelper.roundSymbolPrice(position.symbol(), stopPrice);
        return new StopOrderDto(
                StopOrderDto.StopOrderType.TAKE_PROFIT,
                null,
                StopOrderDto.StopOrderStatus.CREATED,
                action,
                stopPrice,
                null,
                position.symbol(),
                position.quantity(),
                position.identifier()
        );
    }
}
