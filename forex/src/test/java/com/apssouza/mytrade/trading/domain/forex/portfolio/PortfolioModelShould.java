package com.apssouza.mytrade.trading.domain.forex.portfolio;

import com.apssouza.mytrade.trading.domain.forex.session.LoopEventBuilder;
import com.apssouza.mytrade.trading.domain.forex.feed.pricefeed.PriceChangedEvent;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class PortfolioModelShould extends TestCase {

    @Test
    public void updatePortfolioValue() {
        PortfolioModel portfolio = new PortfolioModel(BigDecimal.valueOf(10000));
        PositionBuilder positionBuilder = new PositionBuilder();
        PositionDto ps = positionBuilder.build();
        LoopEventBuilder loopEventBuilder = new LoopEventBuilder();
        loopEventBuilder.withPriceMap(BigDecimal.valueOf(10));
        PriceChangedEvent loopEvent = loopEventBuilder.build();
        portfolio.addNewPosition(ps.positionType(), ps.filledOrder());
        portfolio.updatePortfolioBalance(loopEvent.getPrice());

        Map<String, PositionDto> positions = portfolio.getPositions();

        BigDecimal currentPrice = positions.get(ps.identifier()).currentPrice();
        assertFalse(ps.initPrice().equals(currentPrice));
    }


    @Test
    public void returnPositions() {
        PortfolioModel portfolio = new PortfolioModel(BigDecimal.valueOf(10000));
        PositionDto ps = new PositionBuilder().build();
        portfolio.addNewPosition(ps.positionType(), ps.filledOrder());
        Map<String, PositionDto> positions = portfolio.getPositions();
        assertEquals(1, positions.size());
    }

    @Test
    public void addNewPosition() {
        PortfolioModel portfolio = new PortfolioModel(BigDecimal.valueOf(10000));
        PositionBuilder positionBuilder = new PositionBuilder();
        PositionDto ps = positionBuilder.build();
        portfolio.addNewPosition(ps.positionType(), ps.filledOrder());
        Map<String, PositionDto> positions = portfolio.getPositions();
        assertEquals(ps, positions.get(ps.identifier()));
    }

    @Test
    public void addPositionQtd() throws PortfolioException {
        PortfolioModel portfolio = new PortfolioModel(BigDecimal.valueOf(10000));
        PositionBuilder positionBuilder = new PositionBuilder();
        PositionDto ps = positionBuilder.build();

        int qtd = ps.quantity();
        portfolio.addNewPosition(ps.positionType(), ps.filledOrder());
        portfolio.addPositionQtd(ps.identifier(), 100, BigDecimal.ONE);
        Map<String, PositionDto> positions = portfolio.getPositions();

        assertTrue(positions.get(ps.identifier()).quantity() == qtd + 100);
        assertEquals(BigDecimal.valueOf(1.0040).setScale(4), positions.get(ps.identifier()).avgPrice());
    }

    @Test(expected = RuntimeException.class)
    public void notAddPositionQtd_withoutPosition() throws PortfolioException {
        PortfolioModel portfolio = new PortfolioModel(BigDecimal.valueOf(10000));
        PositionBuilder positionBuilder = new PositionBuilder();
        PositionDto ps = positionBuilder.build();

        portfolio.addNewPosition(ps.positionType(), ps.filledOrder());
        portfolio.addPositionQtd("ddd", 100, BigDecimal.ONE);
    }


    @Test
    public void removePositionQtd() throws PortfolioException {
        PortfolioModel portfolio = new PortfolioModel(BigDecimal.valueOf(10000));
        PositionBuilder positionBuilder = new PositionBuilder();
        PositionDto ps = positionBuilder.build();

        int qtd = ps.quantity();
        portfolio.addNewPosition(ps.positionType(), ps.filledOrder());
        portfolio.removePositionQtd(ps.identifier(), qtd);
        Map<String, PositionDto> positions = portfolio.getPositions();

        assertTrue(positions.get(ps.identifier()).quantity() == 0);
    }

    @Test
    public void removePositionHalfQtd() throws PortfolioException {
        PortfolioModel portfolio = new PortfolioModel(BigDecimal.valueOf(10000));
        PositionBuilder positionBuilder = new PositionBuilder();
        PositionDto ps = positionBuilder.build();

        portfolio.addNewPosition(ps.positionType(), ps.filledOrder());
        portfolio.removePositionQtd(ps.identifier(), 50);
        Map<String, PositionDto> positions = portfolio.getPositions();

        assertTrue(positions.get(ps.identifier()).quantity() == 950);
    }

    @Test(expected = RuntimeException.class)
    public void notRemovePosition_withoutPosition() throws PortfolioException {
        PortfolioModel portfolio = new PortfolioModel(BigDecimal.valueOf(10000));
        PositionBuilder positionBuilder = new PositionBuilder();
        PositionDto ps = positionBuilder.build();

        portfolio.addNewPosition(ps.positionType(), ps.filledOrder());
        portfolio.removePositionQtd("dsds", 50);

    }

    @Test
    public void closePosition() {
        PortfolioModel portfolio = new PortfolioModel(BigDecimal.valueOf(10000));
        PositionBuilder positionBuilder = new PositionBuilder();
        PositionDto ps = positionBuilder.build();
        portfolio.addNewPosition(ps.positionType(), ps.filledOrder());
        portfolio.closePosition(ps.identifier(), PositionDto.ExitReason.STOP_ORDER_FILLED);

        Map<String, PositionDto> positions = portfolio.getPositions();

        assertEquals(0, positions.size());
    }


    @Test(expected = RuntimeException.class)
    public void notClosePosition_WithoutPosition() {
        PortfolioModel portfolio = new PortfolioModel(BigDecimal.valueOf(10000));
        PositionBuilder positionBuilder = new PositionBuilder();
        PositionDto ps = positionBuilder.build();
        portfolio.closePosition(ps.identifier(), PositionDto.ExitReason.STOP_ORDER_FILLED);
    }

    @Test
    public void returnPosition() {
        PortfolioModel portfolio = new PortfolioModel(BigDecimal.valueOf(10000));
        PositionBuilder positionBuilder = new PositionBuilder();
        PositionDto ps = positionBuilder.build();
        portfolio.addNewPosition(ps.positionType(), ps.filledOrder());
        PositionDto position = portfolio.getPosition(ps.identifier());
        assertEquals(ps, position);
    }

    @Test(expected = RuntimeException.class)
    public void notReturnPosition_NotExists() {
        PortfolioModel portfolio = new PortfolioModel(BigDecimal.valueOf(10000));
        PositionBuilder positionBuilder = new PositionBuilder();
        PositionDto ps = positionBuilder.build();
        portfolio.getPosition(ps.identifier());
    }
}