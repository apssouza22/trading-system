package com.apssouza.mytrade.common.appconfig;

/**
 * Interface used to load the gRPC property data
 */
public interface ServerConfigDto {

    int port();

    int numExecutors();

    boolean reflectionEnabled();

    String serviceName();
}
