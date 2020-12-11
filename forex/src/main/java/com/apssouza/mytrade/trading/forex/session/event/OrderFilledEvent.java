package com.apssouza.mytrade.trading.forex.session.event;

import com.apssouza.mytrade.feed.api.PriceDto;
import com.apssouza.mytrade.trading.forex.portfolio.FilledOrderDto;

import java.time.LocalDateTime;
import java.util.Map;

public class OrderFilledEvent extends AbstractEvent {
    private final FilledOrderDto filledOrder;

    public OrderFilledEvent(EventType type, LocalDateTime time, Map<String, PriceDto> price, FilledOrderDto filledOrder) {
        super(type, time, price);
        this.filledOrder = filledOrder;
    }

    public FilledOrderDto getFilledOrder() {
        return filledOrder;
    }
}
