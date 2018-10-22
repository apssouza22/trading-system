package com.apssouza.mytrade.trading.forex.session.event;

import com.apssouza.mytrade.feed.price.PriceDto;
import com.apssouza.mytrade.feed.signal.SignalDto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoopEvent extends AbstractEvent {

    public LoopEvent(EventType type, LocalDateTime timestamp, Map<String, PriceDto> price) {
        super(type, timestamp, price);

    }

}
