package com.apssouza.mytrade.trading.forex.execution;

import com.apssouza.mytrade.feed.api.PriceDto;
import com.apssouza.mytrade.trading.builder.StopOrderBuilder;
import com.apssouza.mytrade.trading.forex.order.OrderAction;
import com.apssouza.mytrade.trading.forex.order.StopOrderDto;
import com.apssouza.mytrade.trading.forex.order.StopOrderStatus;
import com.apssouza.mytrade.trading.forex.order.StopOrderType;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
    public void getFilledOrdersWithNoStopOrders() {
        Set<StopOrderDto> filledOrders = stopOrderPriceMonitor.getFilledOrders(priceMap, stopOrders);
        assertTrue(filledOrders.isEmpty());
    }

    @Test
    public void getFilledOrdersWithNoSubmittedOrder() {
        StopOrderBuilder stopOrderBuilder = new StopOrderBuilder();
        stopOrderBuilder.withType(StopOrderType.STOP_LOSS);
        stopOrderBuilder.withStatus(StopOrderStatus.CREATED);
        stopOrders.put(1, stopOrderBuilder.build());

        priceMap.put("AUDUSD", new PriceDto(LocalDateTime.MIN, BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN, "AUDUSD"));
        Set<StopOrderDto> filledOrders = stopOrderPriceMonitor.getFilledOrders(priceMap, stopOrders);
        assertTrue(filledOrders.isEmpty());
    }

    @Test
    public void getFilledOrdersWith2FilledOrderFor1Position() {
        StopOrderBuilder stopOrderBuilder = new StopOrderBuilder();
        stopOrderBuilder.withType(StopOrderType.STOP_LOSS);
        stopOrderBuilder.withStatus(StopOrderStatus.SUBMITTED);
        stopOrders.put(1, stopOrderBuilder.build());

        stopOrderBuilder.withType(StopOrderType.TAKE_PROFIT);
        stopOrders.put(2, stopOrderBuilder.build());

        priceMap.put("AUDUSD", new PriceDto(LocalDateTime.MIN, BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN, "AUDUSD"));

        Set<StopOrderDto> filledOrders = stopOrderPriceMonitor.getFilledOrders(priceMap, stopOrders);
        assertEquals(1, filledOrders.size());
    }

    @Test
    public void getFilledOrdersWithBuyStopLossFilled() {
        StopOrderBuilder stopOrderBuilder = new StopOrderBuilder();
        stopOrderBuilder.withType(StopOrderType.STOP_LOSS);
        stopOrderBuilder.withStatus(StopOrderStatus.SUBMITTED);
        StopOrderDto stopLoss = stopOrderBuilder.build();
        stopOrders.put(1, stopLoss);

        stopOrderBuilder.withType(StopOrderType.TAKE_PROFIT);
        stopOrders.put(2, stopOrderBuilder.build());

        priceMap.put("AUDUSD", new PriceDto(LocalDateTime.MIN, BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN, "AUDUSD"));

        Set<StopOrderDto> filledOrders = stopOrderPriceMonitor.getFilledOrders(priceMap, stopOrders);
        assertEquals(1, filledOrders.size());
        assertEquals(stopLoss.getType(), filledOrders.iterator().next().getType());
    }


    @Test
    public void getFilledOrdersWithBuyTakeProfitFilled() {
        StopOrderBuilder stopOrderBuilder = new StopOrderBuilder();
        stopOrderBuilder.withType(StopOrderType.STOP_LOSS);
        stopOrderBuilder.withStatus(StopOrderStatus.SUBMITTED);
        StopOrderDto stopLoss = stopOrderBuilder.build();
        stopOrders.put(1, stopLoss);

        stopOrderBuilder.withType(StopOrderType.TAKE_PROFIT);
        stopOrders.put(2, stopOrderBuilder.build());

        priceMap.put("AUDUSD", new PriceDto(LocalDateTime.MIN, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, "AUDUSD"));

        Set<StopOrderDto> filledOrders = stopOrderPriceMonitor.getFilledOrders(priceMap, stopOrders);
        assertEquals(1, filledOrders.size());
        assertEquals(StopOrderType.TAKE_PROFIT, filledOrders.iterator().next().getType());
    }

    @Test
    public void getFilledOrdersWithSellTakeProfitFilled() {
        StopOrderBuilder stopOrderBuilder = new StopOrderBuilder();
        stopOrderBuilder.withType(StopOrderType.STOP_LOSS);
        stopOrderBuilder.withAction(OrderAction.SELL);
        stopOrderBuilder.withStatus(StopOrderStatus.SUBMITTED);
        StopOrderDto stopLoss = stopOrderBuilder.build();
        stopOrders.put(1, stopLoss);

        stopOrderBuilder.withType(StopOrderType.TAKE_PROFIT);
        stopOrders.put(2, stopOrderBuilder.build());

        priceMap.put("AUDUSD", new PriceDto(LocalDateTime.MIN, BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN, "AUDUSD"));

        Set<StopOrderDto> filledOrders = stopOrderPriceMonitor.getFilledOrders(priceMap, stopOrders);
        assertEquals(1, filledOrders.size());
        assertEquals(StopOrderType.TAKE_PROFIT, filledOrders.iterator().next().getType());
    }


    @Test
    public void getFilledOrdersWithSellStopLossFilled() {
        StopOrderBuilder stopOrderBuilder = new StopOrderBuilder();
        stopOrderBuilder.withType(StopOrderType.STOP_LOSS);
        stopOrderBuilder.withAction(OrderAction.SELL);
        stopOrderBuilder.withStatus(StopOrderStatus.SUBMITTED);
        StopOrderDto stopLoss = stopOrderBuilder.build();
        stopOrders.put(1, stopLoss);

        stopOrderBuilder.withType(StopOrderType.TAKE_PROFIT);
        stopOrders.put(2, stopOrderBuilder.build());

        priceMap.put("AUDUSD", new PriceDto(LocalDateTime.MIN, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, "AUDUSD"));

        Set<StopOrderDto> filledOrders = stopOrderPriceMonitor.getFilledOrders(priceMap, stopOrders);
        assertEquals(1, filledOrders.size());
        assertEquals(StopOrderType.STOP_LOSS, filledOrders.iterator().next().getType());
    }
}