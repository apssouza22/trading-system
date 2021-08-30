package com.apssouza.mytrade.trading.forex.portfolio;

import com.apssouza.mytrade.trading.forex.common.Symbol;
import com.apssouza.mytrade.trading.forex.order.OrderAction;
import com.apssouza.mytrade.trading.forex.order.StopOrderDto;
import com.apssouza.mytrade.trading.forex.order.StopOrderType;
import com.apssouza.mytrade.trading.forex.common.NumberHelper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.EnumMap;

public class Position {

    private final PositionType positionType;
    private final String symbol;
    private int quantity;
    private final BigDecimal initPrice;
    private final LocalDateTime timestamp;
    private final String identifier;
    private final FilledOrderDto filledOrder;
    private ExitReason exitReason;
    private PositionStatus status;
    private BigDecimal currentPrice;
    private BigDecimal avgPrice;
    private int id = 0;

    EnumMap<StopOrderType, StopOrderDto> stopOrders = new EnumMap<>(StopOrderType.class);

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
        this.currentPrice = initPrice;
        this.avgPrice = initPrice;
    }


    public Position(Position position, EnumMap<StopOrderType, StopOrderDto> stopOrders) {
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

    public StopOrderDto getTakeProfitOrder() {
        if (this.stopOrders.containsKey(StopOrderType.TAKE_PROFIT)) {
            return this.stopOrders.get(StopOrderType.TAKE_PROFIT);
        }
        return null;
    }

    public void updatePositionPrice(BigDecimal price) {
        this.currentPrice = NumberHelper.roundSymbolPrice(symbol, price);
    }

    public void addQuantity(int qtd, BigDecimal price) {
        int newQuantity = this.quantity + qtd;
        BigDecimal newCost = this.currentPrice
                .multiply(BigDecimal.valueOf(qtd));

        BigDecimal oldCost = this.avgPrice.multiply(BigDecimal.valueOf(this.quantity));
        BigDecimal newTotalCost = oldCost.add(newCost);
        int pipScale = Symbol.valueOf(this.symbol).getPipScale();
        this.avgPrice = newTotalCost.divide(BigDecimal.valueOf(newQuantity), pipScale, RoundingMode.HALF_UP);
        this.quantity = newQuantity;
        this.updatePositionPrice(price);
    }

    public void removeUnits(int qtd) {
        int dec_units = qtd;
        this.quantity -= dec_units;
    }

    public Position closePosition(ExitReason exit_reason) {
        Position position = new Position(this, stopOrders);
        position.status = PositionStatus.CLOSED;
        if (exit_reason != null) {
            position.exitReason = exit_reason;
        }
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

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public BigDecimal getAvgPrice() {
        return avgPrice;
    }

    public int getId() {
        return id;
    }

    public EnumMap<StopOrderType, StopOrderDto> getStopOrders() {
        return stopOrders;
    }

    public StopOrderDto getPlacedStopLoss() {
        if (stopOrders.containsKey(StopOrderType.STOP_LOSS)) {
            return stopOrders.get(StopOrderType.STOP_LOSS);
        }
        return null;
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

        public OrderAction getOrderAction(){
            if (this == LONG){
                return OrderAction.BUY;
            }
            return OrderAction.SELL;
        }
    }

    public enum PositionStatus {
        OPEN, FILLED, CANCELLED, CLOSED
    }

    public enum ExitReason {
        STOP_ORDER_FILLED, COUNTER_SIGNAL, RECONCILIATION_FAILED, END_OF_DAY
    }
}
