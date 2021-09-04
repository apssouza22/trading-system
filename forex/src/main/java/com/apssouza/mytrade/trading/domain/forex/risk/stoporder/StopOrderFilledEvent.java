package com.apssouza.mytrade.trading.domain.forex.risk.stoporder;

import com.apssouza.mytrade.feed.api.PriceDto;
import com.apssouza.mytrade.trading.domain.forex.event.AbstractEvent;
import com.apssouza.mytrade.trading.domain.forex.event.EventType;

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
