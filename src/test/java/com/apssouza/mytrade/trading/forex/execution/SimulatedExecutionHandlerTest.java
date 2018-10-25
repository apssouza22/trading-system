package com.apssouza.mytrade.trading.forex.execution;

import com.apssouza.mytrade.feed.price.PriceDto;
import com.apssouza.mytrade.feed.price.PriceHandler;
import com.apssouza.mytrade.trading.builder.OrderBuilder;
import com.apssouza.mytrade.trading.forex.order.OrderAction;
import com.apssouza.mytrade.trading.forex.order.OrderDto;
import com.apssouza.mytrade.trading.forex.order.OrderStatus;
import com.apssouza.mytrade.trading.forex.portfolio.FilledOrderDto;
import com.apssouza.mytrade.trading.misc.helper.config.Properties;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class SimulatedExecutionHandlerTest extends TestCase {
    SimulatedExecutionHandler simulatedExecutionHandler;
    private PriceDto price;
    private HashMap<String,PriceDto> priceDtoMap = new HashMap<>();

    @Before
    public void setUp() throws Exception {
        simulatedExecutionHandler = new SimulatedExecutionHandler();
        this.price = new PriceDto(LocalDateTime.MIN, BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN, "AUDUSD");
        this.priceDtoMap.put("AUDUSD", price);
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
    public void executeOrderNewOrder() {
        OrderBuilder orderBuilder = new OrderBuilder();
        OrderDto order = orderBuilder.build();
        FilledOrderDto filledOrderDto = simulatedExecutionHandler.executeOrder(order);
        assertEquals(order.getSymbol(), filledOrderDto.getSymbol());
        assertEquals(order.getIdentifier(), filledOrderDto.getIdentifier());
        assertEquals(order.getAction(), filledOrderDto.getAction());
        assertEquals(order.getQuantity(), filledOrderDto.getQuantity());
    }

    @Test(expected = RuntimeException.class)
    public void executeOrderAddingUnitsWhenEditNotEnabled() {
        OrderBuilder orderBuilder = new OrderBuilder();
        OrderDto order = orderBuilder.build();
        OrderDto order2 = orderBuilder.build();
        FilledOrderDto filledOrderDto = simulatedExecutionHandler.executeOrder(order);
        simulatedExecutionHandler.executeOrder(order2);
    }

    @Test
    public void executeOrderBuyAddingUnits() {
        Properties.trading_position_edit_enabled=true;
        OrderBuilder orderBuilder = new OrderBuilder();
        OrderDto order = orderBuilder.build();
        OrderDto order2 = orderBuilder.build();
        FilledOrderDto filledOrderDto = simulatedExecutionHandler.executeOrder(order);
        FilledOrderDto filledOrderDto2 = simulatedExecutionHandler.executeOrder(order2);
        assertEquals(order.getSymbol(), filledOrderDto2.getSymbol());
        assertEquals(order.getIdentifier(), filledOrderDto2.getIdentifier());
        assertEquals(order.getAction(), filledOrderDto2.getAction());
        assertEquals(order.getQuantity(), filledOrderDto2.getQuantity());

        Map<String, FilledOrderDto> portfolio = simulatedExecutionHandler.getPortfolio();
        assertEquals(1, portfolio.size());
        assertEquals(2 *  order.getQuantity(),  portfolio.get(filledOrderDto2.getIdentifier()).getQuantity());
    }

    @Test
    public void executeOrderSellAddingUnits() {
        Properties.trading_position_edit_enabled=true;
        OrderBuilder orderBuilder = new OrderBuilder();
        OrderDto order = orderBuilder.addOrder(LocalDateTime.MIN, OrderAction.SELL, OrderStatus.CREATED).build();
        OrderDto order2 = orderBuilder.build();
        FilledOrderDto filledOrderDto = simulatedExecutionHandler.executeOrder(order);
        FilledOrderDto filledOrderDto2 = simulatedExecutionHandler.executeOrder(order2);
        assertEquals(order.getSymbol(), filledOrderDto2.getSymbol());
        assertEquals(order.getIdentifier(), filledOrderDto2.getIdentifier());
        assertEquals(order.getAction(), filledOrderDto2.getAction());
        assertEquals(order.getQuantity(), filledOrderDto2.getQuantity());

        Map<String, FilledOrderDto> portfolio = simulatedExecutionHandler.getPortfolio();
        assertEquals(1, portfolio.size());
        assertEquals(2 *  order.getQuantity(),  portfolio.get(filledOrderDto2.getIdentifier()).getQuantity());
    }

    @Test
    public void executeOrderSellCounterAction() {
        Properties.trading_position_edit_enabled=true;
        OrderBuilder orderBuilder = new OrderBuilder();
        OrderDto orderBuy = orderBuilder.build();
        OrderDto orderSell = orderBuilder.setAction(OrderAction.SELL).build();
        FilledOrderDto filledOrderDto = simulatedExecutionHandler.executeOrder(orderBuy);
        FilledOrderDto filledOrderDto2 = simulatedExecutionHandler.executeOrder(orderSell);

        Map<String, FilledOrderDto> portfolio = simulatedExecutionHandler.getPortfolio();
        assertTrue( portfolio.isEmpty());
//        assertEquals(2 *  order.getQuantity(),  portfolio.get(filledOrderDto2.getIdentifier()).getQuantity());
    }

    @Test
    public void executeOrderSellCounterActionDifferentQtd() {
        Properties.trading_position_edit_enabled=true;
        OrderBuilder orderBuilder = new OrderBuilder();
        OrderDto orderBuy = orderBuilder.build();
        OrderDto orderSell = orderBuilder.setAction(OrderAction.SELL).setQtd(100).build();
        FilledOrderDto filledOrderDto = simulatedExecutionHandler.executeOrder(orderBuy);
        FilledOrderDto filledOrderDto2 = simulatedExecutionHandler.executeOrder(orderSell);

        Map<String, FilledOrderDto> portfolio = simulatedExecutionHandler.getPortfolio();
        assertEquals(1, portfolio.size());
        assertEquals(900, portfolio.get(orderBuy.getSymbol()).getQuantity());
//        assertEquals(2 *  order.getQuantity(),  portfolio.get(filledOrderDto2.getIdentifier()).getQuantity());
    }

    @Test
    public void executeOrderBuyCounterAction() {
        Properties.trading_position_edit_enabled=true;
        OrderBuilder orderBuilder = new OrderBuilder();
        OrderDto orderBuy = orderBuilder.build();
        OrderDto orderSell = orderBuilder.setAction(OrderAction.SELL).build();
        FilledOrderDto filledOrderDto = simulatedExecutionHandler.executeOrder(orderSell);
        FilledOrderDto filledOrderDto2 = simulatedExecutionHandler.executeOrder(orderBuy);

        Map<String, FilledOrderDto> portfolio = simulatedExecutionHandler.getPortfolio();
        assertTrue( portfolio.isEmpty());
//        assertEquals(2 *  order.getQuantity(),  portfolio.get(filledOrderDto2.getIdentifier()).getQuantity());
    }

    @Test
    public void executeOrderBuyCounterActionDifferentQtd() {
        Properties.trading_position_edit_enabled=true;
        OrderBuilder orderBuilder = new OrderBuilder();
        OrderDto orderBuy = orderBuilder.build();
        OrderDto orderSell = orderBuilder.setAction(OrderAction.SELL).setQtd(100).build();
        simulatedExecutionHandler.executeOrder(orderSell);
        simulatedExecutionHandler.executeOrder(orderBuy);

        Map<String, FilledOrderDto> portfolio = simulatedExecutionHandler.getPortfolio();
        assertEquals(1, portfolio.size());
        assertEquals(900, portfolio.get(orderBuy.getSymbol()).getQuantity());
//        assertEquals(2 *  order.getQuantity(),  portfolio.get(filledOrderDto2.getIdentifier()).getQuantity());
    }

    @Test
    public void handleMultiPairPositionPortfolio() {
    }

    @Test
    public void getStopLossOrders() {
    }

    @Test
    public void getLimitOrders() {
    }

    @Test
    public void placeStopOrder() {
    }

    @Test
    public void cancelOpenStopOrders() {
    }

    @Test
    public void cancelOpenLimitOrders() {
    }

    @Test
    public void deleteStopOrders() {
    }

    @Test
    public void processStopOrderWithPrices() {
    }

    @Test
    public void changeLocalPosition() {
    }
}