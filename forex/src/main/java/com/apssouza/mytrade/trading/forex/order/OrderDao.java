package com.apssouza.mytrade.trading.forex.order;

import java.util.List;

interface OrderDao {

    OrderDto persist(OrderDto order);

    void updateStatus(Integer id, OrderStatus status);

    List<OrderDto> getOrderByStatus(OrderStatus status);
}
