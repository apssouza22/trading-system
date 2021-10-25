package com.apssouza.mytrade.trading.domain.forex.common.events;

import com.apssouza.mytrade.feed.api.PriceDto;
import com.apssouza.mytrade.trading.domain.forex.common.events.AbstractEvent;
import com.apssouza.mytrade.trading.domain.forex.order.OrderDto;

import java.time.LocalDateTime;
import java.util.Map;

public class OrderCreatedEvent extends AbstractEvent {
    private final OrderDto order;

    public OrderCreatedEvent(
            LocalDateTime timestamp,
            Map<String, PriceDto> price,
            OrderDto order
    ) {
        super(timestamp, price);
        this.order = order;
    }

    public OrderDto getOrder() {
        return order;
    }
}
