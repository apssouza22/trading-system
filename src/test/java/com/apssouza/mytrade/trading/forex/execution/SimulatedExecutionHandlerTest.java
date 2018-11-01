package com.apssouza.mytrade.trading.forex.execution;

import com.apssouza.mytrade.feed.price.PriceDto;
import com.apssouza.mytrade.trading.builder.OrderBuilder;
import com.apssouza.mytrade.trading.builder.StopOrderBuilder;
import com.apssouza.mytrade.trading.forex.order.*;
import com.apssouza.mytrade.trading.forex.portfolio.FilledOrderDto;
import com.apssouza.mytrade.trading.misc.helper.config.Properties;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.apssouza.mytrade.trading.forex.order.StopOrderType.STOP_LOSS;

@RunWith(MockitoJUnitRunner.class)
public class SimulatedExecutionHandlerTest extends TestCase {
    SimulatedExecutionHandler simulatedExecutionHandler;
    private PriceDto price;
    private HashMap<String, PriceDto> priceDtoMap = new HashMap<>();

    @Before
    public void setUp() throws Exception {
        simulatedExecutionHandler = new SimulatedExecutionHandler();
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
        Properties.trading_position_edit_enabled = false;
        OrderBuilder orderBuilder = new OrderBuilder();
        OrderDto order = orderBuilder.build();
        OrderDto order2 = orderBuilder.build();
        FilledOrderDto filledOrderDto = simulatedExecutionHandler.executeOrder(order);
        simulatedExecutionHandler.executeOrder(order2);
    }

    @Test
    public void executeOrderBuyAddingUnits() {
        Properties.trading_position_edit_enabled = true;
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
        assertEquals(2 * order.getQuantity(), portfolio.get(filledOrderDto2.getIdentifier()).getQuantity());
    }

    @Test
    public void executeOrderSellAddingUnits() {
        Properties.trading_position_edit_enabled = true;
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
        assertEquals(2 * order.getQuantity(), portfolio.get(filledOrderDto2.getIdentifier()).getQuantity());
    }

    @Test
    public void executeOrderSellCounterAction() {
        Properties.trading_position_edit_enabled = true;
        OrderBuilder orderBuilder = new OrderBuilder();
        OrderDto orderBuy = orderBuilder.build();
        OrderDto orderSell = orderBuilder.setAction(OrderAction.SELL).build();
        FilledOrderDto filledOrderDto = simulatedExecutionHandler.executeOrder(orderBuy);
        FilledOrderDto filledOrderDto2 = simulatedExecutionHandler.executeOrder(orderSell);

        Map<String, FilledOrderDto> portfolio = simulatedExecutionHandler.getPortfolio();
        assertTrue(portfolio.isEmpty());
//        assertEquals(2 *  order.getQuantity(),  portfolio.get(filledOrderDto2.getIdentifier()).getQuantity());
    }

    @Test
    public void executeOrderSellCounterActionDifferentQtd() {
        Properties.trading_position_edit_enabled = true;
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
        Properties.trading_position_edit_enabled = true;
        OrderBuilder orderBuilder = new OrderBuilder();
        OrderDto orderBuy = orderBuilder.build();
        OrderDto orderSell = orderBuilder.setAction(OrderAction.SELL).build();
        FilledOrderDto filledOrderDto = simulatedExecutionHandler.executeOrder(orderSell);
        FilledOrderDto filledOrderDto2 = simulatedExecutionHandler.executeOrder(orderBuy);

        Map<String, FilledOrderDto> portfolio = simulatedExecutionHandler.getPortfolio();
        assertTrue(portfolio.isEmpty());
    }

    @Test
    public void executeOrderBuyCounterActionDifferentQtd() {
        Properties.trading_position_edit_enabled = true;
        OrderBuilder orderBuilder = new OrderBuilder();
        OrderDto orderBuy = orderBuilder.build();
        OrderDto orderSell = orderBuilder.setAction(OrderAction.SELL).setQtd(100).build();
        simulatedExecutionHandler.executeOrder(orderSell);
        simulatedExecutionHandler.executeOrder(orderBuy);

        Map<String, FilledOrderDto> portfolio = simulatedExecutionHandler.getPortfolio();
        assertEquals(1, portfolio.size());
        assertEquals(900, portfolio.get(orderBuy.getSymbol()).getQuantity());
    }

    @Test
    public void getStopLossOrders() {
        StopOrderBuilder stopOrderBuilder = new StopOrderBuilder();
        stopOrderBuilder.setType(StopOrderType.HARD_STOP);
        StopOrderDto order = stopOrderBuilder.build();
        StopOrderDto stopOrderDto = simulatedExecutionHandler.placeStopOrder(order);
        Map<Integer, StopOrderDto> stopLossOrders = simulatedExecutionHandler.getStopLossOrders();
        assertEquals(stopOrderDto, stopLossOrders.get(stopOrderDto.getId()));
    }

    @Test
    public void getLimitOrders() {
        assertTrue(simulatedExecutionHandler.getLimitOrders().isEmpty());
    }

    @Test
    public void placeStopOrder() {
        StopOrderBuilder stopOrderBuilder = new StopOrderBuilder();
        stopOrderBuilder.setType(StopOrderType.HARD_STOP);
        StopOrderDto order = stopOrderBuilder.build();
        StopOrderDto stopOrderDto = simulatedExecutionHandler.placeStopOrder(order);
        assertEquals(order.getType(), stopOrderDto.getType());
        assertEquals(order.getAction(), stopOrderDto.getAction());
        assertEquals(order.getPrice(), stopOrderDto.getPrice());
        assertEquals(order.getQuantity(), stopOrderDto.getQuantity());
        assertTrue(order.getId() != null);
    }

    @Test
    public void cancelOpenStopOrdersWithNoOrders() {
        assertEquals(Integer.valueOf(0), simulatedExecutionHandler.cancelOpenStopOrders());
    }

    @Test
    public void cancelOpenStopOrdersWithOrders() {
        StopOrderBuilder stopOrderBuilder = new StopOrderBuilder();
        stopOrderBuilder.setType(StopOrderType.HARD_STOP);
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
    public void processStopOrderBuySameQtd() {
        OrderBuilder orderBuilder = new OrderBuilder();
        OrderDto orderSell = orderBuilder.setAction(OrderAction.SELL).build();
        simulatedExecutionHandler.executeOrder(orderSell);

        StopOrderBuilder stopOrderBuilder = new StopOrderBuilder();
        stopOrderBuilder.setPrice(BigDecimal.ZERO);
        stopOrderBuilder.setType(STOP_LOSS);
        stopOrderBuilder.setQtd(1000);
        StopOrderDto order = stopOrderBuilder.build();
        simulatedExecutionHandler.placeStopOrder(order);
        simulatedExecutionHandler.processStopOrders();
        Map<String, FilledOrderDto> portfolio = simulatedExecutionHandler.getPortfolio();
        assertEquals(0, portfolio.size());
    }

    @Test
    public void processStopOrderBuyDiffQtd() {
        OrderBuilder orderBuilder = new OrderBuilder();
        OrderDto orderSell = orderBuilder.setAction(OrderAction.SELL).build();
        simulatedExecutionHandler.executeOrder(orderSell);

        StopOrderBuilder stopOrderBuilder = new StopOrderBuilder();
        stopOrderBuilder.setPrice(BigDecimal.ZERO);
        stopOrderBuilder.setType(STOP_LOSS);
        StopOrderDto order = stopOrderBuilder.build();
        simulatedExecutionHandler.placeStopOrder(order);
        simulatedExecutionHandler.processStopOrders();
        Map<String, FilledOrderDto> portfolio = simulatedExecutionHandler.getPortfolio();
        assertEquals(1, portfolio.size());
        assertEquals(900, portfolio.get(order.getIdentifier()).getQuantity());
    }

    @Test
    public void processStopOrderAmendingQtd() {
        OrderBuilder orderBuilder = new OrderBuilder();
        OrderDto orderBuy = orderBuilder.build();
        simulatedExecutionHandler.executeOrder(orderBuy);

        StopOrderBuilder stopOrderBuilder = new StopOrderBuilder();
        stopOrderBuilder.setPrice(BigDecimal.ZERO);
        stopOrderBuilder.setType(STOP_LOSS);
        stopOrderBuilder.setAction(OrderAction.BUY);
        stopOrderBuilder.setQtd(1000);
        StopOrderDto order = stopOrderBuilder.build();
        simulatedExecutionHandler.placeStopOrder(order);
        simulatedExecutionHandler.processStopOrders();
        Map<String, FilledOrderDto> portfolio = simulatedExecutionHandler.getPortfolio();
        assertEquals(1, portfolio.size());
        assertEquals(2000, portfolio.get(order.getIdentifier()).getQuantity());
    }


}