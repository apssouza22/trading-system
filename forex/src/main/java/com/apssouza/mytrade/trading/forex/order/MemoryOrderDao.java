package com.apssouza.mytrade.trading.forex.order;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

class MemoryOrderDao implements  OrderDao {
    private static Map<Integer, OrderDto> ORDERS = new ConcurrentHashMap<>();
    private static AtomicInteger orderId = new AtomicInteger();


    @Override
    public OrderDto persist(OrderDto order) {
        Integer id = MemoryOrderDao.orderId.incrementAndGet();

        OrderDto orderDto = new OrderDto(
                id,
                order
        );
        MemoryOrderDao.ORDERS.put(id, orderDto);
        return orderDto;
    }

    @Override
    public void updateStatus(Integer id, OrderStatus status) {
        OrderDto order = MemoryOrderDao.ORDERS.get(id);
        OrderDto orderDto = new OrderDto(
                status,
                order
        );
        MemoryOrderDao.ORDERS.put(id, orderDto);
    }

    @Override
    public List<OrderDto> getOrderByStatus(OrderStatus status) {
        List<OrderDto> orders = new ArrayList<>();
        for (Map.Entry<Integer, OrderDto> entry : MemoryOrderDao.ORDERS.entrySet()) {
            if (entry.getValue().getStatus().equals(status)) {
                orders.add(new OrderDto(
                        entry.getValue().getId(),
                        entry.getValue())
                );
            }
        }
        return orders;
    }

    public Optional<OrderDto> getOrderById(Integer id){
        if (MemoryOrderDao.ORDERS.containsKey(id)){
            return Optional.of(MemoryOrderDao.ORDERS.get(id));
        }
        return Optional.empty();
    }
}
