package com.apssouza.mytrade.trading.forex.risk;

import com.apssouza.mytrade.feed.PriceDto;
import com.apssouza.mytrade.trading.builder.PositionBuilder;
import com.apssouza.mytrade.trading.forex.order.*;
import com.apssouza.mytrade.trading.forex.portfolio.Position;
import com.apssouza.mytrade.trading.forex.portfolio.PositionStatus;
import com.apssouza.mytrade.trading.forex.portfolio.PositionType;
import com.apssouza.mytrade.trading.forex.risk.stoporder.PriceDistanceObject;
import com.apssouza.mytrade.trading.forex.risk.stoporder.fixed.StopOrderCreatorFixed;
import com.apssouza.mytrade.trading.forex.session.event.EventType;
import com.apssouza.mytrade.trading.forex.session.event.PriceChangedEvent;
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

    private StopOrderCreatorFixed obj;

    @Before
    public void setUp() throws Exception {
        this.obj = new StopOrderCreatorFixed(new PriceDistanceObject( .1, .2, .2, .2));

    }

    @Test
    public void getHardStopLossWhenLongPosition() {
        PositionBuilder positionBuilder = new PositionBuilder();
        positionBuilder.setType(PositionType.LONG);
        Position position = positionBuilder.build();
        obj.createContext(PositionType.LONG);
        StopOrderDto hardStopLoss = obj.getHardStopLoss(position);
        assertEquals(StopOrderStatus.CREATED, hardStopLoss.getStatus());
        assertEquals(StopOrderType.HARD_STOP, hardStopLoss.getType());
        assertEquals(OrderAction.SELL, hardStopLoss.getAction());
        assertEquals(BigDecimal.valueOf(0.904), hardStopLoss.getPrice());
    }

    @Test
    public void getHardStopLossWhenShortPosition() {
        PositionBuilder positionBuilder = new PositionBuilder();
        positionBuilder.setType(PositionType.SHORT);
        positionBuilder.setPrice(BigDecimal.valueOf(1.004));
        Position position = positionBuilder.build();
        obj.createContext(PositionType.SHORT);

        StopOrderDto hardStopLoss = obj.getHardStopLoss(position);
        assertEquals(StopOrderStatus.CREATED, hardStopLoss.getStatus());
        assertEquals(StopOrderType.HARD_STOP, hardStopLoss.getType());
        assertEquals(OrderAction.BUY, hardStopLoss.getAction());
        assertEquals(BigDecimal.valueOf(1.104), hardStopLoss.getPrice());
    }

    @Test
    public void getTakeProfitWhenShortPosition() {
        PositionBuilder positionBuilder = new PositionBuilder();
        positionBuilder.setType(PositionType.SHORT);
        Position position = positionBuilder.build();
        obj.createContext(PositionType.SHORT);

        StopOrderDto hardStopLoss = obj.getProfitStopOrder(position);
        assertEquals(StopOrderStatus.CREATED, hardStopLoss.getStatus());
        assertEquals(StopOrderType.TAKE_PROFIT, hardStopLoss.getType());
        assertEquals(OrderAction.BUY, hardStopLoss.getAction());
        assertEquals(BigDecimal.valueOf(0.804), hardStopLoss.getPrice());
    }


    @Test
    public void getTakeProfitWhenLongPosition() {
        PositionBuilder positionBuilder = new PositionBuilder();
        positionBuilder.setType(PositionType.LONG);
        positionBuilder.setPrice(BigDecimal.valueOf(1.004));
        Position position = positionBuilder.build();

        obj.createContext(PositionType.LONG);

        StopOrderDto hardStopLoss = obj.getProfitStopOrder(position);
        assertEquals(StopOrderStatus.CREATED, hardStopLoss.getStatus());
        assertEquals(StopOrderType.TAKE_PROFIT, hardStopLoss.getType());
        assertEquals(OrderAction.SELL, hardStopLoss.getAction());
        assertEquals(BigDecimal.valueOf(1.204), hardStopLoss.getPrice());
    }


    @Test
    public void getEntryStopLossWhenLongPosition() {
        PositionBuilder positionBuilder = new PositionBuilder();
        positionBuilder.setType(PositionType.LONG);
        positionBuilder.setPrice(BigDecimal.valueOf(1.004));
        Position position = positionBuilder.build();

        obj.createContext(PositionType.LONG);

        HashMap<String, PriceDto> priceMap = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        BigDecimal close = BigDecimal.valueOf(1.305);
        PriceDto priceDto = new PriceDto(now, close, close, close, close, "AUDUSD");
        priceMap.put("AUDUSD", priceDto);
        PriceChangedEvent event = new PriceChangedEvent(EventType.PRICE_CHANGED,now, priceMap);
        Optional<StopOrderDto> optional = obj.getEntryStopOrder(position, event);
        StopOrderDto hardStopLoss = optional.get();
        assertEquals(StopOrderStatus.CREATED, hardStopLoss.getStatus());
        assertEquals(StopOrderType.ENTRY_STOP, hardStopLoss.getType());
        assertEquals(OrderAction.SELL, hardStopLoss.getAction());
        assertEquals(BigDecimal.valueOf(1.004), hardStopLoss.getPrice());
    }

    @Test
    public void getEntryStopLossWhenLongPositionAndPriceNotReachedEntryDistance() {
        Position position = new Position(
                PositionType.LONG,
                "AUDUSD",
                1000,
                BigDecimal.valueOf(1.004),
                LocalDateTime.now(), "AUDUSD",
                null,
                null,
                PositionStatus.FILLED
        );
        obj.createContext(PositionType.LONG);
        HashMap<String, PriceDto> priceMap = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        BigDecimal close = BigDecimal.valueOf(1.105);
        PriceDto priceDto = new PriceDto(now, close, close, close, close, "AUDUSD");
        priceMap.put("AUDUSD", priceDto);
        PriceChangedEvent event = new PriceChangedEvent(EventType.PRICE_CHANGED,now, priceMap);
        Optional<StopOrderDto> optional = obj.getEntryStopOrder(position, event);
        assertFalse(optional.isPresent());
    }


    @Test
    public void getEntryStopLossWhenShortPosition() {
        Position position = new Position(
                PositionType.SHORT,
                "AUDUSD",
                1000,
                BigDecimal.valueOf(1.004),
                LocalDateTime.now(), "AUDUSD",
                null,
                null,
                PositionStatus.FILLED
        );
        obj.createContext(PositionType.SHORT);
        HashMap<String, PriceDto> priceMap = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        BigDecimal close = BigDecimal.valueOf(0.803);
        PriceDto priceDto = new PriceDto(now, close, close, close, close, "AUDUSD");
        priceMap.put("AUDUSD", priceDto);
        PriceChangedEvent event = new PriceChangedEvent(EventType.PRICE_CHANGED,now, priceMap);
        Optional<StopOrderDto> optional = obj.getEntryStopOrder(position, event);
        StopOrderDto hardStopLoss = optional.get();
        assertEquals(StopOrderStatus.CREATED, hardStopLoss.getStatus());
        assertEquals(StopOrderType.ENTRY_STOP, hardStopLoss.getType());
        assertEquals(OrderAction.BUY, hardStopLoss.getAction());
        assertEquals(BigDecimal.valueOf(1.004), hardStopLoss.getPrice());
    }

    @Test
    public void getEntryStopLossWhenShortPositionAndPriceNotReachedEntryDistance() {
        Position position = new Position(
                PositionType.SHORT,
                "AUDUSD",
                1000,
                BigDecimal.valueOf(1.004),
                LocalDateTime.now(), "AUDUSD",
                null,
                null,
                PositionStatus.FILLED
        );
        obj.createContext(PositionType.SHORT);
        HashMap<String, PriceDto> priceMap = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        BigDecimal close = BigDecimal.valueOf(1.105);
        PriceDto priceDto = new PriceDto(now, close, close, close, close, "AUDUSD");
        priceMap.put("AUDUSD", priceDto);
        PriceChangedEvent event = new PriceChangedEvent(EventType.PRICE_CHANGED,now, priceMap);
        Optional<StopOrderDto> optional = obj.getEntryStopOrder(position, event);
        assertFalse(optional.isPresent());
    }

}