package com.apssouza.mytrade.trading.domain.forex.common.events;


import com.apssouza.mytrade.feed.api.PriceDto;
import com.apssouza.mytrade.feed.api.SignalDto;
import com.apssouza.mytrade.trading.domain.forex.common.events.AbstractEvent;

import java.time.LocalDateTime;
import java.util.Map;

public class SignalCreatedEvent extends AbstractEvent {


    private final SignalDto signal;

    public SignalCreatedEvent(LocalDateTime timestamp, Map<String, PriceDto> price, SignalDto signalDto) {
        super(timestamp, price);
        this.signal = signalDto;
    }

    public SignalDto getSignal() {
        return signal;
    }
}
