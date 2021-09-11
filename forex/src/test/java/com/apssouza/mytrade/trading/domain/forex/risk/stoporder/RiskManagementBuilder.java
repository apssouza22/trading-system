package com.apssouza.mytrade.trading.domain.forex.risk.stoporder;

import com.apssouza.mytrade.trading.domain.forex.common.TradingParams;
import com.apssouza.mytrade.trading.domain.forex.order.StopOrderBuilder;
import com.apssouza.mytrade.trading.domain.forex.portfolio.PortfolioModel;
import com.apssouza.mytrade.trading.domain.forex.risk.RiskManagementFactory;
import com.apssouza.mytrade.trading.domain.forex.risk.RiskManagementHandler;
import com.apssouza.mytrade.trading.domain.forex.risk.stoporder.StopOrderConfigDto;
import com.apssouza.mytrade.trading.domain.forex.risk.stoporder.StopOrderCreatorFixed;

import org.mockito.Mockito;

import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

public class RiskManagementBuilder {

    public RiskManagementHandler build() {
        var stopOrder = new StopOrderConfigDto(
                TradingParams.hard_stop_loss_distance,
                TradingParams.take_profit_distance_fixed,
                TradingParams.entry_stop_loss_distance_fixed,
                TradingParams.trailing_stop_loss_distance
        );
        return RiskManagementFactory.create(new PortfolioModel(BigDecimal.TEN), new StopOrderCreatorFixed(stopOrder));
    }
}
