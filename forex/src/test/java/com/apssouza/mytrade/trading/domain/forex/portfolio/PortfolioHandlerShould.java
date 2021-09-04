package com.apssouza.mytrade.trading.domain.forex.portfolio;

import com.apssouza.mytrade.feed.api.PriceDto;
import com.apssouza.mytrade.trading.domain.forex.common.TradingParams;
import com.apssouza.mytrade.trading.domain.forex.feed.PriceBuilder;
import com.apssouza.mytrade.trading.domain.forex.order.StopOrderBuilder;
import com.apssouza.mytrade.trading.domain.forex.risk.stoporder.StopOrderDto;
import com.apssouza.mytrade.trading.domain.forex.risk.stoporder.StopOrderDto.StopOrderType;
import com.apssouza.mytrade.trading.domain.forex.session.EndedTradingDayEvent;
import com.apssouza.mytrade.trading.domain.forex.event.EventType;
import com.apssouza.mytrade.trading.domain.forex.feed.pricefeed.PriceChangedEvent;
import static com.apssouza.mytrade.trading.domain.forex.order.OrderDto.OrderAction.BUY;
import static com.apssouza.mytrade.trading.domain.forex.order.OrderDto.OrderAction.SELL;
import static com.apssouza.mytrade.trading.domain.forex.risk.stoporder.StopOrderDto.StopOrderStatus.CREATED;
import static com.apssouza.mytrade.trading.domain.forex.risk.stoporder.StopOrderDto.StopOrderStatus.FILLED;
import static com.apssouza.mytrade.trading.domain.forex.portfolio.Position.ExitReason.END_OF_DAY;
import static com.apssouza.mytrade.trading.domain.forex.portfolio.Position.ExitReason.RECONCILIATION_FAILED;
import static com.apssouza.mytrade.trading.domain.forex.portfolio.Position.PositionStatus.CLOSED;
import static com.apssouza.mytrade.trading.domain.forex.event.EventType.PRICE_CHANGED;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.MockitoJUnitRunner;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import static java.math.BigDecimal.ONE;
import static java.util.Collections.emptyList;

@RunWith(MockitoJUnitRunner.class)
public class PortfolioHandlerShould {


    @Test
    public void updatePositionsPrices() {
        var builder = new PortfolioHandlerBuilder();
        var portfolio = builder.build();
        var position = new PositionBuilder().build();

        var model = portfolio.getPortfolio();
        model.addNewPosition(position);

        var newPrice = BigDecimal.valueOf(2);
        var priceMap = new PriceBuilder().withPrice("AUDUSD", newPrice).builderMap();

        portfolio.updatePositionsPrices(new PriceChangedEvent(PRICE_CHANGED, LocalDateTime.now(), priceMap));

        var modelPosition = model.getPosition(position.getIdentifier());
        assertEquals(newPrice, modelPosition.getCurrentPrice());
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

        portfolio.createStopOrder(new PriceChangedEvent(PRICE_CHANGED, null, new PriceBuilder().builderMap()));

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

        var stopOrderDtoMap = new HashMap<Integer, StopOrderDto>();
        stopOrderDtoMap.put(0, new StopOrderBuilder().withStatus(FILLED).build());

        var stopOrders = new EnumMap<StopOrderType, StopOrderDto>(StopOrderType.class);
        stopOrders.put(StopOrderType.STOP_LOSS, stopOrderDtoMap.get(0));

        when(builder.executionHandler.getStopLossOrders()).thenReturn(stopOrderDtoMap);
        when(builder.riskManagementHandler.createStopOrders(any(), any())).thenReturn(stopOrders);
        when(builder.executionHandler.placeStopOrder(any())).thenReturn(
                new StopOrderDto(FILLED, stopOrders.get(StopOrderType.STOP_LOSS))
        );

        var event = new PriceChangedEvent(PRICE_CHANGED, null, new PriceBuilder().builderMap());
        portfolio.createStopOrder(event);
        portfolio.handleStopOrder(event);

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
    public void processReconciliation_remotePortfolioNotInSync() {
        var builder = new PortfolioHandlerBuilder();
        var priceMap = new PriceBuilder().builderMap();
        var event = new PriceChangedEvent(PRICE_CHANGED, LocalDateTime.now(), priceMap);
        var portfolio = builder.build();
        var model = portfolio.getPortfolio();
        Position position = new PositionBuilder().build();
        model.addNewPosition(position);

        var filledOrderDtoMap = new HashMap<String, FilledOrderDto>();
        when(builder.executionHandler.getPositions()).thenReturn(filledOrderDtoMap);

        portfolio.processReconciliation(event);

        assertEquals(0, portfolio.getPortfolio().getOpenPositions().size());
        assertEquals(RECONCILIATION_FAILED, model.getPosition(position.getIdentifier()).getExitReason());
    }

    @Test
    public void processReconciliation_remotePortfolioInSync() {
        var builder = new PortfolioHandlerBuilder();
        var priceMap = new PriceBuilder().builderMap();
        var event = new PriceChangedEvent(PRICE_CHANGED, LocalDateTime.now(), priceMap);
        var portfolio = builder.build();
        var model = portfolio.getPortfolio();
        Position position = new PositionBuilder().build();
        model.addNewPosition(position);

        var filledOrderDtoMap = new HashMap<String, FilledOrderDto>();
        var filledOrderBuilder = new FilledOrderBuilder();
        filledOrderBuilder.withSymbol(position.getSymbol());
        filledOrderDtoMap.put(position.getSymbol(), filledOrderBuilder.build());
        when(builder.executionHandler.getPositions()).thenReturn(filledOrderDtoMap);

        portfolio.processReconciliation(event);

        assertEquals(1, portfolio.getPortfolio().getOpenPositions().size());
        assertNull(model.getPosition(position.getIdentifier()).getExitReason());
    }
}