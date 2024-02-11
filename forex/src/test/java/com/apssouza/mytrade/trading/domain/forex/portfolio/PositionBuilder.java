package com.apssouza.mytrade.trading.domain.forex.portfolio;

import com.apssouza.mytrade.trading.domain.forex.riskmanagement.stopordercreation.StopOrderDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.EnumMap;

public class PositionBuilder {

    PositionDto.PositionType type = PositionDto.PositionType.LONG;
    String symbol = "AUDUSD";
    Integer qtd = 1000;
    BigDecimal price = BigDecimal.valueOf(1.004);
    LocalDateTime timestamp = LocalDateTime.MIN;
    String identifier = "AUDUSD";
    PositionDto.ExitReason exitReason = null;
    FilledOrderDto filledOrder = new FilledOrderDto(timestamp, symbol, null,qtd, price,identifier, 1);
    PositionDto.PositionStatus positionStatus = PositionDto.PositionStatus.FILLED;
    private EnumMap<StopOrderDto.StopOrderType, StopOrderDto> stopOrders = new EnumMap(StopOrderDto.StopOrderType.class);

    public void withType(PositionDto.PositionType type) {
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

    public void withExitReason(PositionDto.ExitReason exitReason) {
        this.exitReason = exitReason;
    }

    public void withFilledOrder(FilledOrderDto filledOrder) {
        this.filledOrder = filledOrder;
    }

    public PositionBuilder withPositionStatus(PositionDto.PositionStatus positionStatus) {
        this.positionStatus = positionStatus;
        return this;
    }

    public PositionDto build() {

        PositionDto position = new PositionDto(
                type,
                symbol,
                qtd,
                price,
                timestamp,
                identifier,
                filledOrder,
                exitReason,
                positionStatus,
                price,
                price,
                null
        );

        if (stopOrders.isEmpty()) {
            return position;
        }
        return new PositionDto(position, stopOrders);
    }


    public void addStopOrder(StopOrderDto stopOrder) {
        this.stopOrders.put(stopOrder.type(), stopOrder);
    }
}
