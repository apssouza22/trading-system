package com.apssouza.mytrade.trading.domain.forex.portfolio;

import java.util.Map;

class ReconciliationException extends Exception {

    private final Map<String, PositionDto> localPositions;
    private final Map<String, FilledOrderDto> remotePositions;

    public ReconciliationException(
            String msg,
            Map<String, PositionDto> localPositions,
            Map<String, FilledOrderDto> remotePositions
    ) {
        super(msg);
        this.localPositions = localPositions;
        this.remotePositions = remotePositions;
    }

    public Map<String, PositionDto> getLocalPositions() {
        return localPositions;
    }

    public Map<String, FilledOrderDto> getRemotePositions() {
        return remotePositions;
    }

}
