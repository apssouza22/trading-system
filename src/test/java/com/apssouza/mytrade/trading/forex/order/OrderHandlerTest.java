package com.apssouza.mytrade.trading.forex.order;

import com.apssouza.mytrade.feed.signal.SignalDto;
import com.apssouza.mytrade.trading.builder.OrderHandlerBuilder;
import com.apssouza.mytrade.trading.builder.PositionBuilder;
import com.apssouza.mytrade.trading.builder.SignalBuilder;
import com.apssouza.mytrade.trading.forex.portfolio.Position;
import com.apssouza.mytrade.trading.forex.portfolio.PositionStatus;
import com.apssouza.mytrade.trading.forex.portfolio.PositionType;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class OrderHandlerTest extends TestCase {



    @Test
    public void createOrderFromSignal() {
        OrderHandlerBuilder orderHandlerBuilder = new OrderHandlerBuilder();
        OrderHandler orderHandler = orderHandlerBuilder.build();
        SignalBuilder signalBuilder = new SignalBuilder();
        signalBuilder.addSignal(LocalDateTime.MIN, "Buy");
        List<SignalDto> signalDtos = signalBuilder.buildList();
        List<OrderDto> orderFromSignal = orderHandler.createOrderFromSignal(signalDtos, LocalDateTime.MIN);
        assertEquals(1, orderFromSignal.size());

    }

    @Test
    public void createOrderFromSignalWithEmptySignalList() {
        OrderHandlerBuilder orderHandlerBuilder = new OrderHandlerBuilder();
        OrderHandler orderHandler = orderHandlerBuilder.build();
        List<SignalDto> signalDtos = new ArrayList<>();
        List<OrderDto> orderFromSignal = orderHandler.createOrderFromSignal(signalDtos, LocalDateTime.MIN);
        assertEquals(0, orderFromSignal.size());
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
}