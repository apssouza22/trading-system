package com.apssouza.mytrade.trading.domain.forex.risk.stoporder;

public class StopOrderFactory {

    public static StopOrderCreator factory(
            final StopOrderConfigDto stopOrderDto
    ) {
        return new StopOrderCreatorFixed(stopOrderDto);
    }
}
