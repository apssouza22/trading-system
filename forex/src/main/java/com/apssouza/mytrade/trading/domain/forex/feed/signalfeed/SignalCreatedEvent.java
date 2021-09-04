package com.apssouza.mytrade.trading.domain.forex.feed.signalfeed;


import com.apssouza.mytrade.feed.api.PriceDto;
import com.apssouza.mytrade.feed.api.SignalDto;
import com.apssouza.mytrade.trading.domain.forex.event.AbstractEvent;
import com.apssouza.mytrade.trading.domain.forex.event.EventType;

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
