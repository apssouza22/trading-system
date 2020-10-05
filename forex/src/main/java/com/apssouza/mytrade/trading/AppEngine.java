package com.apssouza.mytrade.trading;

import com.apssouza.mytrade.feed.price.MemoryPriceDao;
import com.apssouza.mytrade.feed.price.PriceHandler;
import com.apssouza.mytrade.feed.signal.MemorySignalDao;
import com.apssouza.mytrade.feed.signal.SignalHandler;
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
        var priceMemoryDao = new MemoryPriceDao(TradingParams.tradingStartDay, TradingParams.tradingEndDay);
        var systemName = "signal_test";
        var signalMemoryDao =
                new MemorySignalDao(TradingParams.tradingStartDay, TradingParams.tradingEndDay, systemName);
        var priceAdapter = new PriceFeedAdapter(new PriceHandler(priceMemoryDao));
        var signalFeed = new SignalFeedAdapter(new SignalHandler(signalMemoryDao));
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
