package com.apssouza.mytrade.trading.builder;

import com.apssouza.mytrade.feed.signal.SignalDto;
import com.apssouza.mytrade.trading.forex.order.OrderAction;
import com.apssouza.mytrade.trading.forex.order.OrderDto;
import com.apssouza.mytrade.trading.forex.order.OrderOrigin;
import com.apssouza.mytrade.trading.forex.order.OrderStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderBuilder {

    List<OrderDto> signals = new ArrayList<>();

    public OrderBuilder addOrder(LocalDateTime time, OrderAction action, OrderStatus status) {
        signals.add(new OrderDto(
                "AUDUSD",
                action,
                1000,
                OrderOrigin.SIGNAL,
                time,
                "AUDUSD",
                status
        ));
        return this;
    }

    public List<OrderDto> buildList() {
        return signals;
    }

    public OrderDto build() {
        return signals.get(0);
    }
}
