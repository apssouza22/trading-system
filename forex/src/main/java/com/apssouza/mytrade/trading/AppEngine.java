package com.apssouza.mytrade.trading;

import com.apssouza.mytrade.feed.FeedBuilder;
import com.apssouza.mytrade.trading.forex.feed.price.PriceFeedAdapter;
import com.apssouza.mytrade.trading.forex.feed.signal.SignalFeedAdapter;
import com.apssouza.mytrade.trading.forex.session.ExecutionType;
import com.apssouza.mytrade.trading.forex.session.SessionType;
import com.apssouza.mytrade.trading.forex.session.TradingSession;
import com.apssouza.mytrade.trading.misc.helper.TradingParams;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


public class AppEngine {


    public static void main(String[] args) {
        var date = LocalDate.of(2018, 9, 10);
        TradingParams.tradingStartDay = LocalDateTime.of(date.minusDays(20), LocalTime.MIN);
        TradingParams.tradingEndDay = LocalDateTime.of(date.plusDays(6), LocalTime.MIN);
        TradingParams.tradingStartTime = LocalTime.of(8, 0);
        TradingParams.tradingEndTime = LocalTime.of(20, 0);
        setUpEngine();
    }

    public static void setUpEngine() {
        var systemName = "signal_test";
        var feed = new FeedBuilder()
                .withStartTime(TradingParams.tradingStartDay)
                .withEndTime(TradingParams.tradingEndDay)
                .withSignalName(systemName)
                .build();

        var priceAdapter = new PriceFeedAdapter(feed);
        var signalFeed = new SignalFeedAdapter(feed);

        var tradingSession = new TradingSession(
                BigDecimal.valueOf(100000l),
                TradingParams.tradingStartDay,
                TradingParams.tradingEndDay,
                signalFeed,
                SessionType.BACK_TEST,
                systemName,
                ExecutionType.SIMULATED,
                priceAdapter
        );
        registerShutDownListener(tradingSession);
        tradingSession.start();
    }

    private static Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("org.h2.Driver"); // (1)
        String url = "jdbc:h2:mem:";
        Connection con = DriverManager.getConnection(url);
        return con;
    }

    private static void registerShutDownListener(final TradingSession tradingSession) {
        Runtime.getRuntime().addShutdownHook(new Thread(tradingSession::shutdown));
    }

}
