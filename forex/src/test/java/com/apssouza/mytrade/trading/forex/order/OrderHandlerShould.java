package com.apssouza.mytrade.trading.forex.order;

import com.apssouza.mytrade.trading.builder.PositionBuilder;
import com.apssouza.mytrade.trading.builder.SignalBuilder;
import com.apssouza.mytrade.trading.forex.common.TradingParams;
import com.apssouza.mytrade.trading.forex.portfolio.PortfolioModel;
import com.apssouza.mytrade.trading.forex.portfolio.Position;
import com.apssouza.mytrade.trading.forex.risk.RiskManagementFactory;
import com.apssouza.mytrade.trading.forex.risk.RiskManagementHandler;
import com.apssouza.mytrade.trading.forex.risk.stoporder.StopOrderCreator;
import com.apssouza.mytrade.trading.forex.risk.stoporder.StopOrderConfigDto;
import com.apssouza.mytrade.trading.forex.risk.stoporder.StopOrderFactory;
import com.apssouza.mytrade.trading.forex.session.event.EventType;
import com.apssouza.mytrade.trading.forex.session.event.SignalCreatedEvent;
import static com.apssouza.mytrade.trading.forex.order.OrderDto.OrderAction.BUY;
import static com.apssouza.mytrade.trading.forex.order.OrderDto.OrderOrigin.EXITS;
import static com.apssouza.mytrade.trading.forex.order.OrderDto.OrderOrigin.STOP_ORDER;
import static com.apssouza.mytrade.trading.forex.order.OrderDto.OrderStatus.CREATED;
import static com.apssouza.mytrade.trading.forex.order.OrderDto.OrderStatus.EXECUTED;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

@RunWith(MockitoJUnitRunner.class)
public class OrderHandlerShould extends TestCase {

    private RiskManagementHandler riskManagementHandler;

    @Before
    public void setUp(){
        StopOrderCreator stopOrderCreator = StopOrderFactory.factory(new StopOrderConfigDto(
                TradingParams.hard_stop_loss_distance,
                TradingParams.take_profit_distance_fixed,
                TradingParams.entry_stop_loss_distance_fixed,
                TradingParams.trailing_stop_loss_distance
        ));
        this.riskManagementHandler = RiskManagementFactory.create(new PortfolioModel(BigDecimal.TEN), stopOrderCreator);
    }

    @Test
    public void createOrderFromSignal() {
        OrderHandlerBuilder orderHandlerBuilder = new OrderHandlerBuilder();
        OrderHandler orderHandler = orderHandlerBuilder.build();
        SignalBuilder signalBuilder = new SignalBuilder();
        signalBuilder.addSignal(LocalDateTime.MIN, "Buy");
        SignalCreatedEvent event = new SignalCreatedEvent(EventType.SIGNAL_CREATED, LocalDateTime.MIN, Collections.emptyMap(), signalBuilder.build());
        OrderDto orderFromSignal = orderHandler.createOrderFromSignal(event);
        assertEquals("BUY", orderFromSignal.getAction().name());

    }

    @Test
    public void createOrderFromClosedPositionShortType() {
        PositionBuilder positionBuilder = new PositionBuilder();
        positionBuilder.withPositionStatus(Position.PositionStatus.CLOSED);
        positionBuilder.withType(Position.PositionType.SHORT);
        Position position = positionBuilder.build();

        OrderHandler orderHandler = OrderHandlerFactory.factory(riskManagementHandler, new MemoryOrderDao());
        OrderDto orderFromClosedPosition = orderHandler.createOrderFromClosedPosition(position, LocalDateTime.MIN);
        assertEquals(EXITS, orderFromClosedPosition.getOrigin());
        assertEquals(BUY, orderFromClosedPosition.getAction());
    }

    @Test
    public void createOrderFromClosedPositionLongType() {
        PositionBuilder positionBuilder = new PositionBuilder();
        positionBuilder.withPositionStatus(Position.PositionStatus.CLOSED);
        positionBuilder.withType(Position.PositionType.LONG);
        Position position = positionBuilder.build();

        OrderHandler orderHandler = OrderHandlerFactory.factory(riskManagementHandler, new MemoryOrderDao());
        OrderDto orderFromClosedPosition = orderHandler.createOrderFromClosedPosition(position, LocalDateTime.MIN);
        assertEquals(EXITS, orderFromClosedPosition.getOrigin());
        assertEquals(OrderDto.OrderAction.SELL, orderFromClosedPosition.getAction());
    }

    @Test
    public void persist() {
        MemoryOrderDao memoryOrderDao = Mockito.mock(MemoryOrderDao.class);
        OrderHandler orderHandler = OrderHandlerFactory.factory(riskManagementHandler, memoryOrderDao);
        orderHandler.persist(Mockito.mock(OrderDto.class));
        Mockito.verify(memoryOrderDao).persist(Mockito.any());
    }

    @Test
    public void updateStatus() {
        OrderHandler orderHandler = OrderHandlerFactory.factory(riskManagementHandler, new MemoryOrderDao());
        var order = new OrderDto("AUDUSD", BUY, 10, STOP_ORDER, LocalDateTime.MIN, "123", CREATED);
        var createdOrder = orderHandler.persist(order);
        orderHandler.updateOrderStatus(createdOrder.getId(), EXECUTED);
        List<OrderDto> orderByStatus = orderHandler.getOrderByStatus(EXECUTED);
        assertEquals(createdOrder.getId(), orderByStatus.get(0).getId());
    }

    @Test
    public void getOrdersByStatus() {
        OrderHandler orderHandler = OrderHandlerFactory.factory(riskManagementHandler, new MemoryOrderDao());
        var order = new OrderDto("AUDUSD", BUY, 10, STOP_ORDER, LocalDateTime.MIN, "123", CREATED);
        var createdOrder = orderHandler.persist(order);
        List<OrderDto> orderByStatus = orderHandler.getOrderByStatus(CREATED);
        assertEquals(createdOrder.getId(), orderByStatus.get(0).getId());
    }
}