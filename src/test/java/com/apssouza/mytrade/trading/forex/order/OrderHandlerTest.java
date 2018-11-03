package com.apssouza.mytrade.trading.forex.order;

import com.apssouza.mytrade.trading.builder.OrderHandlerBuilder;
import com.apssouza.mytrade.trading.builder.PositionBuilder;
import com.apssouza.mytrade.trading.builder.SignalBuilder;
import com.apssouza.mytrade.trading.forex.portfolio.Position;
import com.apssouza.mytrade.trading.forex.portfolio.PositionStatus;
import com.apssouza.mytrade.trading.forex.portfolio.PositionType;
import com.apssouza.mytrade.trading.forex.session.event.EventType;
import com.apssouza.mytrade.trading.forex.session.event.SignalCreatedEvent;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.Collections;

@RunWith(MockitoJUnitRunner.class)
public class OrderHandlerTest extends TestCase {



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
        OrderHandlerBuilder orderHandlerBuilder = new OrderHandlerBuilder();
        OrderHandler orderHandler = orderHandlerBuilder.build();
        PositionBuilder positionBuilder = new PositionBuilder();
        positionBuilder.setPositionStatus(PositionStatus.CLOSED);
        positionBuilder.setType(PositionType.SHORT);
        Position position = positionBuilder.build();
        OrderDto orderFromClosedPosition = orderHandler.createOrderFromClosedPosition(position, LocalDateTime.MIN);
        assertEquals(OrderOrigin.STOP_ORDER, orderFromClosedPosition.getOrigin());
        assertEquals(OrderAction.BUY, orderFromClosedPosition.getAction());
    }

    @Test
    public void createOrderFromClosedPositionLongType() {
        OrderHandlerBuilder orderHandlerBuilder = new OrderHandlerBuilder();
        OrderHandler orderHandler = orderHandlerBuilder.build();
        PositionBuilder positionBuilder = new PositionBuilder();
        positionBuilder.setPositionStatus(PositionStatus.CLOSED);
        positionBuilder.setType(PositionType.LONG);
        Position position = positionBuilder.build();
        OrderDto orderFromClosedPosition = orderHandler.createOrderFromClosedPosition(position, LocalDateTime.MIN);
        assertEquals(OrderOrigin.STOP_ORDER, orderFromClosedPosition.getOrigin());
        assertEquals(OrderAction.SELL, orderFromClosedPosition.getAction());
    }

    @Test
    public void persist(){
        MemoryOrderDao memoryOrderDao = Mockito.mock(MemoryOrderDao.class);
        OrderHandlerBuilder orderHandlerBuilder = new OrderHandlerBuilder();
        orderHandlerBuilder.setMemoryOrderDao(memoryOrderDao);
        OrderHandler orderHandler = orderHandlerBuilder.build();
        orderHandler.persist(Mockito.mock(OrderDto.class));
        Mockito.verify(memoryOrderDao).persist(Mockito.any());
    }


    @Test
    public void updateStatus(){
        MemoryOrderDao memoryOrderDao = Mockito.mock(MemoryOrderDao.class);
        OrderHandlerBuilder orderHandlerBuilder = new OrderHandlerBuilder();
        orderHandlerBuilder.setMemoryOrderDao(memoryOrderDao);
        OrderHandler orderHandler = orderHandlerBuilder.build();
        orderHandler.updateOrderStatus(1, OrderStatus.CREATED);
        Mockito.verify(memoryOrderDao).updateStatus(Mockito.anyInt(), Mockito.any());
    }
}