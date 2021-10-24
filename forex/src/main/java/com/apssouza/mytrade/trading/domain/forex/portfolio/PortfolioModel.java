package com.apssouza.mytrade.trading.domain.forex.portfolio;

import com.apssouza.mytrade.feed.api.PriceDto;
import com.apssouza.mytrade.trading.domain.forex.common.Symbol;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import static java.math.BigDecimal.valueOf;

public class PortfolioModel {
    private final BigDecimal equity;
    private Map<String, Position> positions = new ConcurrentHashMap<>();
    private static Logger log = Logger.getLogger(PortfolioModel.class.getName());

    public PortfolioModel(BigDecimal equity) {
        this.equity = equity;
    }

    public void updatePortfolioBalance(Map<String, PriceDto> price) {
        for (Map.Entry<String, Position> entry : this.positions.entrySet()) {
            Position ps = entry.getValue();
            PriceDto priceDto = price.get(ps.getSymbol());
            entry.setValue(new Position(ps, ps.getQuantity(), priceDto.close(), ps.getAvgPrice()));
        }
    }

    public Map<String, Position> getPositions() {
        return positions;
    }

    public Map<String, Position> getOpenPositions() {
        return positions.entrySet()
                .stream()
                .filter(entry -> entry.getValue().isPositionAlive())
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue()));
    }

    public void addNewPosition(Position ps) {
        this.positions.put(ps.getIdentifier(), ps);
    }


    public Position addPositionQtd(String identifier, int qtd, BigDecimal price) throws PortfolioException {
        if (!this.positions.containsKey(identifier)) {
            throw new PortfolioException("Position not found");
        }
        var ps = this.positions.get(identifier);
        var newQuantity = ps.getQuantity() + qtd;
        var newCost = ps.getCurrentPrice().multiply(valueOf(qtd));
        var newTotalCost = ps.getAvgPrice().add(newCost);
        int pipScale = Symbol.valueOf(ps.getSymbol()).getPipScale();
        var avgPrice = newTotalCost.divide(valueOf(newQuantity), pipScale, RoundingMode.HALF_UP);
        var newPosition = new Position(ps, newQuantity, price, avgPrice);
        this.positions.put(identifier, newPosition);
        return newPosition;
    }

    public boolean removePositionQtd(String identfier, int qtd) throws PortfolioException {
        if (!this.positions.containsKey(identfier)) {
            throw new RuntimeException("Position not found");
        }
        Position ps = this.positions.get(identfier);
        var position = addPositionQtd(identfier, -qtd, ps.getAvgPrice());
        if (position.getQuantity() == 0) {
            closePosition(position.getIdentifier(), Position.ExitReason.STOP_ORDER_FILLED);
        }
        return true;

    }

    public boolean closePosition(String identifier, Position.ExitReason reason) {
        if (!this.positions.containsKey(identifier)) {
            throw new RuntimeException("Position not found");
        }
        Position ps = this.positions.get(identifier);
        ps.closePosition(reason);
        this.positions.remove(identifier);
        log.info(String.format("Position closed - %s %s  ", ps.getIdentifier(), ps.getQuantity()));
        return true;
    }


    public Position getPosition(String identifier) {
        if (!this.positions.containsKey(identifier)) {
            throw new RuntimeException(String.format("Position %s not found", identifier));
        }
        return this.positions.get(identifier);
    }

    public void printPortfolio() {
        positions.entrySet().forEach(entry -> log.info(entry.getValue().toString()));
    }
}
