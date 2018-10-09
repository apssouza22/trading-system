package com.apssouza.mytrade.trading.forex.portfolio;

import com.apssouza.mytrade.trading.forex.order.StopOrderDto;
import com.apssouza.mytrade.trading.forex.order.StopOrderType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

    EnumMap<StopOrderType,StopOrderDto> stopOrders = new EnumMap<>(StopOrderType.class);
    private StopOrderDto placedStopLoss;

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
        this.placedStopLoss = null;
    }



    public Position(Position position, EnumMap<StopOrderType,StopOrderDto> stopOrders) {
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
        if(this.stopOrders.containsKey(StopOrderType.TAKE_PROFIT)) {
            return this.stopOrders.get(StopOrderType.TAKE_PROFIT);
        }
        return null;
    }

    public void setPlacedStopLoss(StopOrderDto stopLoss) {
        this.placedStopLoss = stopLoss;
    }


    public void updatePositionPrice(BigDecimal price) {
        this.currentPrice = price;
    }

    public void addQuantity(int qtd, BigDecimal price) {
        int newQuantity = this.quantity + qtd;
        BigDecimal newCost = this.currentPrice
                .multiply(BigDecimal.valueOf(qtd))
                .add(BigDecimal.valueOf(this.quantity));
        BigDecimal oldCost = this.avgPrice.multiply(BigDecimal.valueOf(this.quantity));
        BigDecimal newTotalCost = oldCost.add(newCost);

        this.avgPrice = newTotalCost.divide(BigDecimal.valueOf(newQuantity));
        this.quantity = newQuantity;
        this.updatePositionPrice(price);
    }

    public void removeUnits(int qtd) {
        int dec_units = qtd;
        this.quantity -= dec_units;
    }

    public void closePosition(ExitReason exit_reason) {
        this.status = PositionStatus.CLOSED;
        if (exit_reason != null) {
            this.exitReason = exit_reason;
        }


    }

    public String getSymbol() {
        return symbol;
    }

    public int getQuantity() {
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

    public EnumMap<StopOrderType,StopOrderDto> getStopOrders() {
        return stopOrders;
    }

    public StopOrderDto getPlacedStopLoss() {
        return placedStopLoss;
    }

    public PositionType getPositionType() {
        return positionType;
    }
}
