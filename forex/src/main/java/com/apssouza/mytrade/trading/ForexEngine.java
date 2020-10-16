package com.apssouza.mytrade.trading;

import com.apssouza.mytrade.feed.FeedBuilder;
import com.apssouza.mytrade.trading.forex.session.TradingSession;
import com.apssouza.mytrade.trading.misc.ForexException;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import static java.time.LocalDate.of;


public class ForexEngine {

    private TradingSession tradingSession;

    public static void main(String[] args) {
        var date = of(2018, 9, 10);

        var systemName = "signal_test";
        var dto = new ForexDto(
                systemName,
                LocalDateTime.of(date.minusDays(20), LocalTime.MIN),
                LocalDateTime.of(date.plusDays(6), LocalTime.MIN),
                BigDecimal.valueOf(100000l),
                SessionType.BACK_TEST,
                ExecutionType.SIMULATED
        );
        var engine = new ForexEngine();
        engine.setUp(dto);
        engine.start();
    }

    public void start() {
        tradingSession.start();
    }

    public void setUp(final ForexDto dto) {
        var feed = new FeedBuilder()
                .withStartTime(dto.startDay())
                .withEndTime(dto.endDay())
                .withSignalName(dto.systemName())
                //                .withConnection(getConnection())
                .build();

        this.tradingSession = new ForexBuilder()
                .withSystemName(dto.systemName())
                .withStartTime(dto.startDay())
                .withEndTime(dto.endDay())
                .withEquity(dto.equity())
                .withSessionType(dto.sessionType())
                .withExecutionType(dto.executionType())
                .withFeed(feed)
                .build();
    }

    private static Connection getConnection() throws ForexException {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException ex) {
            throw new ForexException(ex);
        }
        String url = "jdbc:h2:mem:";

        Connection con;
        try {
            con = DriverManager.getConnection(url);
        } catch (SQLException ex) {
            throw new ForexException(ex);
        }
        return con;
    }


}
