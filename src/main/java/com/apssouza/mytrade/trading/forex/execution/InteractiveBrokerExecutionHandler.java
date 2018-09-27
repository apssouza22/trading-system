package com.apssouza.mytrade.trading.forex.execution;

public class InteractiveBrokerExecutionHandler implements ExecutionHandler {

    private final String brokerHost;
    private final String brokerPort;
    private final String brokerClientId;

    public InteractiveBrokerExecutionHandler(String brokerHost, String brokerPort, String brokerClientId) {
        this.brokerHost = brokerHost;
        this.brokerPort = brokerPort;
        this.brokerClientId = brokerClientId;
    }

    @Override
    public void closeAllPositions() {

    }

    @Override
    public void cancelOpenLimitOrders() {

    }
}
