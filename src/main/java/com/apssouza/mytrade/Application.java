package com.apssouza.mytrade;

import com.apssouza.mytrade.trading.forex.session.ExecutionType;
import com.apssouza.mytrade.trading.forex.session.SessionType;
import com.apssouza.mytrade.trading.forex.session.TradingSession;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Application {

    public static void main(String[] args) {
        LocalDate date = LocalDate.of(2018, 9, 1);
        Connection conn = null;

        TradingSession tradingSession = new TradingSession(
                BigDecimal.valueOf(100000l),
                LocalDateTime.of(date, LocalTime.MIN),
                LocalDateTime.of(date.plusDays(30), LocalTime.MIN),
                conn,
                SessionType.LIVE,
                "test",
                ExecutionType.SIMULATED
        );
        tradingSession.start();
    }

}
