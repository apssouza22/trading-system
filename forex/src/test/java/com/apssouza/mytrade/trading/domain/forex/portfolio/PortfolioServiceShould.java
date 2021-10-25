package com.apssouza.mytrade.trading.domain.forex.portfolio;

import com.apssouza.mytrade.feed.api.PriceDto;
import com.apssouza.mytrade.trading.domain.forex.common.TradingParams;
import com.apssouza.mytrade.trading.domain.forex.feed.PriceBuilder;
import com.apssouza.mytrade.trading.domain.forex.common.events.PriceChangedEvent;
import com.apssouza.mytrade.trading.domain.forex.order.StopOrderBuilder;
import com.apssouza.mytrade.trading.domain.forex.risk.stopordercreation.StopOrderDto;
import com.apssouza.mytrade.trading.domain.forex.risk.stopordercreation.StopOrderDto.StopOrderType;
import com.apssouza.mytrade.trading.domain.forex.common.events.EndedTradingDayEvent;
import static com.apssouza.mytrade.trading.domain.forex.order.OrderDto.OrderAction.BUY;
import static com.apssouza.mytrade.trading.domain.forex.order.OrderDto.OrderAction.SELL;
import static com.apssouza.mytrade.trading.domain.forex.portfolio.PositionDto.ExitReason.END_OF_DAY;
import static com.apssouza.mytrade.trading.domain.forex.portfolio.PositionDto.ExitReason.RECONCILIATION_FAILED;
import static com.apssouza.mytrade.trading.domain.forex.portfolio.PositionDto.PositionStatus.CLOSED;
import static com.apssouza.mytrade.trading.domain.forex.risk.stopordercreation.StopOrderDto.StopOrderStatus.CREATED;
import static com.apssouza.mytrade.trading.domain.forex.risk.stopordercreation.StopOrderDto.StopOrderStatus.FILLED;

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
public class PortfolioServiceShould {


    @Test
    public void updatePositionsPrices() {
        var builder = new PortfolioHandlerBuilder();
        var portfolio = builder.build();
        var position = new PositionBuilder().build();

        var model = portfolio.getPortfolio();
        model.addNewPosition(position.positionType(), position.filledOrder());

        var newPrice = BigDecimal.valueOf(2);
        var priceMap = new PriceBuilder().withPrice("AUDUSD", newPrice).builderMap();

        portfolio.updatePositionsPrices(priceMap);

        var modelPosition = model.getPosition(position.identifier());
        assertEquals(newPrice, modelPosition.currentPrice());
    }

    @Test
    public void createStopOrder_withoutTakeProfitStopOrder() {
        var builder = new PortfolioHandlerBuilder();
        var portfolio = builder.build();
        var model = portfolio.getPortfolio();
        var position = new PositionBuilder().build();
        model.addNewPosition(position.positionType(), position.filledOrder());
        var stopLossOrder = new StopOrderDto(StopOrderType.STOP_LOSS, 1, CREATED, BUY, ONE, ONE, "EURUSD", 100, "id");
        var takeProfitOrder = new StopOrderDto(StopOrderType.TAKE_PROFIT, 2, FILLED, SELL, ONE, ONE, "EURUSD", 100, "id");

        var stopOrders = new EnumMap<StopOrderType, StopOrderDto>(StopOrderType.class);
        stopOrders.put(StopOrderType.STOP_LOSS, stopLossOrder);

        when(builder.riskManagementService.createStopOrders(any(), any())).thenReturn(stopOrders);
        when(builder.brokerService.placeStopOrder(any()))
                .thenReturn(new StopOrderDto(FILLED, stopLossOrder))
                .thenReturn(takeProfitOrder);
        TradingParams.take_profit_stop_enabled = false;

        portfolio.createStopOrder(new PriceChangedEvent(null, new PriceBuilder().builderMap()));

        assert_createStopOrder_withoutTakeProfitStopOrder(builder, model);
    }

    private void assert_createStopOrder_withoutTakeProfitStopOrder(PortfolioHandlerBuilder builder, PortfolioModel model) {
        ArgumentCaptor<StopOrderDto> placeStopOrderArgCapture = ArgumentCaptor.forClass(StopOrderDto.class);
        ArgumentCaptor<PositionDto> createStopOrdersArgCaptor = ArgumentCaptor.forClass(PositionDto.class);

        verify(builder.riskManagementService, times(1)).createStopOrders(createStopOrdersArgCaptor.capture(), any());
        verify(builder.brokerService, times(1)).placeStopOrder(placeStopOrderArgCapture.capture());

        var createStopOrdersArgCaptured = placeStopOrderArgCapture.getValue();
        var placeStopOrderArgCaptured = createStopOrdersArgCaptor.getValue();

        assertEquals(StopOrderType.STOP_LOSS, createStopOrdersArgCaptured.type());
        assertEquals(model.getPosition("AUDUSD"), placeStopOrderArgCaptured);
    }

    @Test
    public void createStopOrder_withTakeProfitStopOrder() {
        var builder = new PortfolioHandlerBuilder();
        var portfolio = builder.build();
        var model = portfolio.getPortfolio();
        var position = new PositionBuilder().build();
        model.addNewPosition(position.positionType(), position.filledOrder());
        var stopLossOrder = new StopOrderDto(StopOrderType.STOP_LOSS, 1, CREATED, BUY, ONE, ONE, "EURUSD", 100, "id");
        var takeProfitOrder = new StopOrderDto(StopOrderType.TAKE_PROFIT, 2, FILLED, SELL, ONE, ONE, "EURUSD", 100, "id");

        var stopOrders = new EnumMap<StopOrderType, StopOrderDto>(StopOrderType.class);
        stopOrders.put(StopOrderType.STOP_LOSS, stopLossOrder);
        stopOrders.put(StopOrderType.TAKE_PROFIT, takeProfitOrder);

        when(builder.riskManagementService.createStopOrders(any(), any())).thenReturn(stopOrders);
        when(builder.brokerService.placeStopOrder(any()))
                .thenReturn(new StopOrderDto(FILLED, stopLossOrder))
                .thenReturn(takeProfitOrder);

        portfolio.createStopOrder(new PriceChangedEvent(null, null));
        assert_createStopOrder_withTakeProfitStopOrder(builder, model);
    }

    private void assert_createStopOrder_withTakeProfitStopOrder(PortfolioHandlerBuilder builder, PortfolioModel model) {
        ArgumentCaptor<StopOrderDto> placeStopOrderArgCapture = ArgumentCaptor.forClass(StopOrderDto.class);
        ArgumentCaptor<PositionDto> createStopOrdersArgCaptor = ArgumentCaptor.forClass(PositionDto.class);

        verify(builder.riskManagementService, times(1)).createStopOrders(createStopOrdersArgCaptor.capture(), any());
        verify(builder.brokerService, times(2)).placeStopOrder(placeStopOrderArgCapture.capture());

        var createStopOrdersArgCaptured = placeStopOrderArgCapture.getAllValues();
        var placeStopOrderArgCaptured = createStopOrdersArgCaptor.getValue();

        assertEquals(StopOrderType.STOP_LOSS, createStopOrdersArgCaptured.get(0).type());
        assertEquals(StopOrderType.TAKE_PROFIT, createStopOrdersArgCaptured.get(1).type());
        assertEquals(model.getPosition("AUDUSD"), placeStopOrderArgCaptured);
    }

    @Test
    public void handleStopOrder() {
        var builder = new PortfolioHandlerBuilder();
        var portfolio = builder.build();
        var model = portfolio.getPortfolio();
        var position = new PositionBuilder().build();
        model.addNewPosition(position.positionType(), position.filledOrder());

        var stopOrderDtoMap = new HashMap<Integer, StopOrderDto>();
        stopOrderDtoMap.put(0, new StopOrderBuilder().withStatus(FILLED).build());

        var stopOrders = new EnumMap<StopOrderType, StopOrderDto>(StopOrderType.class);
        stopOrders.put(StopOrderType.STOP_LOSS, stopOrderDtoMap.get(0));

        when(builder.brokerService.getStopLossOrders()).thenReturn(stopOrderDtoMap);
        when(builder.riskManagementService.createStopOrders(any(), any())).thenReturn(stopOrders);
        when(builder.brokerService.placeStopOrder(any())).thenReturn(
                new StopOrderDto(FILLED, stopOrders.get(StopOrderType.STOP_LOSS))
        );

        var event = new PriceChangedEvent(null, new PriceBuilder().builderMap());
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

        when(builder.riskManagementService.processPositionExit(any(), any()))
                .thenReturn(Arrays.asList(cancelledPosition));

        var model = portfolio.getPortfolio();
        var position = new PositionBuilder().build();
        model.addNewPosition(position.positionType(), position.filledOrder());
        var event = new PriceChangedEvent(null, null);

        portfolio.processExits(event, emptyList());

        verify(builder.riskManagementService, times(1)).processPositionExit(any(), any());
        verify(builder.orderService, times(1)).createOrderFromClosedPosition(any(), any());
        verify(builder.eventNotifier, times(1)).notify(any());
    }

    @Test
    public void closeAllPositions() {
        var builder = new PortfolioHandlerBuilder();
        var portfolio = builder.build();
        var position = new PositionBuilder()
                .withPositionStatus(PositionDto.PositionStatus.FILLED)
                .build();

        var model = portfolio.getPortfolio();
        model.addNewPosition(position.positionType(), position.filledOrder());
        assertEquals(1, portfolio.getPortfolio().getPositionCollection().size());

        var priceMap = new HashMap<String, PriceDto>();
        priceMap.put("AUDUSD", new PriceDto(
                LocalDateTime.MIN,
                ONE,
                ONE,
                ONE,
                ONE,
                "AUDUSD"
        ));
        var event = new EndedTradingDayEvent(LocalDateTime.MIN, priceMap);

        var closedPositions = portfolio.closeAllPositions(END_OF_DAY, event);

        assertEquals(CLOSED, closedPositions.get(0).status());
        verify(builder.orderService, times(1)).createOrderFromClosedPosition(any(), any());
        verify(builder.eventNotifier, times(1)).notify(any());
    }

    @Test
    public void returnPortfolio() {
        var portfolio = new PortfolioHandlerBuilder().build();
        var model = portfolio.getPortfolio();
        var position = new PositionBuilder().build();
        model.addNewPosition(position.positionType(), position.filledOrder());
        assertEquals(1, portfolio.getPortfolio().getPositionCollection().size());
    }

    @Test
    public void processReconciliation_remotePortfolioNotInSync() {
        var builder = new PortfolioHandlerBuilder();
        var priceMap = new PriceBuilder().builderMap();
        var event = new PriceChangedEvent(LocalDateTime.now(), priceMap);
        var portfolio = builder.build();
        var model = portfolio.getPortfolio();
        PositionDto position = new PositionBuilder().build();
        model.addNewPosition(position.positionType(), position.filledOrder());

        var filledOrderDtoMap = new HashMap<String, FilledOrderDto>();
        when(builder.brokerService.getPositions()).thenReturn(filledOrderDtoMap);

        portfolio.processReconciliation(event);

        assertEquals(0, portfolio.getPortfolio().getOpenPositions().size());
        assertEquals(RECONCILIATION_FAILED, model.getPosition(position.identifier()).exitReason());
    }

    @Test
    public void processReconciliation_remotePortfolioInSync() {
        var builder = new PortfolioHandlerBuilder();
        var priceMap = new PriceBuilder().builderMap();
        var event = new PriceChangedEvent(LocalDateTime.now(), priceMap);
        var portfolio = builder.build();
        var model = portfolio.getPortfolio();
        PositionDto position = new PositionBuilder().build();
        model.addNewPosition(position.positionType(), position.filledOrder());

        var filledOrderDtoMap = new HashMap<String, FilledOrderDto>();
        var filledOrderBuilder = new FilledOrderBuilder();
        filledOrderBuilder.withSymbol(position.symbol());
        filledOrderDtoMap.put(position.symbol(), filledOrderBuilder.build());
        when(builder.brokerService.getPositions()).thenReturn(filledOrderDtoMap);

        portfolio.processReconciliation(event);

        assertEquals(1, portfolio.getPortfolio().getOpenPositions().size());
        assertNull(model.getPosition(position.identifier()).exitReason());
    }
}