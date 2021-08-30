package com.apssouza.mytrade.trading.builder;

import com.apssouza.mytrade.trading.forex.order.StopOrderDto;
import com.apssouza.mytrade.trading.forex.order.StopOrderType;
import com.apssouza.mytrade.trading.forex.portfolio.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.EnumMap;

public class PositionBuilder {

    Position.PositionType type = Position.PositionType.LONG;
    String symbol = "AUDUSD";
    Integer qtd = 1000;
    BigDecimal price = BigDecimal.valueOf(1.004);
    LocalDateTime timestamp = LocalDateTime.MIN;
    String identifier = "AUDUSD";
    Position.ExitReason exitReason = null;
    FilledOrderDto filledOrder = null;
    Position.PositionStatus positionStatus = Position.PositionStatus.FILLED;
    private EnumMap<StopOrderType, StopOrderDto> stopOrders = new EnumMap(StopOrderType.class);

    public void withType(Position.PositionType type) {
        this.type = type;
    }

    public void withSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void withQtd(Integer qtd) {
        this.qtd = qtd;
    }

    public void withPrice(BigDecimal price) {
        this.price = price;
    }

    public void withTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void withIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void withExitReason(Position.ExitReason exitReason) {
        this.exitReason = exitReason;
    }

    public void withFilledOrder(FilledOrderDto filledOrder) {
        this.filledOrder = filledOrder;
    }

    public void withPositionStatus(Position.PositionStatus positionStatus) {
        this.positionStatus = positionStatus;
    }

    public Position build() {

        Position position = new Position(
                type,
                symbol,
                qtd,
                price,
                timestamp,
                identifier,
                filledOrder,
                exitReason,
                Position.PositionStatus.FILLED
        );

        if (stopOrders.isEmpty()) {
            return position;
        }
        return new Position(position, stopOrders);
    }


    public void addStopOrder(StopOrderDto stopOrder) {
        this.stopOrders.put(stopOrder.getType(), stopOrder);
    }
}
