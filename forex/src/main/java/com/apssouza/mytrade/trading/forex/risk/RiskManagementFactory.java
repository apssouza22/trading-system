package com.apssouza.mytrade.trading.forex.risk;

import com.apssouza.mytrade.trading.forex.portfolio.PortfolioModel;
import com.apssouza.mytrade.trading.forex.risk.stoporder.StopOrderCreator;

public class RiskManagementFactory {

    public static RiskManagementHandler create(PortfolioModel portfolio, StopOrderCreator stopOrderCreator) {
        return new RiskManagementHandler(
                portfolio,
                new PositionExitHandler(portfolio),
                stopOrderCreator
        );
    }
}
