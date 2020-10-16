package com.apssouza.mytrade.trading.forex.session.event;

import com.apssouza.mytrade.feed.PriceDto;
import com.apssouza.mytrade.trading.forex.order.StopOrderDto;

import java.time.LocalDateTime;
import java.util.Map;

public class StopOrderFilledEvent extends AbstractEvent {
    private final StopOrderDto order;

    public StopOrderFilledEvent(
            EventType type,
            LocalDateTime timestamp,
            Map<String, PriceDto> price,
            StopOrderDto order
    ) {
        super(type, timestamp, price);
        this.order = order;
    }

    public StopOrderDto getStopOrder() {
        return order;
    }
}
