package com.apssouza.mytrade.trading.domain.forex.order;

import com.apssouza.mytrade.feed.api.PriceDto;
import com.apssouza.mytrade.trading.domain.forex.event.AbstractEvent;
import com.apssouza.mytrade.trading.domain.forex.event.EventType;
import com.apssouza.mytrade.trading.domain.forex.order.OrderDto;

import java.time.LocalDateTime;
import java.util.Map;

public class OrderCreatedEvent extends AbstractEvent {
    private final OrderDto order;

    public OrderCreatedEvent(
            EventType type,
            LocalDateTime timestamp,
            Map<String, PriceDto> price,
            OrderDto order
    ) {
        super(type, timestamp, price);
        this.order = order;
    }

    public OrderDto getOrder() {
        return order;
    }
}
