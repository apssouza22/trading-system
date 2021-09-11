package com.apssouza.mytrade.trading.domain.forex.execution;

import com.apssouza.mytrade.feed.api.PriceDto;
import com.apssouza.mytrade.trading.domain.forex.order.StopOrderBuilder;
import com.apssouza.mytrade.trading.domain.forex.order.OrderDto;
import com.apssouza.mytrade.trading.domain.forex.risk.stoporder.StopOrderDto;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import static java.math.BigDecimal.*;
import static java.time.LocalDateTime.MIN;

@RunWith(MockitoJUnitRunner.class)
public class StopOrderPriceMonitorTest extends TestCase {
    Map<Integer, StopOrderDto> stopOrders;
    Map<String, PriceDto> priceMap;
    StopOrderPriceMonitor stopOrderPriceMonitor;

    @Before
    public void setUp() throws Exception {
        stopOrders = new HashMap<>();
        priceMap = new HashMap<>();
        stopOrderPriceMonitor = new StopOrderPriceMonitor();
    }

    @Test
    public void getFilledOrders_WithNoStopOrders() {
        Set<StopOrderDto> filledOrders = stopOrderPriceMonitor.getFilledOrders(priceMap, stopOrders);
        assertTrue(filledOrders.isEmpty());
    }

    @Test
    public void getFilledOrders_WithNoSubmittedOrder() {
        StopOrderBuilder stopOrderBuilder = new StopOrderBuilder();
        stopOrderBuilder.withType(StopOrderDto.StopOrderType.STOP_LOSS);
        stopOrderBuilder.withStatus(StopOrderDto.StopOrderStatus.CREATED);
        stopOrders.put(1, stopOrderBuilder.build());

        priceMap.put("AUDUSD", new PriceDto(MIN, TEN, TEN, TEN, TEN, "AUDUSD"));
        Set<StopOrderDto> filledOrders = stopOrderPriceMonitor.getFilledOrders(priceMap, stopOrders);
        assertTrue(filledOrders.isEmpty());
    }

    @Test
    public void getFilledOrders_With2FilledOrderFor1Position() {
        StopOrderBuilder stopOrderBuilder = new StopOrderBuilder();
        stopOrderBuilder.withType(StopOrderDto.StopOrderType.STOP_LOSS);
        stopOrderBuilder.withStatus(StopOrderDto.StopOrderStatus.SUBMITTED);
        stopOrders.put(1, stopOrderBuilder.build());

        stopOrderBuilder.withType(StopOrderDto.StopOrderType.TAKE_PROFIT);
        stopOrders.put(2, stopOrderBuilder.build());

        priceMap.put("AUDUSD", new PriceDto(MIN, TEN, TEN, TEN, TEN, "AUDUSD"));

        Set<StopOrderDto> filledOrders = stopOrderPriceMonitor.getFilledOrders(priceMap, stopOrders);
        assertEquals(1, filledOrders.size());
    }

    @Test
    public void getFilledOrders_WithBuyStopLossFilled() {
        StopOrderBuilder stopOrderBuilder = new StopOrderBuilder();
        stopOrderBuilder.withType(StopOrderDto.StopOrderType.STOP_LOSS);
        stopOrderBuilder.withStatus(StopOrderDto.StopOrderStatus.SUBMITTED);
        StopOrderDto stopLoss = stopOrderBuilder.build();
        stopOrders.put(1, stopLoss);

        stopOrderBuilder.withType(StopOrderDto.StopOrderType.TAKE_PROFIT);
        stopOrders.put(2, stopOrderBuilder.build());

        priceMap.put("AUDUSD", new PriceDto(MIN, TEN, TEN, TEN, TEN, "AUDUSD"));

        Set<StopOrderDto> filledOrders = stopOrderPriceMonitor.getFilledOrders(priceMap, stopOrders);
        assertEquals(1, filledOrders.size());
        assertEquals(stopLoss.type(), filledOrders.iterator().next().type());
    }


    @Test
    public void getFilledOrders_WithBuyTakeProfitFilled() {
        StopOrderBuilder stopOrderBuilder = new StopOrderBuilder();
        stopOrderBuilder.withType(StopOrderDto.StopOrderType.STOP_LOSS);
        stopOrderBuilder.withStatus(StopOrderDto.StopOrderStatus.SUBMITTED);
        StopOrderDto stopLoss = stopOrderBuilder.build();
        stopOrders.put(1, stopLoss);

        stopOrderBuilder.withType(StopOrderDto.StopOrderType.TAKE_PROFIT);
        stopOrders.put(2, stopOrderBuilder.build());

        priceMap.put("AUDUSD", new PriceDto(MIN, ZERO, ZERO, ZERO, ZERO, "AUDUSD"));

        Set<StopOrderDto> filledOrders = stopOrderPriceMonitor.getFilledOrders(priceMap, stopOrders);
        assertEquals(1, filledOrders.size());
        assertEquals(StopOrderDto.StopOrderType.TAKE_PROFIT, filledOrders.iterator().next().type());
    }

    @Test
    public void getFilledOrders_WithSellTakeProfitFilled() {
        StopOrderBuilder stopOrderBuilder = new StopOrderBuilder();
        stopOrderBuilder.withType(StopOrderDto.StopOrderType.STOP_LOSS);
        stopOrderBuilder.withAction(OrderDto.OrderAction.SELL);
        stopOrderBuilder.withStatus(StopOrderDto.StopOrderStatus.SUBMITTED);
        StopOrderDto stopLoss = stopOrderBuilder.build();
        stopOrders.put(1, stopLoss);

        stopOrderBuilder.withType(StopOrderDto.StopOrderType.TAKE_PROFIT);
        stopOrders.put(2, stopOrderBuilder.build());

        priceMap.put("AUDUSD", new PriceDto(MIN, TEN, TEN, TEN, TEN, "AUDUSD"));

        Set<StopOrderDto> filledOrders = stopOrderPriceMonitor.getFilledOrders(priceMap, stopOrders);
        assertEquals(1, filledOrders.size());
        assertEquals(StopOrderDto.StopOrderType.TAKE_PROFIT, filledOrders.iterator().next().type());
    }


    @Test
    public void getFilledOrders_WithSellStopLossFilled() {
        StopOrderBuilder stopOrderBuilder = new StopOrderBuilder();
        stopOrderBuilder.withType(StopOrderDto.StopOrderType.STOP_LOSS);
        stopOrderBuilder.withAction(OrderDto.OrderAction.SELL);
        stopOrderBuilder.withStatus(StopOrderDto.StopOrderStatus.SUBMITTED);
        StopOrderDto stopLoss = stopOrderBuilder.build();
        stopOrders.put(1, stopLoss);

        stopOrderBuilder.withType(StopOrderDto.StopOrderType.TAKE_PROFIT);
        stopOrders.put(2, stopOrderBuilder.build());

        priceMap.put("AUDUSD", new PriceDto(MIN, ZERO, ZERO, ZERO, ZERO, "AUDUSD"));

        Set<StopOrderDto> filledOrders = stopOrderPriceMonitor.getFilledOrders(priceMap, stopOrders);
        assertEquals(1, filledOrders.size());
        assertEquals(StopOrderDto.StopOrderType.STOP_LOSS, filledOrders.iterator().next().type());
    }
}