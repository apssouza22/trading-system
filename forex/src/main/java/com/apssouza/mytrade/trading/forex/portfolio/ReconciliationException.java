package com.apssouza.mytrade.trading.forex.portfolio;

import com.apssouza.mytrade.trading.forex.session.event.Event;

import java.util.Map;

public class ReconciliationException extends Exception {

    private final Map<String, Position> localPositions;
    private final Map<String, FilledOrderDto> remotePositions;
    private final Event event;

    public ReconciliationException(
            Map<String, Position> localPositions,
            Map<String, FilledOrderDto> remotePositions,
            Event event
    ) {
        this.localPositions = localPositions;
        this.remotePositions = remotePositions;
        this.event = event;
    }

    public Map<String, Position> getLocalPositions() {
        return localPositions;
    }

    public Map<String, FilledOrderDto> getRemotePositions() {
        return remotePositions;
    }

    public Event getEvent() {
        return event;
    }
}
