package com.apssouza.mytrade.trading.domain.forex.portfolio;

import com.apssouza.mytrade.trading.domain.forex.common.NumberHelper;
import com.apssouza.mytrade.trading.domain.forex.order.OrderDto;
import com.apssouza.mytrade.trading.domain.forex.risk.stopordercreation.StopOrderDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.EnumMap;

public record PositionDto(
        PositionType positionType,
        String symbol,
        int quantity,
        BigDecimal initPrice,
        LocalDateTime timestamp,
        String identifier,
        FilledOrderDto filledOrder,
        ExitReason exitReason,
        PositionStatus status,
        BigDecimal currentPrice,
        BigDecimal avgPrice,
        EnumMap<StopOrderDto.StopOrderType, StopOrderDto> stopOrders
) {

    public PositionDto {
       currentPrice = NumberHelper.roundSymbolPrice(symbol, initPrice);
    }


    public PositionDto(PositionDto position, EnumMap<StopOrderDto.StopOrderType, StopOrderDto> stopOrders) {
        this(
                position.positionType(),
                position.symbol(),
                position.quantity(),
                position.initPrice(),
                position.timestamp(),
                position.identifier(),
                position.filledOrder(),
                position.exitReason(),
                position.status(),
                position.currentPrice(),
                position.avgPrice(),
                stopOrders
        );
    }


    public PositionDto(PositionDto position, int qtd, BigDecimal price, BigDecimal avgPrice) {
        this(
                position.positionType(),
                position.symbol(),
                qtd,
                price,
                position.timestamp(),
                position.identifier(),
                position.filledOrder(),
                position.exitReason(),
                position.status(),
                position.currentPrice(),
                avgPrice,
                position.stopOrders()
        );
    }

    public boolean isPositionAlive() {
        return status == PositionDto.PositionStatus.OPEN || status == PositionDto.PositionStatus.FILLED;
    }


    public StopOrderDto getPlacedStopLoss() {
        return stopOrders.get(StopOrderDto.StopOrderType.STOP_LOSS);
    }

    public enum PositionType {
        LONG, SHORT;

        public OrderDto.OrderAction getOrderAction() {
            if (this == LONG) {
                return OrderDto.OrderAction.BUY;
            }
            return OrderDto.OrderAction.SELL;
        }
    }

    public enum PositionStatus {
        OPEN, FILLED, CANCELLED, CLOSED
    }

    public enum ExitReason {
        STOP_ORDER_FILLED, COUNTER_SIGNAL, RECONCILIATION_FAILED, END_OF_DAY, PORTFOLIO_EXCEPTION;
    }
}
