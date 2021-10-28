package com.apssouza.mytrade.trading.domain.forex.order;

import com.apssouza.mytrade.trading.domain.forex.common.events.SignalCreatedEvent;
import com.apssouza.mytrade.trading.domain.forex.portfolio.PositionDto;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {


    OrderDto createOrderFromClosedPosition(PositionDto position, LocalDateTime time) ;

    OrderDto persist(OrderDto order) ;

    boolean updateOrderStatus(Integer id, OrderDto.OrderStatus status);

    OrderDto createOrderFromSignal(SignalCreatedEvent event) ;

    List<OrderDto> getOrderByStatus(OrderDto.OrderStatus status);

}
