package com.apssouza.mytrade.trading.forex.session.event;


import com.apssouza.mytrade.feed.api.PriceDto;
import com.apssouza.mytrade.feed.api.SignalDto;

import java.time.LocalDateTime;
import java.util.Map;

public class SignalCreatedEvent extends AbstractEvent {


    private final SignalDto signal;

    public SignalCreatedEvent(EventType type, LocalDateTime timestamp, Map<String, PriceDto> price, SignalDto signalDto) {
        super(type, timestamp, price);
        this.signal = signalDto;
    }

    public SignalDto getSignal() {
        return signal;
    }
}
