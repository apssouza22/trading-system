package com.apssouza.mytrade.trading.forex.portfolio;

import com.apssouza.mytrade.feed.price.PriceDto;
import com.apssouza.mytrade.feed.price.PriceHandler;
import com.apssouza.mytrade.trading.misc.loop.LoopEvent;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class Portfolio {
    private final PriceHandler priceHandler;
    private final BigDecimal equity;
    private BigDecimal balance;
    private Map<String, Position> positions = new HashMap<>();
    private static Logger log = Logger.getLogger(Portfolio.class.getName());

    public Portfolio(PriceHandler priceHandler, BigDecimal equity) {
        this.priceHandler = priceHandler;
        this.equity = equity;
        this.balance = equity;
    }

    public void updatePortfolioValue(LoopEvent event) {
        for (Map.Entry<String, Position> entry : this.positions.entrySet()) {
            Position ps = entry.getValue();
            PriceDto priceDto = event.getPrice().get(ps.getSymbol());
            ps.updatePositionPrice(priceDto.getClose());
        }
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public Map<String, Position> getPositions() {
        return positions;
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

    public boolean closePosition(String identfier) {
        if (!this.positions.containsKey(identfier)) {
            throw new RuntimeException("Position not found");
        }
        Position ps = this.positions.get(identfier);
        ps.closePosition(null);
        this.positions.remove(identfier);
        log.info(String.format("Position closed - %s %s  ", ps.getIdentifier(), ps.getQuantity()));
        return true;

    }


    public Position getPosition(String identifier) {
        if (!this.positions.containsKey(identifier)) {
            throw new RuntimeException("Position not found");
        }
        return this.positions.get(identifier);
    }

}
