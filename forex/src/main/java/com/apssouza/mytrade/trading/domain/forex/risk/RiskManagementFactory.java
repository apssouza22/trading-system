package com.apssouza.mytrade.trading.domain.forex.risk;

import com.apssouza.mytrade.trading.domain.forex.risk.stopordercreation.StopOrderCreator;

public class RiskManagementFactory {

    public static RiskManagementService create(StopOrderCreator stopOrderCreator) {
        return new RiskManagementService(
                new PositionExitChecker(),
                stopOrderCreator
        );
    }
}
