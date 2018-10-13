package com.apssouza.mytrade.trading.forex.risk.stoporder;

import java.math.BigDecimal;

public class PriceDistanceObject {

    private final BigDecimal hardStopDistance;
    private final BigDecimal takeProfitDistance;
    private final BigDecimal entryStopDistance;
    private final BigDecimal traillingStopDistance;

    public PriceDistanceObject(
            double hardStopDistance,
            double takeProfitDistance,
            double entryStopDistance,
            double traillingStopDistance
    ) {
        this.hardStopDistance = BigDecimal.valueOf(hardStopDistance);
        this.takeProfitDistance = BigDecimal.valueOf(takeProfitDistance);
        this.entryStopDistance = BigDecimal.valueOf(entryStopDistance);
        this.traillingStopDistance = BigDecimal.valueOf(traillingStopDistance);
    }

    public BigDecimal getHardStopDistance() {
        return hardStopDistance;
    }

    public BigDecimal getTakeProfitDistance() {
        return takeProfitDistance;
    }

    public BigDecimal getEntryStopDistance() {
        return entryStopDistance;
    }

    public BigDecimal getTraillingStopDistance() {
        return traillingStopDistance;
    }
}
