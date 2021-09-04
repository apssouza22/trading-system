package com.apssouza.mytrade.trading.domain.forex.order;

import com.apssouza.mytrade.trading.domain.forex.portfolio.PositionBuilder;
import com.apssouza.mytrade.trading.domain.forex.feed.SignalBuilder;
import com.apssouza.mytrade.trading.domain.forex.portfolio.Position;
import com.apssouza.mytrade.trading.domain.forex.risk.RiskManagementHandler;
import com.apssouza.mytrade.trading.domain.forex.feed.signalfeed.SignalCreatedEvent;
import static com.apssouza.mytrade.trading.domain.forex.order.OrderDto.OrderAction.BUY;
import static com.apssouza.mytrade.trading.domain.forex.order.OrderDto.OrderOrigin.EXITS;
import static com.apssouza.mytrade.trading.domain.forex.order.OrderDto.OrderOrigin.STOP_ORDER;
import static com.apssouza.mytrade.trading.domain.forex.order.OrderDto.OrderStatus.CREATED;
import static com.apssouza.mytrade.trading.domain.forex.order.OrderDto.OrderStatus.EXECUTED;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import static org.mockito.Mockito.mock;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

@RunWith(MockitoJUnitRunner.class)
public class OrderHandlerShould extends TestCase {

    private RiskManagementHandler riskManagementHandler;

    @Before
    public void setUp() {
        this.riskManagementHandler = mock(RiskManagementHandler.class);
    }

    @Test
    public void createOrderFromSignal() {
        OrderHandler orderHandler = OrderHandlerFactory.create(this.riskManagementHandler);
        SignalBuilder signalBuilder = new SignalBuilder();
        signalBuilder.addSignal(LocalDateTime.MIN, "Buy");
        SignalCreatedEvent event = new SignalCreatedEvent(LocalDateTime.MIN, Collections.emptyMap(), signalBuilder.build());
        OrderDto orderFromSignal = orderHandler.createOrderFromSignal(event);
        assertEquals("BUY", orderFromSignal.getAction().name());
    }

    @Test
    public void createOrderFromClosedPosition_ofShortType() {
        PositionBuilder positionBuilder = new PositionBuilder();
        positionBuilder.withPositionStatus(Position.PositionStatus.CLOSED);
        positionBuilder.withType(Position.PositionType.SHORT);
        Position position = positionBuilder.build();

        OrderHandler orderHandler = OrderHandlerFactory.create(this.riskManagementHandler);
        OrderDto orderFromClosedPosition = orderHandler.createOrderFromClosedPosition(position, LocalDateTime.MIN);
        assertEquals(EXITS, orderFromClosedPosition.getOrigin());
        assertEquals(BUY, orderFromClosedPosition.getAction());
    }

    @Test
    public void createOrderFromClosedPosition_ofLongType() {
        PositionBuilder positionBuilder = new PositionBuilder();
        positionBuilder.withPositionStatus(Position.PositionStatus.CLOSED);
        positionBuilder.withType(Position.PositionType.LONG);
        Position position = positionBuilder.build();

        OrderHandler orderHandler = OrderHandlerFactory.create(this.riskManagementHandler);
        OrderDto orderFromClosedPosition = orderHandler.createOrderFromClosedPosition(position, LocalDateTime.MIN);
        assertEquals(EXITS, orderFromClosedPosition.getOrigin());
        assertEquals(OrderDto.OrderAction.SELL, orderFromClosedPosition.getAction());
    }

    @Test
    public void persistOrder() {
        OrderHandler orderHandler = OrderHandlerFactory.create(riskManagementHandler);
        orderHandler.persist(new OrderBuilder().build());
        List<OrderDto> ordersByStatus = orderHandler.getOrderByStatus(CREATED);
        assertNotNull(ordersByStatus.get(0));
    }

    @Test
    public void updateOrderStatus() {
        OrderHandler orderHandler = OrderHandlerFactory.create(this.riskManagementHandler);
        var order = new OrderDto("AUDUSD", BUY, 10, STOP_ORDER, LocalDateTime.MIN, "123", CREATED);
        var createdOrder = orderHandler.persist(order);
        orderHandler.updateOrderStatus(createdOrder.getId(), EXECUTED);
        List<OrderDto> orderByStatus = orderHandler.getOrderByStatus(EXECUTED);
        assertEquals(createdOrder.getId(), orderByStatus.get(0).getId());
    }

    @Test
    public void returnOrdersByStatus() {
        OrderHandler orderHandler = OrderHandlerFactory.create(this.riskManagementHandler);
        var order = new OrderDto("AUDUSD", BUY, 10, STOP_ORDER, LocalDateTime.MIN, "123", CREATED);
        var createdOrder = orderHandler.persist(order);
        List<OrderDto> orderByStatus = orderHandler.getOrderByStatus(CREATED);
        assertEquals(createdOrder.getId(), orderByStatus.get(0).getId());
    }
}