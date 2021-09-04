package com.apssouza.mytrade.trading.domain.forex.risk;

import com.apssouza.mytrade.feed.api.PriceDto;
import com.apssouza.mytrade.trading.domain.forex.portfolio.PositionBuilder;
import com.apssouza.mytrade.trading.domain.forex.order.*;
import com.apssouza.mytrade.trading.domain.forex.portfolio.Position;
import com.apssouza.mytrade.trading.domain.forex.risk.stoporder.StopOrderCreator;
import com.apssouza.mytrade.trading.domain.forex.risk.stoporder.StopOrderConfigDto;
import com.apssouza.mytrade.trading.domain.forex.risk.stoporder.StopOrderFactory;
import com.apssouza.mytrade.trading.domain.forex.session.event.EventType;
import com.apssouza.mytrade.trading.domain.forex.session.event.PriceChangedEvent;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class StopOrderCreatorFixedTest extends TestCase {

    private StopOrderCreator obj;

    @Before
    public void setUp() throws Exception {
        this.obj = StopOrderFactory.factory(new StopOrderConfigDto( .1, .2, .2, .2));
    }

    @Test
    public void getHardStopLossWhenLongPosition() {
        PositionBuilder positionBuilder = new PositionBuilder();
        positionBuilder.withType(Position.PositionType.LONG);
        Position position = positionBuilder.build();
        obj.createContext(Position.PositionType.LONG);
        com.apssouza.mytrade.trading.domain.forex.order.StopOrderDto hardStopLoss = obj.getHardStopLoss(position);
        assertEquals(com.apssouza.mytrade.trading.domain.forex.order.StopOrderDto.StopOrderStatus.CREATED, hardStopLoss.getStatus());
        assertEquals(com.apssouza.mytrade.trading.domain.forex.order.StopOrderDto.StopOrderType.HARD_STOP, hardStopLoss.getType());
        assertEquals(OrderDto.OrderAction.SELL, hardStopLoss.getAction());
        assertEquals(BigDecimal.valueOf(0.904), hardStopLoss.getPrice());
    }

    @Test
    public void getHardStopLossWhenShortPosition() {
        PositionBuilder positionBuilder = new PositionBuilder();
        positionBuilder.withType(Position.PositionType.SHORT);
        positionBuilder.withPrice(BigDecimal.valueOf(1.004));
        Position position = positionBuilder.build();
        obj.createContext(Position.PositionType.SHORT);

        com.apssouza.mytrade.trading.domain.forex.order.StopOrderDto hardStopLoss = obj.getHardStopLoss(position);
        assertEquals(com.apssouza.mytrade.trading.domain.forex.order.StopOrderDto.StopOrderStatus.CREATED, hardStopLoss.getStatus());
        assertEquals(com.apssouza.mytrade.trading.domain.forex.order.StopOrderDto.StopOrderType.HARD_STOP, hardStopLoss.getType());
        assertEquals(OrderDto.OrderAction.BUY, hardStopLoss.getAction());
        assertEquals(BigDecimal.valueOf(1.104), hardStopLoss.getPrice());
    }

    @Test
    public void getTakeProfitWhenShortPosition() {
        PositionBuilder positionBuilder = new PositionBuilder();
        positionBuilder.withType(Position.PositionType.SHORT);
        Position position = positionBuilder.build();
        obj.createContext(Position.PositionType.SHORT);

        com.apssouza.mytrade.trading.domain.forex.order.StopOrderDto hardStopLoss = obj.getProfitStopOrder(position);
        assertEquals(com.apssouza.mytrade.trading.domain.forex.order.StopOrderDto.StopOrderStatus.CREATED, hardStopLoss.getStatus());
        assertEquals(com.apssouza.mytrade.trading.domain.forex.order.StopOrderDto.StopOrderType.TAKE_PROFIT, hardStopLoss.getType());
        assertEquals(OrderDto.OrderAction.BUY, hardStopLoss.getAction());
        assertEquals(BigDecimal.valueOf(0.804), hardStopLoss.getPrice());
    }


    @Test
    public void getTakeProfitWhenLongPosition() {
        PositionBuilder positionBuilder = new PositionBuilder();
        positionBuilder.withType(Position.PositionType.LONG);
        positionBuilder.withPrice(BigDecimal.valueOf(1.004));
        Position position = positionBuilder.build();

        obj.createContext(Position.PositionType.LONG);

        com.apssouza.mytrade.trading.domain.forex.order.StopOrderDto hardStopLoss = obj.getProfitStopOrder(position);
        assertEquals(com.apssouza.mytrade.trading.domain.forex.order.StopOrderDto.StopOrderStatus.CREATED, hardStopLoss.getStatus());
        assertEquals(com.apssouza.mytrade.trading.domain.forex.order.StopOrderDto.StopOrderType.TAKE_PROFIT, hardStopLoss.getType());
        assertEquals(OrderDto.OrderAction.SELL, hardStopLoss.getAction());
        assertEquals(BigDecimal.valueOf(1.204), hardStopLoss.getPrice());
    }


    @Test
    public void getEntryStopLossWhenLongPosition() {
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
        PriceChangedEvent event = new PriceChangedEvent(EventType.PRICE_CHANGED,now, priceMap);
        Optional<com.apssouza.mytrade.trading.domain.forex.order.StopOrderDto> optional = obj.getEntryStopOrder(position, event);
        com.apssouza.mytrade.trading.domain.forex.order.StopOrderDto hardStopLoss = optional.get();
        assertEquals(com.apssouza.mytrade.trading.domain.forex.order.StopOrderDto.StopOrderStatus.CREATED, hardStopLoss.getStatus());
        assertEquals(com.apssouza.mytrade.trading.domain.forex.order.StopOrderDto.StopOrderType.ENTRY_STOP, hardStopLoss.getType());
        assertEquals(OrderDto.OrderAction.SELL, hardStopLoss.getAction());
        assertEquals(BigDecimal.valueOf(1.004), hardStopLoss.getPrice());
    }

    @Test
    public void getEntryStopLossWhenLongPositionAndPriceNotReachedEntryDistance() {
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
        PriceChangedEvent event = new PriceChangedEvent(EventType.PRICE_CHANGED,now, priceMap);
        Optional<com.apssouza.mytrade.trading.domain.forex.order.StopOrderDto> optional = obj.getEntryStopOrder(position, event);
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
        PriceChangedEvent event = new PriceChangedEvent(EventType.PRICE_CHANGED,now, priceMap);
        Optional<com.apssouza.mytrade.trading.domain.forex.order.StopOrderDto> optional = obj.getEntryStopOrder(position, event);
        com.apssouza.mytrade.trading.domain.forex.order.StopOrderDto hardStopLoss = optional.get();
        assertEquals(com.apssouza.mytrade.trading.domain.forex.order.StopOrderDto.StopOrderStatus.CREATED, hardStopLoss.getStatus());
        assertEquals(com.apssouza.mytrade.trading.domain.forex.order.StopOrderDto.StopOrderType.ENTRY_STOP, hardStopLoss.getType());
        assertEquals(OrderDto.OrderAction.BUY, hardStopLoss.getAction());
        assertEquals(BigDecimal.valueOf(1.004), hardStopLoss.getPrice());
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
        PriceChangedEvent event = new PriceChangedEvent(EventType.PRICE_CHANGED,now, priceMap);
        Optional<com.apssouza.mytrade.trading.domain.forex.order.StopOrderDto> optional = obj.getEntryStopOrder(position, event);
        assertFalse(optional.isPresent());
    }

}