package com.apssouza.mytrade.trading.forex.order;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface OrderDao {

    OrderDto persist(OrderDto order);

    void updateStatus(Integer id, OrderStatus status);

    List<OrderDto> getOrderByStatus(OrderStatus status);
}
