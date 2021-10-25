package com.apssouza.mytrade.trading.domain.forex.portfolio;

import com.apssouza.mytrade.feed.api.PriceDto;
import com.apssouza.mytrade.trading.domain.forex.risk.stopordercreation.StopOrderDto;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class PortfolioModel {
    private final BigDecimal equity;
    private PositionCollection positions = new PositionCollection();
    private static Logger log = Logger.getLogger(PortfolioModel.class.getName());

    public PortfolioModel(BigDecimal equity) {
        this.equity = equity;
    }

    public void updatePortfolioBalance(Map<String, PriceDto> price) {
        positions.updateItems(position -> {
            PriceDto priceDto = price.get(position.symbol());
            return new PositionDto(position, position.quantity(), priceDto.close(), position.avgPrice());
        });
    }

    public PositionCollection getPositionCollection() {
        return positions;
    }

    public Map<String, PositionDto> getOpenPositions() {
        return positions.getOpenPositions();
    }

    public PositionDto addPositionQtd(String identifier, int qtd, BigDecimal price) throws PortfolioException {
        if (!this.positions.contains(identifier)) {
            throw new PortfolioException("Position not found");
        }
        var ps = this.positions.get(identifier);
        var avgPrice = ps.getNewAveragePrice(qtd, price);
        var newPosition = new PositionDto(ps, qtd, price, avgPrice);
        this.positions.update(newPosition);
        return newPosition;
    }

    public boolean removePositionQtd(String identfier, int qtd) throws PortfolioException {
        if (!this.positions.contains(identfier)) {
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
        if (!this.positions.contains(identifier)) {
            throw new RuntimeException("Position not found");
        }
        this.positions.remove(identifier);
        log.info(String.format("Position closed - %s %s  ", identifier, reason));
        return true;
    }


    public PositionDto getPosition(String identifier) {
        if (!this.positions.contains(identifier)) {
            throw new RuntimeException(String.format("Position %s not found", identifier));
        }
        return this.positions.get(identifier);
    }

    public void printPortfolio() {
        positions.getOpenPositions().entrySet().forEach(entry -> log.info(entry.getValue().toString()));
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
        this.positions.add(ps);
        return ps;
    }

    public boolean contains(final String identifier) {
        return positions.contains(identifier);
    }

    public List<PositionDto> getPositions() {
        return positions.getPositions();
    }

    public boolean isEmpty() {
        return positions.isEmpty();
    }
}
