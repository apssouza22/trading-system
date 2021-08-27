package com.apssouza.mytrade.trading.forex.portfolio;

import com.apssouza.mytrade.trading.builder.LoopEventBuilder;
import com.apssouza.mytrade.trading.builder.PositionBuilder;
import com.apssouza.mytrade.trading.forex.session.event.PriceChangedEvent;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class PortfolioModelTest extends TestCase {

    @Test
    public void updatePortfolioValue() {
        PortfolioModel portfolio = new PortfolioModel(BigDecimal.valueOf(10000));
        PositionBuilder positionBuilder = new PositionBuilder();
        Position ps = positionBuilder.build();
        LoopEventBuilder loopEventBuilder = new LoopEventBuilder();
        loopEventBuilder.withPriceMap(BigDecimal.valueOf(10));
        PriceChangedEvent loopEvent = loopEventBuilder.build();
        portfolio.addNewPosition(ps);
        portfolio.updatePortfolioValue(loopEvent);

        Map<String, Position> positions = portfolio.getPositions();

        BigDecimal currentPrice = positions.get(ps.getIdentifier()).getCurrentPrice();
        assertFalse(ps.getInitPrice().equals(currentPrice));
    }


    @Test
    public void getPositions() {
    }

    @Test
    public void addNewPosition() {
        PortfolioModel portfolio = new PortfolioModel(BigDecimal.valueOf(10000));
        PositionBuilder positionBuilder = new PositionBuilder();
        Position ps = positionBuilder.build();
        portfolio.addNewPosition(ps);
        Map<String, Position> positions = portfolio.getPositions();
        assertEquals(ps, positions.get(ps.getIdentifier()));
    }

    @Test
    public void addPositionQtd() {
        PortfolioModel portfolio = new PortfolioModel(BigDecimal.valueOf(10000));
        PositionBuilder positionBuilder = new PositionBuilder();
        Position ps = positionBuilder.build();

        int qtd = ps.getQuantity();
        portfolio.addNewPosition(ps);
        portfolio.addPositionQtd(ps.getIdentifier(), 100, BigDecimal.ONE);
        Map<String, Position> positions = portfolio.getPositions();

        assertTrue( positions.get(ps.getIdentifier()).getQuantity() == qtd + 100);
        assertEquals(BigDecimal.valueOf(1.0040).setScale(4), positions.get(ps.getIdentifier()).getAvgPrice());
    }

    @Test(expected = RuntimeException.class)
    public void addPositionQtdWithNoPosition() {
        PortfolioModel portfolio = new PortfolioModel(BigDecimal.valueOf(10000));
        PositionBuilder positionBuilder = new PositionBuilder();
        Position ps = positionBuilder.build();

        portfolio.addNewPosition(ps);
        portfolio.addPositionQtd("ddd", 100, BigDecimal.ONE);
    }


    @Test
    public void removePositionQtd() {
        PortfolioModel portfolio = new PortfolioModel(BigDecimal.valueOf(10000));
        PositionBuilder positionBuilder = new PositionBuilder();
        Position ps = positionBuilder.build();

        int qtd = ps.getQuantity();
        portfolio.addNewPosition(ps);
        portfolio.removePositionQtd(ps.getIdentifier(),qtd);
        Map<String, Position> positions = portfolio.getPositions();

        assertTrue(positions.get(ps.getIdentifier()).getQuantity()==0);
    }

    @Test
    public void removePositionHalfQtd() {
        PortfolioModel portfolio = new PortfolioModel(BigDecimal.valueOf(10000));
        PositionBuilder positionBuilder = new PositionBuilder();
        Position ps = positionBuilder.build();

        portfolio.addNewPosition(ps);
        portfolio.removePositionQtd(ps.getIdentifier(), 50);
        Map<String, Position> positions = portfolio.getPositions();

        assertTrue( positions.get(ps.getIdentifier()).getQuantity() == 950);
    }

    @Test(expected = RuntimeException.class)
    public void removePositionWithNoPosition() {
        PortfolioModel portfolio = new PortfolioModel(BigDecimal.valueOf(10000));
        PositionBuilder positionBuilder = new PositionBuilder();
        Position ps = positionBuilder.build();

        portfolio.addNewPosition(ps);
        portfolio.removePositionQtd("dsds", 50);

    }

    @Test
    public void closePosition() {
        PortfolioModel portfolio = new PortfolioModel(BigDecimal.valueOf(10000));
        PositionBuilder positionBuilder = new PositionBuilder();
        Position ps = positionBuilder.build();
        portfolio.addNewPosition(ps);
        portfolio.closePosition(ps.getIdentifier());

        Map<String, Position> positions = portfolio.getPositions();

        assertEquals(0, positions.size());
    }


    @Test(expected = RuntimeException.class)
    public void closePositionWithNoPosition() {
        PortfolioModel portfolio = new PortfolioModel(BigDecimal.valueOf(10000));
        PositionBuilder positionBuilder = new PositionBuilder();
        Position ps = positionBuilder.build();
        portfolio.closePosition(ps.getIdentifier());
    }

    @Test
    public void getPosition() {
        PortfolioModel portfolio = new PortfolioModel(BigDecimal.valueOf(10000));
        PositionBuilder positionBuilder = new PositionBuilder();
        Position ps = positionBuilder.build();
        portfolio.addNewPosition(ps);
        Position position = portfolio.getPosition(ps.getIdentifier());
        assertEquals(ps, position);
    }

    @Test(expected = RuntimeException.class)
    public void getPositionNotFound() {
        PortfolioModel portfolio = new PortfolioModel(BigDecimal.valueOf(10000));
        PositionBuilder positionBuilder = new PositionBuilder();
        Position ps = positionBuilder.build();
        portfolio.getPosition(ps.getIdentifier());
    }
}