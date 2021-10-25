package com.apssouza.mytrade.trading.domain.forex.portfolio;

import java.util.List;
import java.util.Map;

class ReconciliationException extends Exception {

    private final List<PositionDto> localPositions;
    private final Map<String, FilledOrderDto> remotePositions;

    public ReconciliationException(
            String msg,
            List<PositionDto> localPositions,
            Map<String, FilledOrderDto> remotePositions
    ) {
        super(msg);
        this.localPositions = localPositions;
        this.remotePositions = remotePositions;
    }

    public List<PositionDto> getLocalPositions() {
        return localPositions;
    }

    public Map<String, FilledOrderDto> getRemotePositions() {
        return remotePositions;
    }

}
