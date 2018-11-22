package com.apssouza.mytrade;

import com.apssouza.mytrade.feed.price.MemoryPriceDao;
import com.apssouza.mytrade.feed.price.PriceDao;
import com.apssouza.mytrade.feed.signal.MemorySignalDao;
import com.apssouza.mytrade.feed.signal.SignalDao;
import com.apssouza.mytrade.trading.forex.session.ExecutionType;
import com.apssouza.mytrade.trading.forex.session.SessionType;
import com.apssouza.mytrade.trading.forex.session.TradingSession;
import com.apssouza.mytrade.trading.misc.helper.config.Properties;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Application {


    public static void main(String[] args){
        LocalDate date = LocalDate.of(2018, 9, 10);
        Properties.tradingStartDay = LocalDateTime.of(date, LocalTime.MIN);
        Properties.tradingEndDay = LocalDateTime.of(date.plusDays(1), LocalTime.MIN);
        Properties.tradingStartTime = LocalTime.of(8,0);
        Properties.tradingEndTime = LocalTime.of(20,0);
        DataGenerator dataGenerator = new DataGenerator();
        PriceDao priceMemoryDao = new MemoryPriceDao(dataGenerator);
        SignalDao signalMemoryDao = new MemorySignalDao(dataGenerator);
        signalMemoryDao.loadData(Properties.tradingStartDay, Properties.tradingEndDay);
        TradingSession tradingSession = new TradingSession(
                BigDecimal.valueOf(100000l),
                Properties.tradingStartDay,
                Properties.tradingEndDay,
                signalMemoryDao,
                priceMemoryDao,
                SessionType.BACK_TEST,
                "signal_test",
                ExecutionType.SIMULATED
        );
        tradingSession.start();
    }

    private static Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("org.h2.Driver"); // (1)
        String url = "jdbc:h2:mem:";
        Connection con = DriverManager.getConnection(url);
        return con;
    }

}
