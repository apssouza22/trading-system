package com.apssouza.mytrade.runner;

import com.apssouza.mytrade.trading.ForexDto;
import com.apssouza.mytrade.trading.ForexEngine;
import com.apssouza.mytrade.trading.forex.session.ExecutionType;
import com.apssouza.mytrade.trading.forex.session.SessionType;
import com.apssouza.mytrade.trading.misc.helper.TradingParams;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


@SpringBootApplication
@Configuration
public class Application {


    public static void main(String[] args) {
        var springApplication = new SpringApplication(Application.class, Application.class);
        springApplication.run(args);

        var date = LocalDate.of(2018, 9, 10);

        var systemName = "signal_test";
        var dto = new ForexDto(
                systemName,
                LocalDateTime.of(date.minusDays(20), LocalTime.MIN),
                LocalDateTime.of(date.plusDays(6), LocalTime.MIN),
                BigDecimal.valueOf(100000l),
                SessionType.BACK_TEST,
                ExecutionType.SIMULATED
        );
        ForexEngine.setUpEngine(dto);
    }

}
