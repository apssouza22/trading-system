package com.apssouza.mytrade.trading.forex.execution;

import com.apssouza.mytrade.trading.api.ExecutionType;
import com.apssouza.mytrade.trading.misc.helper.TradingParams;

public class OrderExecutionFactory {

    public static OrderExecution factory(final ExecutionType executionType){
        if (executionType == ExecutionType.BROKER) {
            return new InteractiveBrokerOrderExecution(
                    TradingParams.brokerHost,
                    TradingParams.brokerPort,
                    TradingParams.brokerClientId
            );
        }
        return new SimulatedOrderExecution();
    }
}
