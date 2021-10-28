package com.apssouza.mytrade.trading.domain.forex.brokerintegration;

import com.apssouza.mytrade.feed.api.PriceDto;
import com.apssouza.mytrade.trading.api.ExecutionType;
import com.apssouza.mytrade.trading.domain.forex.order.OrderBuilder;
import com.apssouza.mytrade.trading.domain.forex.order.StopOrderBuilder;
import com.apssouza.mytrade.trading.domain.forex.order.*;
import com.apssouza.mytrade.trading.domain.forex.portfolio.FilledOrderDto;
import com.apssouza.mytrade.trading.domain.forex.common.TradingParams;
import com.apssouza.mytrade.trading.domain.forex.risk.stopordercreation.StopOrderDto;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.apssouza.mytrade.trading.domain.forex.risk.stopordercreation.StopOrderDto.StopOrderType.STOP_LOSS;

@RunWith(MockitoJUnitRunner.class)
public class BrokerIntegrationServiceShould extends TestCase {
    BrokerIntegrationService simulatedBroker;
    private PriceDto price;
    private HashMap<String, PriceDto> priceDtoMap = new HashMap<>();

    @Before
    public void setUp() throws Exception {
        simulatedBroker = BrokerIntegrationFactory.factory(ExecutionType.SIMULATED);
        this.price = new PriceDto(LocalDateTime.MIN, BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN, "AUDUSD");
        this.priceDtoMap.put("AUDUSD", price);
        this.priceDtoMap.put("EURUSD", price);
        simulatedBroker.setCurrentTime(LocalDateTime.MIN);
        simulatedBroker.setPriceMap(priceDtoMap);
    }

    @Test
    public void getPortfolio() {
        assertTrue(simulatedBroker.getPortfolio().isEmpty());
        OrderBuilder orderBuilder = new OrderBuilder();
        OrderDto build = orderBuilder.build();
        simulatedBroker.executeOrder(build);
        assertEquals(1, simulatedBroker.getPortfolio().size());
    }

    @Test
    public void setCurrentTime() {
        simulatedBroker.setCurrentTime(LocalDateTime.MIN);
    }

    @Test
    public void setPriceMap() {
        simulatedBroker.setPriceMap(priceDtoMap);
    }

    @Test
    public void closeAllPositions() {
        simulatedBroker.closeAllPositions();
    }

    @Test
    public void executeOrder_NewOrder() {
        OrderBuilder orderBuilder = new OrderBuilder();
        OrderDto order = orderBuilder.build();
        FilledOrderDto filledOrderDto = simulatedBroker.executeOrder(order);
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
        simulatedBroker.executeOrder(order);
        simulatedBroker.executeOrder(order2);
    }

    @Test
    public void executeOrder_BuyAddingUnits() {
        TradingParams.trading_position_edit_enabled = true;
        OrderBuilder orderBuilder = new OrderBuilder();
        OrderDto order = orderBuilder.build();
        OrderDto order2 = orderBuilder.build();
        FilledOrderDto filledOrderDto = simulatedBroker.executeOrder(order);
        FilledOrderDto filledOrderDto2 = simulatedBroker.executeOrder(order2);
        assertEquals(order.symbol(), filledOrderDto2.symbol());
        assertEquals(order.identifier(), filledOrderDto2.identifier());
        assertEquals(order.action(), filledOrderDto2.action());
        assertEquals(order.quantity(), filledOrderDto2.quantity());

        Map<String, FilledOrderDto> portfolio = simulatedBroker.getPortfolio();
        assertEquals(1, portfolio.size());
        assertEquals(2 * order.quantity(), portfolio.get(filledOrderDto2.identifier()).quantity());
    }

    @Test
    public void executeOrder_SellAddingUnits() {
        TradingParams.trading_position_edit_enabled = true;
        OrderBuilder orderBuilder = new OrderBuilder();
        OrderDto order = orderBuilder.withOrder(LocalDateTime.MIN, OrderDto.OrderAction.SELL, OrderDto.OrderStatus.CREATED).build();
        OrderDto order2 = orderBuilder.build();
        simulatedBroker.executeOrder(order);
        FilledOrderDto filledOrderDto2 = simulatedBroker.executeOrder(order2);
        assertEquals(order.symbol(), filledOrderDto2.symbol());
        assertEquals(order.identifier(), filledOrderDto2.identifier());
        assertEquals(order.action(), filledOrderDto2.action());
        assertEquals(order.quantity(), filledOrderDto2.quantity());

        Map<String, FilledOrderDto> portfolio = simulatedBroker.getPortfolio();
        assertEquals(1, portfolio.size());
        assertEquals(2 * order.quantity(), portfolio.get(filledOrderDto2.identifier()).quantity());
    }

    @Test
    public void executeOrder_SellCounterAction() {
        TradingParams.trading_position_edit_enabled = true;
        OrderBuilder orderBuilder = new OrderBuilder();
        OrderDto orderBuy = orderBuilder.build();
        OrderDto orderSell = orderBuilder.withAction(OrderDto.OrderAction.SELL).build();
        simulatedBroker.executeOrder(orderBuy);
        simulatedBroker.executeOrder(orderSell);

        Map<String, FilledOrderDto> portfolio = simulatedBroker.getPortfolio();
        assertTrue(portfolio.isEmpty());
    }

    @Test
    public void executeOrder_SellCounterActionDifferentQtd() {
        TradingParams.trading_position_edit_enabled = true;
        OrderBuilder orderBuilder = new OrderBuilder();
        OrderDto orderBuy = orderBuilder.build();
        OrderDto orderSell = orderBuilder.withAction(OrderDto.OrderAction.SELL).withQtd(100).build();
        simulatedBroker.executeOrder(orderBuy);
        simulatedBroker.executeOrder(orderSell);

        Map<String, FilledOrderDto> portfolio = simulatedBroker.getPortfolio();
        assertEquals(1, portfolio.size());
        assertEquals(900, portfolio.get(orderBuy.symbol()).quantity());
    }

    @Test
    public void executeOrder_BuyCounterAction() {
        TradingParams.trading_position_edit_enabled = true;
        OrderBuilder orderBuilder = new OrderBuilder();
        OrderDto orderBuy = orderBuilder.build();
        OrderDto orderSell = orderBuilder.withAction(OrderDto.OrderAction.SELL).build();
        simulatedBroker.executeOrder(orderSell);
        simulatedBroker.executeOrder(orderBuy);

        Map<String, FilledOrderDto> portfolio = simulatedBroker.getPortfolio();
        assertTrue(portfolio.isEmpty());
    }

    @Test
    public void executeOrder_BuyCounterActionDifferentQtd() {
        TradingParams.trading_position_edit_enabled = true;
        OrderBuilder orderBuilder = new OrderBuilder();
        OrderDto orderBuy = orderBuilder.build();
        OrderDto orderSell = orderBuilder.withAction(OrderDto.OrderAction.SELL).withQtd(100).build();
        simulatedBroker.executeOrder(orderSell);
        simulatedBroker.executeOrder(orderBuy);

        Map<String, FilledOrderDto> portfolio = simulatedBroker.getPortfolio();
        assertEquals(1, portfolio.size());
        assertEquals(900, portfolio.get(orderBuy.symbol()).quantity());
    }

    @Test
    public void getStopLossOrders() {
        StopOrderBuilder stopOrderBuilder = new StopOrderBuilder();
        stopOrderBuilder.withType(StopOrderDto.StopOrderType.HARD_STOP);
        StopOrderDto order = stopOrderBuilder.build();
        StopOrderDto stopOrderDto = simulatedBroker.placeStopOrder(order);
        Map<Integer, StopOrderDto> stopLossOrders = simulatedBroker.getStopLossOrders();
        assertEquals(stopOrderDto, stopLossOrders.get(stopOrderDto.id()));
    }

    @Test
    public void getLimitOrders() {
        assertTrue(simulatedBroker.getLimitOrders().isEmpty());
    }

    @Test
    public void placeStopOrder() {
        StopOrderBuilder stopOrderBuilder = new StopOrderBuilder();
        stopOrderBuilder.withType(StopOrderDto.StopOrderType.HARD_STOP);
        StopOrderDto order = stopOrderBuilder.build();
        StopOrderDto stopOrderDto = simulatedBroker.placeStopOrder(order);
        assertEquals(order.type(), stopOrderDto.type());
        assertEquals(order.action(), stopOrderDto.action());
        assertEquals(order.price(), stopOrderDto.price());
        assertEquals(order.quantity(), stopOrderDto.quantity());
        assertTrue(order.id() != null);
    }

    @Test
    public void cancelOpenStopOrders_WithNoOrders() {
        assertEquals(Integer.valueOf(0), simulatedBroker.cancelOpenStopOrders());
    }

    @Test
    public void cancelOpenStopOrders_WithOrders() {
        StopOrderBuilder stopOrderBuilder = new StopOrderBuilder();
        stopOrderBuilder.withType(StopOrderDto.StopOrderType.HARD_STOP);
        StopOrderDto order = stopOrderBuilder.build();
        simulatedBroker.placeStopOrder(order);
        assertEquals(Integer.valueOf(1), simulatedBroker.cancelOpenStopOrders());
    }

    @Test
    public void cancelOpenLimitOrders() {
        assertEquals(Integer.valueOf(0), simulatedBroker.cancelOpenStopOrders());
    }

    @Test
    public void deleteStopOrders() {
        StopOrderBuilder stopOrderBuilder = new StopOrderBuilder();
        StopOrderDto order = stopOrderBuilder.build();
        simulatedBroker.placeStopOrder(order);
        simulatedBroker.deleteStopOrders();
        assertTrue(simulatedBroker.getStopLossOrders().isEmpty());
    }

    @Test
    public void processStopOrders_BuySameQtd() {
        OrderBuilder orderBuilder = new OrderBuilder();
        OrderDto orderSell = orderBuilder.withAction(OrderDto.OrderAction.SELL).build();
        simulatedBroker.executeOrder(orderSell);

        StopOrderBuilder stopOrderBuilder = new StopOrderBuilder();
        stopOrderBuilder.withPrice(BigDecimal.ZERO);
        stopOrderBuilder.withType(STOP_LOSS);
        stopOrderBuilder.withQtd(1000);
        StopOrderDto order = stopOrderBuilder.build();
        simulatedBroker.placeStopOrder(order);
        simulatedBroker.processStopOrders();
        Map<String, FilledOrderDto> portfolio = simulatedBroker.getPortfolio();
        assertEquals(0, portfolio.size());
    }

    @Test
    public void processStopOrders_BuyDiffQtd() {
        OrderBuilder orderBuilder = new OrderBuilder();
        OrderDto orderSell = orderBuilder.withAction(OrderDto.OrderAction.SELL).build();
        simulatedBroker.executeOrder(orderSell);

        StopOrderBuilder stopOrderBuilder = new StopOrderBuilder();
        stopOrderBuilder.withPrice(BigDecimal.ZERO);
        stopOrderBuilder.withType(STOP_LOSS);
        StopOrderDto order = stopOrderBuilder.build();
        simulatedBroker.placeStopOrder(order);
        simulatedBroker.processStopOrders();
        Map<String, FilledOrderDto> portfolio = simulatedBroker.getPortfolio();
        assertEquals(1, portfolio.size());
        assertEquals(900, portfolio.get(order.identifier()).quantity());
    }

    @Test
    public void processStopOrder_AmendingQtd() {
        OrderBuilder orderBuilder = new OrderBuilder();
        OrderDto orderBuy = orderBuilder.build();
        simulatedBroker.executeOrder(orderBuy);

        StopOrderBuilder stopOrderBuilder = new StopOrderBuilder();
        stopOrderBuilder.withPrice(BigDecimal.ZERO);
        stopOrderBuilder.withType(STOP_LOSS);
        stopOrderBuilder.withAction(OrderDto.OrderAction.BUY);
        stopOrderBuilder.withQtd(1000);
        StopOrderDto order = stopOrderBuilder.build();
        simulatedBroker.placeStopOrder(order);
        simulatedBroker.processStopOrders();
        Map<String, FilledOrderDto> portfolio = simulatedBroker.getPortfolio();
        assertEquals(1, portfolio.size());
        assertEquals(2000, portfolio.get(order.identifier()).quantity());
    }

}