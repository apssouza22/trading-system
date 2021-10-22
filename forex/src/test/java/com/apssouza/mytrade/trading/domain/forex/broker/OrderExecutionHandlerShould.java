package com.apssouza.mytrade.trading.domain.forex.broker;

import com.apssouza.mytrade.feed.api.PriceDto;
import com.apssouza.mytrade.trading.api.ExecutionType;
import com.apssouza.mytrade.trading.domain.forex.order.OrderBuilder;
import com.apssouza.mytrade.trading.domain.forex.order.StopOrderBuilder;
import com.apssouza.mytrade.trading.domain.forex.order.*;
import com.apssouza.mytrade.trading.domain.forex.portfolio.FilledOrderDto;
import com.apssouza.mytrade.trading.domain.forex.common.TradingParams;
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

import static com.apssouza.mytrade.trading.domain.forex.risk.stoporder.StopOrderDto.StopOrderType.STOP_LOSS;

@RunWith(MockitoJUnitRunner.class)
public class OrderExecutionHandlerShould extends TestCase {
    OrderExecution simulatedExecutionHandler;
    private PriceDto price;
    private HashMap<String, PriceDto> priceDtoMap = new HashMap<>();

    @Before
    public void setUp() throws Exception {
        simulatedExecutionHandler = OrderExecutionFactory.factory(ExecutionType.SIMULATED);
        this.price = new PriceDto(LocalDateTime.MIN, BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN, "AUDUSD");
        this.priceDtoMap.put("AUDUSD", price);
        this.priceDtoMap.put("EURUSD", price);
        simulatedExecutionHandler.setCurrentTime(LocalDateTime.MIN);
        simulatedExecutionHandler.setPriceMap(priceDtoMap);
    }

    @Test
    public void getPortfolio() {
        assertTrue(simulatedExecutionHandler.getPortfolio().isEmpty());
        OrderBuilder orderBuilder = new OrderBuilder();
        OrderDto build = orderBuilder.build();
        simulatedExecutionHandler.executeOrder(build);
        assertEquals(1, simulatedExecutionHandler.getPortfolio().size());
    }

    @Test
    public void setCurrentTime() {
        simulatedExecutionHandler.setCurrentTime(LocalDateTime.MIN);
    }

    @Test
    public void setPriceMap() {
        simulatedExecutionHandler.setPriceMap(priceDtoMap);
    }

    @Test
    public void closeAllPositions() {
        simulatedExecutionHandler.closeAllPositions();
    }

    @Test
    public void executeOrder_NewOrder() {
        OrderBuilder orderBuilder = new OrderBuilder();
        OrderDto order = orderBuilder.build();
        FilledOrderDto filledOrderDto = simulatedExecutionHandler.executeOrder(order);
        assertEquals(order.symbol(), filledOrderDto.symbol());
        assertEquals(order.identifier(), filledOrderDto.identifier());
        assertEquals(order.action(), filledOrderDto.action());
        assertEquals(order.quantity(), filledOrderDto.quantity());
    }

    @Test(expected = RuntimeException.class)
    public void executeOrder_AddingUnitsWhenEditNotEnabled() {
        TradingParams.trading_position_edit_enabled = false;
        OrderBuilder orderBuilder = new OrderBuilder();
        OrderDto order = orderBuilder.build();
        OrderDto order2 = orderBuilder.build();
        simulatedExecutionHandler.executeOrder(order);
        simulatedExecutionHandler.executeOrder(order2);
    }

    @Test
    public void executeOrder_BuyAddingUnits() {
        TradingParams.trading_position_edit_enabled = true;
        OrderBuilder orderBuilder = new OrderBuilder();
        OrderDto order = orderBuilder.build();
        OrderDto order2 = orderBuilder.build();
        FilledOrderDto filledOrderDto = simulatedExecutionHandler.executeOrder(order);
        FilledOrderDto filledOrderDto2 = simulatedExecutionHandler.executeOrder(order2);
        assertEquals(order.symbol(), filledOrderDto2.symbol());
        assertEquals(order.identifier(), filledOrderDto2.identifier());
        assertEquals(order.action(), filledOrderDto2.action());
        assertEquals(order.quantity(), filledOrderDto2.quantity());

        Map<String, FilledOrderDto> portfolio = simulatedExecutionHandler.getPortfolio();
        assertEquals(1, portfolio.size());
        assertEquals(2 * order.quantity(), portfolio.get(filledOrderDto2.identifier()).quantity());
    }

    @Test
    public void executeOrder_SellAddingUnits() {
        TradingParams.trading_position_edit_enabled = true;
        OrderBuilder orderBuilder = new OrderBuilder();
        OrderDto order = orderBuilder.withOrder(LocalDateTime.MIN, OrderDto.OrderAction.SELL, OrderDto.OrderStatus.CREATED).build();
        OrderDto order2 = orderBuilder.build();
        simulatedExecutionHandler.executeOrder(order);
        FilledOrderDto filledOrderDto2 = simulatedExecutionHandler.executeOrder(order2);
        assertEquals(order.symbol(), filledOrderDto2.symbol());
        assertEquals(order.identifier(), filledOrderDto2.identifier());
        assertEquals(order.action(), filledOrderDto2.action());
        assertEquals(order.quantity(), filledOrderDto2.quantity());

        Map<String, FilledOrderDto> portfolio = simulatedExecutionHandler.getPortfolio();
        assertEquals(1, portfolio.size());
        assertEquals(2 * order.quantity(), portfolio.get(filledOrderDto2.identifier()).quantity());
    }

    @Test
    public void executeOrder_SellCounterAction() {
        TradingParams.trading_position_edit_enabled = true;
        OrderBuilder orderBuilder = new OrderBuilder();
        OrderDto orderBuy = orderBuilder.build();
        OrderDto orderSell = orderBuilder.withAction(OrderDto.OrderAction.SELL).build();
        simulatedExecutionHandler.executeOrder(orderBuy);
        simulatedExecutionHandler.executeOrder(orderSell);

        Map<String, FilledOrderDto> portfolio = simulatedExecutionHandler.getPortfolio();
        assertTrue(portfolio.isEmpty());
    }

    @Test
    public void executeOrder_SellCounterActionDifferentQtd() {
        TradingParams.trading_position_edit_enabled = true;
        OrderBuilder orderBuilder = new OrderBuilder();
        OrderDto orderBuy = orderBuilder.build();
        OrderDto orderSell = orderBuilder.withAction(OrderDto.OrderAction.SELL).withQtd(100).build();
        simulatedExecutionHandler.executeOrder(orderBuy);
        simulatedExecutionHandler.executeOrder(orderSell);

        Map<String, FilledOrderDto> portfolio = simulatedExecutionHandler.getPortfolio();
        assertEquals(1, portfolio.size());
        assertEquals(900, portfolio.get(orderBuy.symbol()).quantity());
    }

    @Test
    public void executeOrder_BuyCounterAction() {
        TradingParams.trading_position_edit_enabled = true;
        OrderBuilder orderBuilder = new OrderBuilder();
        OrderDto orderBuy = orderBuilder.build();
        OrderDto orderSell = orderBuilder.withAction(OrderDto.OrderAction.SELL).build();
        simulatedExecutionHandler.executeOrder(orderSell);
        simulatedExecutionHandler.executeOrder(orderBuy);

        Map<String, FilledOrderDto> portfolio = simulatedExecutionHandler.getPortfolio();
        assertTrue(portfolio.isEmpty());
    }

    @Test
    public void executeOrder_BuyCounterActionDifferentQtd() {
        TradingParams.trading_position_edit_enabled = true;
        OrderBuilder orderBuilder = new OrderBuilder();
        OrderDto orderBuy = orderBuilder.build();
        OrderDto orderSell = orderBuilder.withAction(OrderDto.OrderAction.SELL).withQtd(100).build();
        simulatedExecutionHandler.executeOrder(orderSell);
        simulatedExecutionHandler.executeOrder(orderBuy);

        Map<String, FilledOrderDto> portfolio = simulatedExecutionHandler.getPortfolio();
        assertEquals(1, portfolio.size());
        assertEquals(900, portfolio.get(orderBuy.symbol()).quantity());
    }

    @Test
    public void getStopLossOrders() {
        StopOrderBuilder stopOrderBuilder = new StopOrderBuilder();
        stopOrderBuilder.withType(StopOrderDto.StopOrderType.HARD_STOP);
        StopOrderDto order = stopOrderBuilder.build();
        StopOrderDto stopOrderDto = simulatedExecutionHandler.placeStopOrder(order);
        Map<Integer, StopOrderDto> stopLossOrders = simulatedExecutionHandler.getStopLossOrders();
        assertEquals(stopOrderDto, stopLossOrders.get(stopOrderDto.id()));
    }

    @Test
    public void getLimitOrders() {
        assertTrue(simulatedExecutionHandler.getLimitOrders().isEmpty());
    }

    @Test
    public void placeStopOrder() {
        StopOrderBuilder stopOrderBuilder = new StopOrderBuilder();
        stopOrderBuilder.withType(StopOrderDto.StopOrderType.HARD_STOP);
        StopOrderDto order = stopOrderBuilder.build();
        StopOrderDto stopOrderDto = simulatedExecutionHandler.placeStopOrder(order);
        assertEquals(order.type(), stopOrderDto.type());
        assertEquals(order.action(), stopOrderDto.action());
        assertEquals(order.price(), stopOrderDto.price());
        assertEquals(order.quantity(), stopOrderDto.quantity());
        assertTrue(order.id() != null);
    }

    @Test
    public void cancelOpenStopOrders_WithNoOrders() {
        assertEquals(Integer.valueOf(0), simulatedExecutionHandler.cancelOpenStopOrders());
    }

    @Test
    public void cancelOpenStopOrders_WithOrders() {
        StopOrderBuilder stopOrderBuilder = new StopOrderBuilder();
        stopOrderBuilder.withType(StopOrderDto.StopOrderType.HARD_STOP);
        StopOrderDto order = stopOrderBuilder.build();
        simulatedExecutionHandler.placeStopOrder(order);
        assertEquals(Integer.valueOf(1), simulatedExecutionHandler.cancelOpenStopOrders());
    }

    @Test
    public void cancelOpenLimitOrders() {
        assertEquals(Integer.valueOf(0), simulatedExecutionHandler.cancelOpenStopOrders());
    }

    @Test
    public void deleteStopOrders() {
        StopOrderBuilder stopOrderBuilder = new StopOrderBuilder();
        StopOrderDto order = stopOrderBuilder.build();
        simulatedExecutionHandler.placeStopOrder(order);
        simulatedExecutionHandler.deleteStopOrders();
        assertTrue(simulatedExecutionHandler.getStopLossOrders().isEmpty());
    }

    @Test
    public void processStopOrders_BuySameQtd() {
        OrderBuilder orderBuilder = new OrderBuilder();
        OrderDto orderSell = orderBuilder.withAction(OrderDto.OrderAction.SELL).build();
        simulatedExecutionHandler.executeOrder(orderSell);

        StopOrderBuilder stopOrderBuilder = new StopOrderBuilder();
        stopOrderBuilder.withPrice(BigDecimal.ZERO);
        stopOrderBuilder.withType(STOP_LOSS);
        stopOrderBuilder.withQtd(1000);
        StopOrderDto order = stopOrderBuilder.build();
        simulatedExecutionHandler.placeStopOrder(order);
        simulatedExecutionHandler.processStopOrders();
        Map<String, FilledOrderDto> portfolio = simulatedExecutionHandler.getPortfolio();
        assertEquals(0, portfolio.size());
    }

    @Test
    public void processStopOrders_BuyDiffQtd() {
        OrderBuilder orderBuilder = new OrderBuilder();
        OrderDto orderSell = orderBuilder.withAction(OrderDto.OrderAction.SELL).build();
        simulatedExecutionHandler.executeOrder(orderSell);

        StopOrderBuilder stopOrderBuilder = new StopOrderBuilder();
        stopOrderBuilder.withPrice(BigDecimal.ZERO);
        stopOrderBuilder.withType(STOP_LOSS);
        StopOrderDto order = stopOrderBuilder.build();
        simulatedExecutionHandler.placeStopOrder(order);
        simulatedExecutionHandler.processStopOrders();
        Map<String, FilledOrderDto> portfolio = simulatedExecutionHandler.getPortfolio();
        assertEquals(1, portfolio.size());
        assertEquals(900, portfolio.get(order.identifier()).quantity());
    }

    @Test
    public void processStopOrder_AmendingQtd() {
        OrderBuilder orderBuilder = new OrderBuilder();
        OrderDto orderBuy = orderBuilder.build();
        simulatedExecutionHandler.executeOrder(orderBuy);

        StopOrderBuilder stopOrderBuilder = new StopOrderBuilder();
        stopOrderBuilder.withPrice(BigDecimal.ZERO);
        stopOrderBuilder.withType(STOP_LOSS);
        stopOrderBuilder.withAction(OrderDto.OrderAction.BUY);
        stopOrderBuilder.withQtd(1000);
        StopOrderDto order = stopOrderBuilder.build();
        simulatedExecutionHandler.placeStopOrder(order);
        simulatedExecutionHandler.processStopOrders();
        Map<String, FilledOrderDto> portfolio = simulatedExecutionHandler.getPortfolio();
        assertEquals(1, portfolio.size());
        assertEquals(2000, portfolio.get(order.identifier()).quantity());
    }

}