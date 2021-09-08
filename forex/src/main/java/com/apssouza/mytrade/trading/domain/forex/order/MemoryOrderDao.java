package com.apssouza.mytrade.trading.domain.forex.order;

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
    public boolean updateStatus(Integer id, OrderDto.OrderStatus status) {
        OrderDto order = MemoryOrderDao.ORDERS.get(id);
        OrderDto orderDto = new OrderDto(
                status,
                order
        );
        MemoryOrderDao.ORDERS.put(id, orderDto);
        return true;
    }

    @Override
    public List<OrderDto> getOrderByStatus(OrderDto.OrderStatus status) {
        List<OrderDto> orders = new ArrayList<>();
        for (Map.Entry<Integer, OrderDto> entry : MemoryOrderDao.ORDERS.entrySet()) {
            if (entry.getValue().status().equals(status)) {
                orders.add(new OrderDto(
                        entry.getValue().id(),
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
