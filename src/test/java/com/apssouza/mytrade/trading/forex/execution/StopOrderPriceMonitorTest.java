package com.apssouza.mytrade.trading.forex.execution;

import com.apssouza.mytrade.feed.price.PriceDto;
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
import java.util.*;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class StopOrderPriceMonitorTest extends TestCase {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void getFilledOrdersWithNoStopOrders() {
        Map<Integer, StopOrderDto>  stopOrders = new HashMap<>();
        Map<String, PriceDto> priceMap = new HashMap<>();
        StopOrderPriceMonitor stopOrderPriceMonitor = new StopOrderPriceMonitor(stopOrders, priceMap);
        Set<StopOrderDto> filledOrders = stopOrderPriceMonitor.getFilledOrders();
        assertTrue(filledOrders.isEmpty());
    }

    @Test
    public void getFilledOrdersWithNoSubmittedOrder() {
        Map<Integer, StopOrderDto>  stopOrders = new HashMap<>();
        StopOrderBuilder stopOrderBuilder = new StopOrderBuilder();
        stopOrderBuilder.setType(StopOrderType.STOP_LOSS);
        stopOrderBuilder.setStatus(StopOrderStatus.CREATED);
        stopOrders.put(1, stopOrderBuilder.build());

        Map<String, PriceDto> priceMap = new HashMap<>();
        priceMap.put("AUDUSD", new PriceDto(LocalDateTime.MIN, BigDecimal.TEN,BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN,"AUDUSD"));

        StopOrderPriceMonitor stopOrderPriceMonitor = new StopOrderPriceMonitor(stopOrders, priceMap);
        Set<StopOrderDto> filledOrders = stopOrderPriceMonitor.getFilledOrders();
        assertTrue(filledOrders.isEmpty());
    }

    @Test
    public void getFilledOrdersWith2FilledOrderFor1Position() {
        Map<Integer, StopOrderDto>  stopOrders = new HashMap<>();
        StopOrderBuilder stopOrderBuilder = new StopOrderBuilder();
        stopOrderBuilder.setType(StopOrderType.STOP_LOSS);
        stopOrderBuilder.setStatus(StopOrderStatus.SUBMITTED);
        stopOrders.put(1, stopOrderBuilder.build());

        stopOrderBuilder.setType(StopOrderType.TAKE_PROFIT);
        stopOrders.put(2, stopOrderBuilder.build());

        Map<String, PriceDto> priceMap = new HashMap<>();
        priceMap.put("AUDUSD", new PriceDto(LocalDateTime.MIN, BigDecimal.TEN,BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN,"AUDUSD"));

        StopOrderPriceMonitor stopOrderPriceMonitor = new StopOrderPriceMonitor(stopOrders, priceMap);
        Set<StopOrderDto> filledOrders = stopOrderPriceMonitor.getFilledOrders();
        assertEquals(1, filledOrders.size());
    }

    @Test
    public void getFilledOrdersWithBuyStopLossFilled() {
        Map<Integer, StopOrderDto>  stopOrders = new HashMap<>();
        StopOrderBuilder stopOrderBuilder = new StopOrderBuilder();
        stopOrderBuilder.setType(StopOrderType.STOP_LOSS);
        stopOrderBuilder.setStatus(StopOrderStatus.SUBMITTED);
        StopOrderDto stopLoss = stopOrderBuilder.build();
        stopOrders.put(1, stopLoss);

        stopOrderBuilder.setType(StopOrderType.TAKE_PROFIT);
        stopOrders.put(2, stopOrderBuilder.build());

        Map<String, PriceDto> priceMap = new HashMap<>();
        priceMap.put("AUDUSD", new PriceDto(LocalDateTime.MIN, BigDecimal.TEN,BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN,"AUDUSD"));

        StopOrderPriceMonitor stopOrderPriceMonitor = new StopOrderPriceMonitor(stopOrders, priceMap);
        Set<StopOrderDto> filledOrders = stopOrderPriceMonitor.getFilledOrders();
        assertEquals(1, filledOrders.size());
        assertEquals(stopLoss.getType(),filledOrders.iterator().next().getType());
        assertEquals(StopOrderStatus.FILLED,filledOrders.iterator().next().getStatus());
    }


    @Test
    public void getFilledOrdersWithBuyTakeProfitFilled() {
        Map<Integer, StopOrderDto>  stopOrders = new HashMap<>();
        StopOrderBuilder stopOrderBuilder = new StopOrderBuilder();
        stopOrderBuilder.setType(StopOrderType.STOP_LOSS);
        stopOrderBuilder.setStatus(StopOrderStatus.SUBMITTED);
        StopOrderDto stopLoss = stopOrderBuilder.build();
        stopOrders.put(1, stopLoss);

        stopOrderBuilder.setType(StopOrderType.TAKE_PROFIT);
        stopOrders.put(2, stopOrderBuilder.build());

        Map<String, PriceDto> priceMap = new HashMap<>();
        priceMap.put("AUDUSD", new PriceDto(LocalDateTime.MIN, BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,"AUDUSD"));

        StopOrderPriceMonitor stopOrderPriceMonitor = new StopOrderPriceMonitor(stopOrders, priceMap);
        Set<StopOrderDto> filledOrders = stopOrderPriceMonitor.getFilledOrders();
        assertEquals(1, filledOrders.size());
        assertEquals(StopOrderType.TAKE_PROFIT,filledOrders.iterator().next().getType());
        assertEquals(StopOrderStatus.FILLED,filledOrders.iterator().next().getStatus());
    }

    @Test
    public void getFilledOrdersWithSellTakeProfitFilled() {
        Map<Integer, StopOrderDto>  stopOrders = new HashMap<>();
        StopOrderBuilder stopOrderBuilder = new StopOrderBuilder();
        stopOrderBuilder.setType(StopOrderType.STOP_LOSS);
        stopOrderBuilder.setAction(OrderAction.SELL);
        stopOrderBuilder.setStatus(StopOrderStatus.SUBMITTED);
        StopOrderDto stopLoss = stopOrderBuilder.build();
        stopOrders.put(1, stopLoss);

        stopOrderBuilder.setType(StopOrderType.TAKE_PROFIT);
        stopOrders.put(2, stopOrderBuilder.build());

        Map<String, PriceDto> priceMap = new HashMap<>();
        priceMap.put("AUDUSD", new PriceDto(LocalDateTime.MIN, BigDecimal.TEN,BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN,"AUDUSD"));

        StopOrderPriceMonitor stopOrderPriceMonitor = new StopOrderPriceMonitor(stopOrders, priceMap);
        Set<StopOrderDto> filledOrders = stopOrderPriceMonitor.getFilledOrders();
        assertEquals(1, filledOrders.size());
        assertEquals(StopOrderType.TAKE_PROFIT,filledOrders.iterator().next().getType());
        assertEquals(StopOrderStatus.FILLED,filledOrders.iterator().next().getStatus());
    }


    @Test
    public void getFilledOrdersWithSellStopLossFilled() {
        Map<Integer, StopOrderDto>  stopOrders = new HashMap<>();
        StopOrderBuilder stopOrderBuilder = new StopOrderBuilder();
        stopOrderBuilder.setType(StopOrderType.STOP_LOSS);
        stopOrderBuilder.setAction(OrderAction.SELL);
        stopOrderBuilder.setStatus(StopOrderStatus.SUBMITTED);
        StopOrderDto stopLoss = stopOrderBuilder.build();
        stopOrders.put(1, stopLoss);

        stopOrderBuilder.setType(StopOrderType.TAKE_PROFIT);
        stopOrders.put(2, stopOrderBuilder.build());

        Map<String, PriceDto> priceMap = new HashMap<>();
        priceMap.put("AUDUSD", new PriceDto(LocalDateTime.MIN, BigDecimal.ZERO,BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,"AUDUSD"));

        StopOrderPriceMonitor stopOrderPriceMonitor = new StopOrderPriceMonitor(stopOrders, priceMap);
        Set<StopOrderDto> filledOrders = stopOrderPriceMonitor.getFilledOrders();
        assertEquals(1, filledOrders.size());
        assertEquals(StopOrderType.STOP_LOSS,filledOrders.iterator().next().getType());
        assertEquals(StopOrderStatus.FILLED,filledOrders.iterator().next().getStatus());
    }
}