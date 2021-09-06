package com.apssouza.mytrade.trading.domain.forex.portfolio;

import com.apssouza.mytrade.feed.api.PriceDto;
import com.apssouza.mytrade.trading.domain.forex.common.Event;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class PortfolioModel {
    private final BigDecimal equity;
    private Map<String, Position> positions = new ConcurrentHashMap<>();
    private static Logger log = Logger.getLogger(PortfolioModel.class.getName());

    public PortfolioModel(BigDecimal equity) {
        this.equity = equity;
    }

    public void updatePortfolioBalance(Event event) {
        for (Map.Entry<String, Position> entry : this.positions.entrySet()) {
            Position ps = entry.getValue();
            PriceDto priceDto = event.getPrice().get(ps.getSymbol());
            ps.updatePositionPrice(priceDto.close());
        }
    }

    public Map<String, Position> getPositions() {
        return positions;
    }

    public Map<String, Position> getOpenPositions() {
        Map<String, Position> openPositions = new HashMap<>();
        for (Map.Entry<String, Position> entry : positions.entrySet()) {
            if (entry.getValue().getStatus() == Position.PositionStatus.OPEN ||
                    entry.getValue().getStatus() == Position.PositionStatus.FILLED) {
                openPositions.put(entry.getKey(), entry.getValue());
            }
        }
        return openPositions;
    }

    public void addNewPosition(Position ps) {
        this.positions.put(ps.getIdentifier(), ps);
    }


    public boolean addPositionQtd(String identifier, int qtd, BigDecimal price) {
        if (!this.positions.containsKey(identifier)) {
            throw new RuntimeException("Position not found");
        }
        Position ps = this.positions.get(identifier);
        ps.addQuantity(qtd, price);
        return true;

    }

    public boolean removePositionQtd(String identfier, int qtd) {
        if (!this.positions.containsKey(identfier)) {
            throw new RuntimeException("Position not found");
        }
        Position ps = this.positions.get(identfier);
        ps.removeUnits(qtd);
        return true;

    }

    public boolean closePosition(String identifier) {
        if (!this.positions.containsKey(identifier)) {
            throw new RuntimeException("Position not found");
        }
        Position ps = this.positions.get(identifier);
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
