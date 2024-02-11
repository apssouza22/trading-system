package com.apssouza.mytrade.trading.domain.forex.riskmanagement;

import com.apssouza.mytrade.trading.domain.forex.riskmanagement.stopordercreation.StopOrderCreator;

public class RiskManagementFactory {

    public static RiskManagementService create(StopOrderCreator stopOrderCreator) {
        return new RiskManagementServiceImpl(
                new PositionExitChecker(),
                stopOrderCreator
        );
    }
}
