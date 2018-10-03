package com.apssouza.mytrade.trading.forex.risk;

import com.apssouza.mytrade.trading.forex.order.OrderDto;
import com.apssouza.mytrade.trading.forex.portfolio.Portfolio;

import java.util.Collections;
import java.util.List;

public class RiskManagementHandler {

    private final Portfolio portfolio;
    private final PositionSizer positionSizer;

    public RiskManagementHandler(Portfolio portfolio, PositionSizer positionSizer) {
        this.portfolio = portfolio;
        this.positionSizer = positionSizer;
    }

    public List<OrderDto> checkOrders(List<OrderDto> orders) {
        return Collections.emptyList();
    }

    private boolean canOpenPosition() {
        return true;
    }

    private Integer getPositionSize() {
        return 0;
    }

    private void setLeverageStats() {

    }

}
