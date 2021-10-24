package com.apssouza.mytrade.trading.domain.forex.portfolio;

import com.apssouza.mytrade.feed.api.PriceDto;
import com.apssouza.mytrade.trading.domain.forex.common.Symbol;
import com.apssouza.mytrade.trading.domain.forex.risk.stopordercreation.StopOrderDto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import static java.math.BigDecimal.valueOf;

public class PortfolioModel {
    private final BigDecimal equity;
    private Map<String, PositionDto> positions = new ConcurrentHashMap<>();
    private static Logger log = Logger.getLogger(PortfolioModel.class.getName());

    public PortfolioModel(BigDecimal equity) {
        this.equity = equity;
    }

    public void updatePortfolioBalance(Map<String, PriceDto> price) {
        for (Map.Entry<String, PositionDto> entry : this.positions.entrySet()) {
            PositionDto ps = entry.getValue();
            PriceDto priceDto = price.get(ps.symbol());
            entry.setValue(new PositionDto(ps, ps.quantity(), priceDto.close(), ps.avgPrice()));
        }
    }

    public Map<String, PositionDto> getPositions() {
        return positions;
    }

    public Map<String, PositionDto> getOpenPositions() {
        return positions.entrySet()
                .stream()
                .filter(entry -> entry.getValue().isPositionAlive())
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue()));
    }

    public PositionDto addPositionQtd(String identifier, int qtd, BigDecimal price) throws PortfolioException {
        if (!this.positions.containsKey(identifier)) {
            throw new PortfolioException("Position not found");
        }
        var ps = this.positions.get(identifier);
        var newQuantity = ps.quantity() + qtd;
        var newCost = ps.currentPrice().multiply(valueOf(qtd));
        var newTotalCost = ps.avgPrice().add(newCost);
        int pipScale = Symbol.valueOf(ps.symbol()).getPipScale();
        var avgPrice = newTotalCost.divide(valueOf(newQuantity), pipScale, RoundingMode.HALF_UP);
        var newPosition = new PositionDto(ps, newQuantity, price, avgPrice);
        this.positions.put(identifier, newPosition);
        return newPosition;
    }

    public boolean removePositionQtd(String identfier, int qtd) throws PortfolioException {
        if (!this.positions.containsKey(identfier)) {
            throw new RuntimeException("Position not found");
        }
        PositionDto ps = this.positions.get(identfier);
        var position = addPositionQtd(identfier, -qtd, ps.avgPrice());
        if (position.quantity() == 0) {
            closePosition(position.identifier(), PositionDto.ExitReason.STOP_ORDER_FILLED);
        }
        return true;

    }

    public boolean closePosition(String identifier, PositionDto.ExitReason reason) {
        if (!this.positions.containsKey(identifier)) {
            throw new RuntimeException("Position not found");
        }
        PositionDto position = this.positions.get(identifier);
        var ps = new PositionDto(
                position.positionType(),
                position.symbol(),
                position.quantity(),
                position.initPrice(),
                position.timestamp(),
                position.identifier(),
                position.filledOrder(),
                reason,
                PositionDto.PositionStatus.CLOSED,
                position.currentPrice(),
                position.avgPrice(),
                position.stopOrders()
        );
        this.positions.remove(identifier);
        log.info(String.format("Position closed - %s %s  ", ps.identifier(), ps.quantity()));
        return true;
    }


    public PositionDto getPosition(String identifier) {
        if (!this.positions.containsKey(identifier)) {
            throw new RuntimeException(String.format("Position %s not found", identifier));
        }
        return this.positions.get(identifier);
    }

    public void printPortfolio() {
        positions.entrySet().forEach(entry -> log.info(entry.getValue().toString()));
    }

    public PositionDto addNewPosition(PositionDto.PositionType position_type, FilledOrderDto filledOrder) {
        var ps = new PositionDto(
                position_type,
                filledOrder.symbol(),
                filledOrder.quantity(),
                filledOrder.priceWithSpread(),
                filledOrder.time(),
                filledOrder.identifier(),
                filledOrder,
                null,
                PositionDto.PositionStatus.FILLED,
                filledOrder.priceWithSpread(),
                filledOrder.priceWithSpread(),
                new EnumMap<>(StopOrderDto.StopOrderType.class)
        );
        this.positions.put(ps.identifier(), ps);
        return ps;
    }
}
