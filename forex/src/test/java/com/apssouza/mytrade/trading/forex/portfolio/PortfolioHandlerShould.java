package com.apssouza.mytrade.trading.forex.portfolio;

import com.apssouza.mytrade.feed.api.PriceDto;
import com.apssouza.mytrade.trading.forex.common.TradingParams;
import com.apssouza.mytrade.trading.forex.order.StopOrderDto;
import com.apssouza.mytrade.trading.forex.order.StopOrderDto.StopOrderType;
import com.apssouza.mytrade.trading.forex.session.event.EndedTradingDayEvent;
import com.apssouza.mytrade.trading.forex.session.event.EventType;
import com.apssouza.mytrade.trading.forex.session.event.PriceChangedEvent;

import static com.apssouza.mytrade.trading.forex.order.OrderDto.OrderAction.BUY;
import static com.apssouza.mytrade.trading.forex.order.OrderDto.OrderAction.SELL;
import static com.apssouza.mytrade.trading.forex.order.StopOrderDto.StopOrderStatus.CREATED;
import static com.apssouza.mytrade.trading.forex.order.StopOrderDto.StopOrderStatus.FILLED;
import static com.apssouza.mytrade.trading.forex.portfolio.Position.ExitReason.END_OF_DAY;
import static com.apssouza.mytrade.trading.forex.portfolio.Position.PositionStatus.*;
import static com.apssouza.mytrade.trading.forex.session.event.EventType.PRICE_CHANGED;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.MockitoJUnitRunner;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import static java.math.BigDecimal.ONE;
import static java.util.Collections.emptyList;

@RunWith(MockitoJUnitRunner.class)
public class PortfolioHandlerShould {


    @Test
    public void updatePortfolioValue() {
        var builder = new PortfolioHandlerBuilder();
        var portfolio = builder.build();
        //        portfolio.updatePortfolioValue();
    }

    @Test
    public void createStopOrder_withoutTakeProfitStopOrder() {
        var builder = new PortfolioHandlerBuilder();
        var portfolio = builder.build();
        var model = portfolio.getPortfolio();
        model.addNewPosition(new PositionBuilder().build());
        var stopLossOrder = new StopOrderDto(StopOrderType.STOP_LOSS, 1, CREATED, BUY, ONE, ONE, "EURUSD", 100, "id");
        var takeProfitOrder = new StopOrderDto(StopOrderType.TAKE_PROFIT, 2, FILLED, SELL, ONE, ONE, "EURUSD", 100, "id");

        var stopOrders = new EnumMap<StopOrderType, StopOrderDto>(StopOrderType.class);
        stopOrders.put(StopOrderType.STOP_LOSS, stopLossOrder);

        when(builder.riskManagementHandler.createStopOrders(any(), any())).thenReturn(stopOrders);
        when(builder.executionHandler.placeStopOrder(any()))
                .thenReturn(new StopOrderDto(FILLED, stopLossOrder))
                .thenReturn(takeProfitOrder);
        TradingParams.take_profit_stop_enabled = false;

        portfolio.createStopOrder(new PriceChangedEvent(PRICE_CHANGED, null, null));
        assert_createStopOrder_withoutTakeProfitStopOrder(builder, model);
    }

    private void assert_createStopOrder_withoutTakeProfitStopOrder(final PortfolioHandlerBuilder builder, final PortfolioModel model) {
        ArgumentCaptor<StopOrderDto> placeStopOrderArgCapture = ArgumentCaptor.forClass(StopOrderDto.class);
        ArgumentCaptor<Position> createStopOrdersArgCaptor = ArgumentCaptor.forClass(Position.class);

        verify(builder.riskManagementHandler, times(1)).createStopOrders(createStopOrdersArgCaptor.capture(), any());
        verify(builder.executionHandler, times(1)).placeStopOrder(placeStopOrderArgCapture.capture());

        var createStopOrdersArgCaptured = placeStopOrderArgCapture.getValue();
        var placeStopOrderArgCaptured = createStopOrdersArgCaptor.getValue();

        assertEquals(StopOrderType.STOP_LOSS, createStopOrdersArgCaptured.getType());
        assertEquals(model.getPosition("AUDUSD"), placeStopOrderArgCaptured);
    }

    @Test
    public void createStopOrder_withTakeProfitStopOrder() {
        var builder = new PortfolioHandlerBuilder();
        var portfolio = builder.build();
        var model = portfolio.getPortfolio();
        model.addNewPosition(new PositionBuilder().build());
        var stopLossOrder = new StopOrderDto(StopOrderType.STOP_LOSS, 1, CREATED, BUY, ONE, ONE, "EURUSD", 100, "id");
        var takeProfitOrder = new StopOrderDto(StopOrderType.TAKE_PROFIT, 2, FILLED, SELL, ONE, ONE, "EURUSD", 100, "id");

        var stopOrders = new EnumMap<StopOrderType, StopOrderDto>(StopOrderType.class);
        stopOrders.put(StopOrderType.STOP_LOSS, stopLossOrder);
        stopOrders.put(StopOrderType.TAKE_PROFIT, takeProfitOrder);

        when(builder.riskManagementHandler.createStopOrders(any(), any())).thenReturn(stopOrders);
        when(builder.executionHandler.placeStopOrder(any()))
                .thenReturn(new StopOrderDto(FILLED, stopLossOrder))
                .thenReturn(takeProfitOrder);

        portfolio.createStopOrder(new PriceChangedEvent(PRICE_CHANGED, null, null));
        assert_createStopOrder_withTakeProfitStopOrder(builder, model);
    }

    private void assert_createStopOrder_withTakeProfitStopOrder(final PortfolioHandlerBuilder builder, final PortfolioModel model) {
        ArgumentCaptor<StopOrderDto> placeStopOrderArgCapture = ArgumentCaptor.forClass(StopOrderDto.class);
        ArgumentCaptor<Position> createStopOrdersArgCaptor = ArgumentCaptor.forClass(Position.class);

        verify(builder.riskManagementHandler, times(1)).createStopOrders(createStopOrdersArgCaptor.capture(), any());
        verify(builder.executionHandler, times(2)).placeStopOrder(placeStopOrderArgCapture.capture());

        var createStopOrdersArgCaptured = placeStopOrderArgCapture.getAllValues();
        var placeStopOrderArgCaptured = createStopOrdersArgCaptor.getValue();

        assertEquals(StopOrderType.STOP_LOSS, createStopOrdersArgCaptured.get(0).getType());
        assertEquals(StopOrderType.TAKE_PROFIT, createStopOrdersArgCaptured.get(1).getType());
        assertEquals(model.getPosition("AUDUSD"), placeStopOrderArgCaptured);
    }

    @Test
    public void handleStopOrder() {
        var builder = new PortfolioHandlerBuilder();
        var portfolio = builder.build();
        var model = portfolio.getPortfolio();
        model.addNewPosition(new PositionBuilder().build());

        var event = new PriceChangedEvent(PRICE_CHANGED, null, null);
        portfolio.handleStopOrder(event);

        verify(builder.orderHandler, times(1)).createOrderFromClosedPosition(any(), any());
        verify(builder.eventNotifier, times(1)).notify(any());


    }

    @Test
    public void processExits() {
        var builder = new PortfolioHandlerBuilder();
        var portfolio = builder.build();

        var cancelledPosition = new PositionBuilder()
                .withPositionStatus(CLOSED)
                .build();

        when(builder.riskManagementHandler.processPositionExit(any(), any()))
                .thenReturn(Arrays.asList(cancelledPosition));

        var model = portfolio.getPortfolio();
        model.addNewPosition(new PositionBuilder().build());
        var event = new PriceChangedEvent(PRICE_CHANGED, null, null);
        portfolio.processExits(event, emptyList());

        verify(builder.riskManagementHandler, times(1)).processPositionExit(any(), any());
        verify(builder.orderHandler, times(1)).createOrderFromClosedPosition(any(), any());
        verify(builder.eventNotifier, times(1)).notify(any());
    }

    @Test
    public void closeAllPositions() {
        var builder = new PortfolioHandlerBuilder();
        var portfolio = builder.build();
        var position = new PositionBuilder()
                .withPositionStatus(Position.PositionStatus.FILLED)
                .build();

        var model = portfolio.getPortfolio();
        model.addNewPosition(position);
        assertEquals(1, portfolio.getPortfolio().getPositions().size());

        var priceMap = new HashMap<String, PriceDto>();
        priceMap.put("AUDUSD", new PriceDto(
                LocalDateTime.MIN,
                ONE,
                ONE,
                ONE,
                ONE,
                "AUDUSD"
        ));
        var event = new EndedTradingDayEvent(EventType.ENDED_TRADING_DAY, LocalDateTime.MIN, priceMap);
        var closedPositions = portfolio.closeAllPositions(END_OF_DAY, event);

        assertEquals(CLOSED, closedPositions.get(0).getStatus());
        verify(builder.orderHandler, times(1)).createOrderFromClosedPosition(any(), any());
        verify(builder.eventNotifier, times(1)).notify(any());
    }

    @Test
    public void returnPortfolio() {
        var portfolio = new PortfolioHandlerBuilder().build();
        portfolio.getPortfolio()
                .addNewPosition(new PositionBuilder().build());

        assertEquals(1, portfolio.getPortfolio().getPositions().size());
    }

    @Test
    public void processReconciliation() {
        var builder = new PortfolioHandlerBuilder();
        var portfolio = builder.build();
        //        portfolio.processReconciliation();
    }
}