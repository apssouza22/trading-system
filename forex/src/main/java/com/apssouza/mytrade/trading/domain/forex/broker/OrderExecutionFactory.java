package com.apssouza.mytrade.trading.domain.forex.broker;

import com.apssouza.mytrade.trading.api.ExecutionType;
import com.apssouza.mytrade.trading.domain.forex.common.TradingParams;

public class OrderExecutionFactory {

    public static BrokerService factory(ExecutionType executionType){
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
