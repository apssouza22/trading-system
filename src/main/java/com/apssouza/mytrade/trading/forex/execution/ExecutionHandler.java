package com.apssouza.mytrade.trading.forex.execution;

import java.time.LocalDateTime;

public interface ExecutionHandler {
    void closeAllPositions();

    void cancelOpenLimitOrders();

    void setCurrentTime(LocalDateTime currentTime);
}
