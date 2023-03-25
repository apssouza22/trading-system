package com.apssouza.mytrade.runner;

import com.apssouza.mytrade.trading.api.ExecutionType;
import com.apssouza.mytrade.trading.api.ForexBuilder;
import com.apssouza.mytrade.trading.api.SessionType;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import static java.time.LocalDate.of;


@SpringBootApplication
@Configuration
public class Application {


    public static void main(String[] args) {
//        var springApplication = new SpringApplication(Application.class, Application.class);
//        springApplication.run(args);

        var date = of(2018, 9, 10);
        var systemName = "signal_test";
        LocalDateTime start = LocalDateTime.of(date.minusDays(20), LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(date.plusDays(6), LocalTime.MIN);


        var engine = new ForexBuilder()
                .withSystemName(systemName)
                .withStartTime(start)
                .withEndTime(end)
                .withEquity(BigDecimal.valueOf(100000L))
                .withSessionType(SessionType.BACK_TEST)
                .withExecutionType(ExecutionType.SIMULATED)
                .build();
        engine.start();
    }

}
