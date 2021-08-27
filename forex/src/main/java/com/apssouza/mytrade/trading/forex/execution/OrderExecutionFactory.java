package com.apssouza.mytrade.trading.forex.execution;

import com.apssouza.mytrade.trading.api.ExecutionType;
import com.apssouza.mytrade.trading.forex.common.TradingParams;

public class OrderExecutionFactory {

    public static OrderExecution factory(ExecutionType executionType){
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
