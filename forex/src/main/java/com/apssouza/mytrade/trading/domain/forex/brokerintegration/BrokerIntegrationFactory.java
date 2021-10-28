package com.apssouza.mytrade.trading.domain.forex.brokerintegration;

import com.apssouza.mytrade.trading.api.ExecutionType;
import com.apssouza.mytrade.trading.domain.forex.common.TradingParams;

public class BrokerIntegrationFactory {

    public static BrokerIntegrationService factory(ExecutionType executionType){
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
