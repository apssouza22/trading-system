package com.apssouza.mytrade.trading.domain.forex.order;

import com.apssouza.mytrade.trading.domain.forex.portfolio.PositionBuilder;
import com.apssouza.mytrade.trading.domain.forex.feed.SignalBuilder;
import com.apssouza.mytrade.trading.domain.forex.portfolio.Position;
import com.apssouza.mytrade.trading.domain.forex.risk.RiskManagementService;
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
public class OrderServiceShould extends TestCase {

    private RiskManagementService riskManagementService;

    @Before
    public void setUp() {
        this.riskManagementService = mock(RiskManagementService.class);
    }

    @Test
    public void createOrderFromSignal() {
        OrderService orderService = OrderHandlerFactory.create(this.riskManagementService);
        SignalBuilder signalBuilder = new SignalBuilder();
        signalBuilder.addSignal(LocalDateTime.MIN, "Buy");
        SignalCreatedEvent event = new SignalCreatedEvent(LocalDateTime.MIN, Collections.emptyMap(), signalBuilder.build());
        OrderDto orderFromSignal = orderService.createOrderFromSignal(event);
        assertEquals("BUY", orderFromSignal.action().name());
    }

    @Test
    public void createOrderFromClosedPosition_ofShortType() {
        PositionBuilder positionBuilder = new PositionBuilder();
        positionBuilder.withPositionStatus(Position.PositionStatus.CLOSED);
        positionBuilder.withType(Position.PositionType.SHORT);
        Position position = positionBuilder.build();

        OrderService orderService = OrderHandlerFactory.create(this.riskManagementService);
        OrderDto orderFromClosedPosition = orderService.createOrderFromClosedPosition(position, LocalDateTime.MIN);
        assertEquals(EXITS, orderFromClosedPosition.origin());
        assertEquals(BUY, orderFromClosedPosition.action());
    }

    @Test
    public void createOrderFromClosedPosition_ofLongType() {
        PositionBuilder positionBuilder = new PositionBuilder();
        positionBuilder.withPositionStatus(Position.PositionStatus.CLOSED);
        positionBuilder.withType(Position.PositionType.LONG);
        Position position = positionBuilder.build();

        OrderService orderService = OrderHandlerFactory.create(this.riskManagementService);
        OrderDto orderFromClosedPosition = orderService.createOrderFromClosedPosition(position, LocalDateTime.MIN);
        assertEquals(EXITS, orderFromClosedPosition.origin());
        assertEquals(OrderDto.OrderAction.SELL, orderFromClosedPosition.action());
    }

    @Test
    public void persistOrder() {
        OrderService orderService = OrderHandlerFactory.create(riskManagementService);
        orderService.persist(new OrderBuilder().build());
        List<OrderDto> ordersByStatus = orderService.getOrderByStatus(CREATED);
        assertNotNull(ordersByStatus.get(0));
    }

    @Test
    public void updateOrderStatus() {
        OrderService orderService = OrderHandlerFactory.create(this.riskManagementService);
        var order = new OrderDto("AUDUSD", BUY, 10, STOP_ORDER, LocalDateTime.MIN, "123", CREATED);
        var createdOrder = orderService.persist(order);
        orderService.updateOrderStatus(createdOrder.id(), EXECUTED);
        List<OrderDto> orderByStatus = orderService.getOrderByStatus(EXECUTED);
        assertEquals(createdOrder.id(), orderByStatus.get(0).id());
    }

    @Test
    public void returnOrdersByStatus() {
        OrderService orderService = OrderHandlerFactory.create(this.riskManagementService);
        var order = new OrderDto("AUDUSD", BUY, 10, STOP_ORDER, LocalDateTime.MIN, "123", CREATED);
        var createdOrder = orderService.persist(order);
        List<OrderDto> orderByStatus = orderService.getOrderByStatus(CREATED);
        assertEquals(createdOrder.id(), orderByStatus.get(0).id());
    }
}