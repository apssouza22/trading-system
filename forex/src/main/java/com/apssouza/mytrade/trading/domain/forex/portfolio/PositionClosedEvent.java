package com.apssouza.mytrade.trading.domain.forex.portfolio;

import com.apssouza.mytrade.feed.api.PriceDto;
import com.apssouza.mytrade.trading.domain.forex.common.AbstractEvent;
import com.apssouza.mytrade.trading.domain.forex.order.OrderDto;

import java.time.LocalDateTime;
import java.util.Map;

public class PositionClosedEvent extends AbstractEvent {
    private OrderDto order;

    public PositionClosedEvent(LocalDateTime timestamp, Map<String, PriceDto> price, OrderDto order) {
        super(timestamp, price);
        this.order = order;
    }

    public OrderDto getOrder() {
        return order;
    }
}
