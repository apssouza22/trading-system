package com.apssouza.mytrade.trading.domain.forex.riskmanagement.stopordercreation;

import java.math.BigDecimal;

public record StopOrderConfigDto (
        BigDecimal hardStopDistance,
        BigDecimal takeProfitDistance,
        BigDecimal entryStopDistance,
        BigDecimal traillingStopDistance
){
    public StopOrderConfigDto(
            double hardStopDistance,
            double takeProfitDistance,
            double entryStopDistance,
            double traillingStopDistance
    ) {
        this(
                BigDecimal.valueOf(hardStopDistance),
                BigDecimal.valueOf(takeProfitDistance),
                BigDecimal.valueOf(entryStopDistance),
                BigDecimal.valueOf(traillingStopDistance)
        );
    }
}
