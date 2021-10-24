package com.apssouza.mytrade.trading.domain.forex.risk;

import com.apssouza.mytrade.feed.api.PriceDto;
import com.apssouza.mytrade.trading.domain.forex.feed.pricefeed.PriceChangedEvent;
import com.apssouza.mytrade.trading.domain.forex.order.OrderDto;
import com.apssouza.mytrade.trading.domain.forex.portfolio.Position;
import com.apssouza.mytrade.trading.domain.forex.portfolio.PositionBuilder;
import com.apssouza.mytrade.trading.domain.forex.risk.stopordercreation.StopOrderConfigDto;
import com.apssouza.mytrade.trading.domain.forex.risk.stopordercreation.StopOrderCreator;
import com.apssouza.mytrade.trading.domain.forex.risk.stopordercreation.StopOrderDto;
import com.apssouza.mytrade.trading.domain.forex.risk.stopordercreation.StopOrderFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;

import junit.framework.TestCase;

@RunWith(MockitoJUnitRunner.class)
public class StopOrderCreatorFixedTest extends TestCase {

    private StopOrderCreator obj;

    @Before
    public void setUp() throws Exception {
        this.obj = StopOrderFactory.factory(new StopOrderConfigDto(.1, .2, .2, .2));
    }

    @Test
    public void getHardStopLoss_WhenLongPosition() {
        PositionBuilder positionBuilder = new PositionBuilder();
        positionBuilder.withType(Position.PositionType.LONG);
        Position position = positionBuilder.build();
        obj.createContext(Position.PositionType.LONG);
        StopOrderDto hardStopLoss = obj.getHardStopLoss(position);

        assertEquals(StopOrderDto.StopOrderStatus.CREATED, hardStopLoss.status());
        assertEquals(StopOrderDto.StopOrderType.HARD_STOP, hardStopLoss.type());
        assertEquals(OrderDto.OrderAction.SELL, hardStopLoss.action());
        assertEquals(BigDecimal.valueOf(0.904), hardStopLoss.price());
    }

    @Test
    public void getHardStopLoss_WhenShortPosition() {
        PositionBuilder positionBuilder = new PositionBuilder();
        positionBuilder.withType(Position.PositionType.SHORT);
        positionBuilder.withPrice(BigDecimal.valueOf(1.004));
        Position position = positionBuilder.build();
        obj.createContext(Position.PositionType.SHORT);

        StopOrderDto hardStopLoss = obj.getHardStopLoss(position);
        assertEquals(StopOrderDto.StopOrderStatus.CREATED, hardStopLoss.status());
        assertEquals(StopOrderDto.StopOrderType.HARD_STOP, hardStopLoss.type());
        assertEquals(OrderDto.OrderAction.BUY, hardStopLoss.action());
        assertEquals(BigDecimal.valueOf(1.104), hardStopLoss.price());
    }

    @Test
    public void getTakeProfit_WhenShortPosition() {
        PositionBuilder positionBuilder = new PositionBuilder();
        positionBuilder.withType(Position.PositionType.SHORT);
        Position position = positionBuilder.build();
        obj.createContext(Position.PositionType.SHORT);

        StopOrderDto hardStopLoss = obj.getProfitStopOrder(position);
        assertEquals(StopOrderDto.StopOrderStatus.CREATED, hardStopLoss.status());
        assertEquals(StopOrderDto.StopOrderType.TAKE_PROFIT, hardStopLoss.type());
        assertEquals(OrderDto.OrderAction.BUY, hardStopLoss.action());
        assertEquals(BigDecimal.valueOf(0.804), hardStopLoss.price());
    }


    @Test
    public void getTakeProfit_WhenLongPosition() {
        PositionBuilder positionBuilder = new PositionBuilder();
        positionBuilder.withType(Position.PositionType.LONG);
        positionBuilder.withPrice(BigDecimal.valueOf(1.004));
        Position position = positionBuilder.build();

        obj.createContext(Position.PositionType.LONG);

        StopOrderDto hardStopLoss = obj.getProfitStopOrder(position);
        assertEquals(StopOrderDto.StopOrderStatus.CREATED, hardStopLoss.status());
        assertEquals(StopOrderDto.StopOrderType.TAKE_PROFIT, hardStopLoss.type());
        assertEquals(OrderDto.OrderAction.SELL, hardStopLoss.action());
        assertEquals(BigDecimal.valueOf(1.204), hardStopLoss.price());
    }


    @Test
    public void getEntryStopLoss_WhenLongPosition() {
        PositionBuilder positionBuilder = new PositionBuilder();
        positionBuilder.withType(Position.PositionType.LONG);
        positionBuilder.withPrice(BigDecimal.valueOf(1.004));
        Position position = positionBuilder.build();

        obj.createContext(Position.PositionType.LONG);

        HashMap<String, PriceDto> priceMap = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        BigDecimal close = BigDecimal.valueOf(1.305);
        PriceDto priceDto = new PriceDto(now, close, close, close, close, "AUDUSD");
        priceMap.put("AUDUSD", priceDto);
        PriceChangedEvent event = new PriceChangedEvent(now, priceMap);
        Optional<StopOrderDto> optional = obj.getEntryStopOrder(position, event);
        StopOrderDto hardStopLoss = optional.get();
        assertEquals(StopOrderDto.StopOrderStatus.CREATED, hardStopLoss.status());
        assertEquals(StopOrderDto.StopOrderType.ENTRY_STOP, hardStopLoss.type());
        assertEquals(OrderDto.OrderAction.SELL, hardStopLoss.action());
        assertEquals(BigDecimal.valueOf(1.004), hardStopLoss.price());
    }

    @Test
    public void getEntryStopLoss_WhenLongPositionAndPriceNotReachedEntryDistance() {
        Position position = new Position(
                Position.PositionType.LONG,
                "AUDUSD",
                1000,
                BigDecimal.valueOf(1.004),
                LocalDateTime.now(), "AUDUSD",
                null,
                null,
                Position.PositionStatus.FILLED
        );
        obj.createContext(Position.PositionType.LONG);
        HashMap<String, PriceDto> priceMap = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        BigDecimal close = BigDecimal.valueOf(1.105);
        PriceDto priceDto = new PriceDto(now, close, close, close, close, "AUDUSD");
        priceMap.put("AUDUSD", priceDto);
        PriceChangedEvent event = new PriceChangedEvent(now, priceMap);
        Optional<StopOrderDto> optional = obj.getEntryStopOrder(position, event);
        assertFalse(optional.isPresent());
    }


    @Test
    public void getEntryStopLossWhenShortPosition() {
        Position position = new Position(
                Position.PositionType.SHORT,
                "AUDUSD",
                1000,
                BigDecimal.valueOf(1.004),
                LocalDateTime.now(), "AUDUSD",
                null,
                null,
                Position.PositionStatus.FILLED
        );
        obj.createContext(Position.PositionType.SHORT);
        HashMap<String, PriceDto> priceMap = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        BigDecimal close = BigDecimal.valueOf(0.803);
        PriceDto priceDto = new PriceDto(now, close, close, close, close, "AUDUSD");
        priceMap.put("AUDUSD", priceDto);
        PriceChangedEvent event = new PriceChangedEvent(now, priceMap);
        Optional<StopOrderDto> optional = obj.getEntryStopOrder(position, event);
        StopOrderDto hardStopLoss = optional.get();
        assertEquals(StopOrderDto.StopOrderStatus.CREATED, hardStopLoss.status());
        assertEquals(StopOrderDto.StopOrderType.ENTRY_STOP, hardStopLoss.type());
        assertEquals(OrderDto.OrderAction.BUY, hardStopLoss.action());
        assertEquals(BigDecimal.valueOf(1.004), hardStopLoss.price());
    }

    @Test
    public void getEntryStopLossWhenShortPositionAndPriceNotReachedEntryDistance() {
        Position position = new Position(
                Position.PositionType.SHORT,
                "AUDUSD",
                1000,
                BigDecimal.valueOf(1.004),
                LocalDateTime.now(), "AUDUSD",
                null,
                null,
                Position.PositionStatus.FILLED
        );
        obj.createContext(Position.PositionType.SHORT);
        HashMap<String, PriceDto> priceMap = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        BigDecimal close = BigDecimal.valueOf(1.105);
        PriceDto priceDto = new PriceDto(now, close, close, close, close, "AUDUSD");
        priceMap.put("AUDUSD", priceDto);
        PriceChangedEvent event = new PriceChangedEvent(now, priceMap);
        Optional<StopOrderDto> optional = obj.getEntryStopOrder(position, event);
        assertFalse(optional.isPresent());
    }

}