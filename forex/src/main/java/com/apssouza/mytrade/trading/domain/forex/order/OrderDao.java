package com.apssouza.mytrade.trading.domain.forex.order;

import java.util.List;

interface OrderDao {

    OrderDto persist(OrderDto order);

    boolean updateStatus(Integer id, OrderDto.OrderStatus status);

    List<OrderDto> getOrderByStatus(OrderDto.OrderStatus status);
}
