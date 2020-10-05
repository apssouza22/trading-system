package com.apssouza.mytrade.runner;

import com.apssouza.mytrade.trading.AppEngine;
import com.apssouza.mytrade.trading.forex.session.TradingSession;
import com.apssouza.mytrade.trading.misc.helper.TradingParams;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


@SpringBootApplication
@Configuration
public class Application {


    public static void main(String[] args) {
        var springApplication = new SpringApplication(Application.class, Application.class);
        var context = springApplication.run(args);

        var date = LocalDate.of(2018, 9, 10);
        TradingParams.tradingStartDay = LocalDateTime.of(date.minusDays(20), LocalTime.MIN);
        TradingParams.tradingEndDay = LocalDateTime.of(date.plusDays(6), LocalTime.MIN);
        TradingParams.tradingStartTime = LocalTime.of(8, 0);
        TradingParams.tradingEndTime = LocalTime.of(20, 0);
        AppEngine.setUpEngine();
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
