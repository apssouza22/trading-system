package com.apssouza.mytrade.trading.forex.risk.stoporder;

public class StopOrderFactory {

    public static StopOrderCreator factory(
            final StopOrderConfigDto stopOrderDto
    ) {
        return new StopOrderCreatorFixed(stopOrderDto);
    }
}
