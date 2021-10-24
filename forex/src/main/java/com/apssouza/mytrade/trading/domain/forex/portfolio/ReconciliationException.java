package com.apssouza.mytrade.trading.domain.forex.portfolio;

import java.util.Map;

class ReconciliationException extends Exception {

    private final Map<String, Position> localPositions;
    private final Map<String, FilledOrderDto> remotePositions;

    public ReconciliationException(
            String msg,
            Map<String, Position> localPositions,
            Map<String, FilledOrderDto> remotePositions
    ) {
        super(msg);
        this.localPositions = localPositions;
        this.remotePositions = remotePositions;
    }

    public Map<String, Position> getLocalPositions() {
        return localPositions;
    }

    public Map<String, FilledOrderDto> getRemotePositions() {
        return remotePositions;
    }

}
