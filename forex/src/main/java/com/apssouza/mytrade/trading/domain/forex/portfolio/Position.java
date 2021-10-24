package com.apssouza.mytrade.trading.domain.forex.portfolio;

import com.apssouza.mytrade.trading.domain.forex.common.Symbol;
import com.apssouza.mytrade.trading.domain.forex.order.OrderDto;
import com.apssouza.mytrade.trading.domain.forex.risk.stopordercreation.StopOrderDto;
import com.apssouza.mytrade.trading.domain.forex.common.NumberHelper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.EnumMap;
import static java.math.BigDecimal.valueOf;

public class Position {

    private final PositionType positionType;
    private final String symbol;
    private int quantity;
    private final BigDecimal initPrice;
    private final LocalDateTime timestamp;
    private final String identifier;
    private final FilledOrderDto filledOrder;
    private BigDecimal avgPrice;
    private ExitReason exitReason;
    private PositionStatus status;
    private BigDecimal currentPrice;
    private int id = 0;

    private EnumMap<StopOrderDto.StopOrderType, StopOrderDto> stopOrders;

    public Position(
            PositionType positionType,
            String symbol,
            int quantity,
            BigDecimal initPrice,
            LocalDateTime timestamp,
            String identifier,
            FilledOrderDto filledOrder,
            ExitReason exitReason,
            PositionStatus status
    ) {
        this.positionType = positionType;
        this.symbol = symbol;
        this.quantity = quantity;
        this.initPrice = initPrice;
        this.timestamp = timestamp;
        this.identifier = identifier;
        this.filledOrder = filledOrder;
        this.exitReason = exitReason;
        this.status = status;
        this.currentPrice = NumberHelper.roundSymbolPrice(symbol, initPrice);
        this.avgPrice = initPrice;
        this.stopOrders = new EnumMap<>(StopOrderDto.StopOrderType.class);
    }


    public Position(Position position, EnumMap<StopOrderDto.StopOrderType, StopOrderDto> stopOrders) {
        this(
                position.getPositionType(),
                position.getSymbol(),
                position.getQuantity(),
                position.getInitPrice(),
                position.getTimestamp(),
                position.getIdentifier(),
                position.getFilledOrder(),
                position.getExitReason(),
                position.getStatus()
        );
        this.stopOrders = stopOrders;
    }


    public Position(Position position, int qtd, BigDecimal price, BigDecimal avgPrice) {
        this(
                position.getPositionType(),
                position.getSymbol(),
                position.getQuantity(),
                price,
                position.getTimestamp(),
                position.getIdentifier(),
                position.getFilledOrder(),
                position.getExitReason(),
                position.getStatus()
        );
        this.stopOrders = position.stopOrders;
        this.avgPrice = avgPrice;
    }

    public Position closePosition(ExitReason exit_reason) {
        Position position = new Position(this, stopOrders);
        position.status = PositionStatus.CLOSED;
        position.exitReason = exit_reason;
        return position;
    }

    public String getSymbol() {
        return symbol;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getInitPrice() {
        return initPrice;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getIdentifier() {
        return identifier;
    }

    public FilledOrderDto getFilledOrder() {
        return filledOrder;
    }

    public ExitReason getExitReason() {
        return exitReason;
    }

    public PositionStatus getStatus() {
        return status;
    }

    public boolean isPositionAlive() {
        return status == Position.PositionStatus.OPEN || status == Position.PositionStatus.FILLED;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public BigDecimal getAvgPrice() {
        return avgPrice;
    }

    public int getId() {
        return id;
    }

    public EnumMap<StopOrderDto.StopOrderType, StopOrderDto> getStopOrders() {
        return stopOrders;
    }

    public StopOrderDto getPlacedStopLoss() {
        return stopOrders.get(StopOrderDto.StopOrderType.STOP_LOSS);
    }

    public PositionType getPositionType() {
        return positionType;
    }

    @Override
    public String toString() {
        return "Position{" +
                "positionType=" + positionType +
                ", symbol='" + symbol + '\'' +
                ", quantity=" + quantity +
                ", initPrice=" + initPrice +
                '}';
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
