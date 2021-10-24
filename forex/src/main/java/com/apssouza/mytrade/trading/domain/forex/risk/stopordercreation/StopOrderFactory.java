package com.apssouza.mytrade.trading.domain.forex.risk.stopordercreation;

public class StopOrderFactory {

    public static StopOrderCreator factory(
            StopOrderConfigDto stopOrderDto
    ) {
        return new StopOrderCreatorFixed(stopOrderDto);
    }
}
