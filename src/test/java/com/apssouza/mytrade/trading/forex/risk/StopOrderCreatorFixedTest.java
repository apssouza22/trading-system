package com.apssouza.mytrade.trading.forex.risk;

import com.apssouza.mytrade.feed.price.PriceDto;
import com.apssouza.mytrade.trading.forex.order.*;
import com.apssouza.mytrade.trading.forex.portfolio.Position;
import com.apssouza.mytrade.trading.forex.portfolio.PositionStatus;
import com.apssouza.mytrade.trading.forex.portfolio.PositionType;
import com.apssouza.mytrade.trading.misc.loop.LoopEvent;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class StopOrderCreatorFixedTest extends TestCase {

    private StopOrderCreatorFixed obj;

    @Before
    public void setUp() throws Exception {
        this.obj = new StopOrderCreatorFixed(.1, .2, .2, .2);

    }

    @Test
    public void getHardStopLossWhenLongPosition() {
        Position position = new Position(
                PositionType.LONG,
                "AUDUSD",
                1000,
                BigDecimal.valueOf(1.0004),
                LocalDateTime.now(), "AUDUSD",
                null,
                null,
                PositionStatus.FILLED
        );
        StopOrderDto hardStopLoss = obj.getHardStopLoss(position);
        assertEquals(StopOrderStatus.CREATED, hardStopLoss.getStatus());
        assertEquals(StopOrderType.HARD_STOP, hardStopLoss.getType());
        assertEquals(OrderAction.SELL, hardStopLoss.getAction());
        assertEquals(BigDecimal.valueOf(0.9004), hardStopLoss.getPrice());
    }

    @Test
    public void getHardStopLossWhenShortPosition() {
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
        StopOrderDto hardStopLoss = obj.getHardStopLoss(position);
        assertEquals(StopOrderStatus.CREATED, hardStopLoss.getStatus());
        assertEquals(StopOrderType.HARD_STOP, hardStopLoss.getType());
        assertEquals(OrderAction.BUY, hardStopLoss.getAction());
        assertEquals(BigDecimal.valueOf(1.104), hardStopLoss.getPrice());
    }

    @Test
    public void getTakeProfitWhenShortPosition() {
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
        StopOrderDto hardStopLoss = obj.getProfitStopOrder(position);
        assertEquals(StopOrderStatus.CREATED, hardStopLoss.getStatus());
        assertEquals(StopOrderType.TAKE_PROFIT, hardStopLoss.getType());
        assertEquals(OrderAction.BUY, hardStopLoss.getAction());
        assertEquals(BigDecimal.valueOf(0.804), hardStopLoss.getPrice());
    }


    @Test
    public void getTakeProfitWhenLongPosition() {
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
        StopOrderDto hardStopLoss = obj.getProfitStopOrder(position);
        assertEquals(StopOrderStatus.CREATED, hardStopLoss.getStatus());
        assertEquals(StopOrderType.TAKE_PROFIT, hardStopLoss.getType());
        assertEquals(OrderAction.SELL, hardStopLoss.getAction());
        assertEquals(BigDecimal.valueOf(1.204), hardStopLoss.getPrice());
    }


    @Test
    public void getEntryStopLossWhenLongPosition() {
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
        HashMap<String, PriceDto> priceMap = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        BigDecimal close = BigDecimal.valueOf(1.305);
        PriceDto priceDto = new PriceDto(now, close, close, close, close, "AUDUSD");
        priceMap.put("AUDUSD", priceDto);
        LoopEvent event = new LoopEvent(now, priceMap);
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
        HashMap<String, PriceDto> priceMap = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        BigDecimal close = BigDecimal.valueOf(1.105);
        PriceDto priceDto = new PriceDto(now, close, close, close, close, "AUDUSD");
        priceMap.put("AUDUSD", priceDto);
        LoopEvent event = new LoopEvent(now, priceMap);
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
        HashMap<String, PriceDto> priceMap = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        BigDecimal close = BigDecimal.valueOf(0.803);
        PriceDto priceDto = new PriceDto(now, close, close, close, close, "AUDUSD");
        priceMap.put("AUDUSD", priceDto);
        LoopEvent event = new LoopEvent(now, priceMap);
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
        HashMap<String, PriceDto> priceMap = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        BigDecimal close = BigDecimal.valueOf(1.105);
        PriceDto priceDto = new PriceDto(now, close, close, close, close, "AUDUSD");
        priceMap.put("AUDUSD", priceDto);
        LoopEvent event = new LoopEvent(now, priceMap);
        Optional<StopOrderDto> optional = obj.getEntryStopOrder(position, event);
        assertFalse(optional.isPresent());
    }

}