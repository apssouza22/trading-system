package com.apssouza.mytrade.trading.builder;

import com.apssouza.mytrade.trading.forex.order.StopOrderDto;
import com.apssouza.mytrade.trading.forex.order.StopOrderType;
import com.apssouza.mytrade.trading.forex.portfolio.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;

public class PositionBuilder {

    PositionType type = PositionType.LONG;
    String symbol = "AUDUSD";
    Integer qtd = 1000;
    BigDecimal price = BigDecimal.valueOf(1.004);
    LocalDateTime timestamp = LocalDateTime.MIN;
    String identifier = "AUDUSD";
    ExitReason exitReason = null;
    FilledOrderDto filledOrder = null;
    PositionStatus positionStatus = PositionStatus.FILLED;
    private EnumMap<StopOrderType, StopOrderDto> stopOrders = new EnumMap(StopOrderType.class);

    public void setType(PositionType type) {
        this.type = type;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setQtd(Integer qtd) {
        this.qtd = qtd;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setExitReason(ExitReason exitReason) {
        this.exitReason = exitReason;
    }

    public void setFilledOrder(FilledOrderDto filledOrder) {
        this.filledOrder = filledOrder;
    }

    public void setPositionStatus(PositionStatus positionStatus) {
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
                PositionStatus.FILLED
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
