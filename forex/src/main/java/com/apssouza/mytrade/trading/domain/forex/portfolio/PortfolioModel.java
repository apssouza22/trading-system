package com.apssouza.mytrade.trading.domain.forex.portfolio;

import com.apssouza.mytrade.feed.api.PriceDto;
import com.apssouza.mytrade.trading.domain.forex.risk.stopordercreation.StopOrderDto;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Logger;
import static java.math.BigDecimal.valueOf;

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

    public Map<String, PositionDto> getPositions() {
        return positions.getPositions();
    }

    public Map<String, PositionDto> getOpenPositions() {
        return positions.getOpenPositions();
    }

    public PositionDto addPositionQtd(String identifier, int qtd, BigDecimal price) throws PortfolioException {
        if (!this.positions.getPositions().containsKey(identifier)) {
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
}
