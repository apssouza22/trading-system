package com.apssouza.mytrade.trading.forex.order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class MemoryOrderDao {
    private static Map<Integer, OrderDto> ORDERS = new HashMap<>();
    private static AtomicInteger orderId = new AtomicInteger();


    public OrderDto persist(OrderDto order) {
        Integer id = MemoryOrderDao.orderId.incrementAndGet();

        OrderDto orderDto = new OrderDto(
                id,
                order
        );
        MemoryOrderDao.ORDERS.put(id, orderDto);
        return orderDto;
    }

    public void updateStatus(Integer id, OrderStatus status) {
        OrderDto order = MemoryOrderDao.ORDERS.get(id);
        OrderDto orderDto = new OrderDto(
                status,
                order
        );
        MemoryOrderDao.ORDERS.put(id, orderDto);
    }

    public List<OrderDto> getOrderByStatus(OrderStatus status) {
        List<OrderDto> orders = new ArrayList<>();
        for (Map.Entry<Integer, OrderDto> entry : MemoryOrderDao.ORDERS.entrySet()) {
            if (entry.getValue().getStatus().equals(status)) {
                orders.add(entry.getValue());
            }
        }
        return orders;
    }
}
