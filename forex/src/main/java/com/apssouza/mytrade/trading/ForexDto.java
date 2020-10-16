package com.apssouza.mytrade.trading;

import com.apssouza.mytrade.trading.forex.session.ExecutionType;
import com.apssouza.mytrade.trading.forex.session.SessionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ForexDto(
        String systemName,
        LocalDateTime startDay,
        LocalDateTime endDay,
        BigDecimal equity,
        SessionType sessionType,
        ExecutionType executionType
) {
}
