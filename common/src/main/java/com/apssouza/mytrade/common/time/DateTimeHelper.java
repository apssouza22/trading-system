package com.apssouza.mytrade.common.time;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Date time helper
 */
public class DateTimeHelper {
    public static final ZoneId DEFAULT_ZONEID = ZoneId.systemDefault();
    public static final ZoneId ZONEID_UTC = ZoneId.of("UTC");
    public static final ZoneOffset ZONEOFFSET_UTC = ZoneOffset.UTC;

    public static Interval calculate(LocalDateTime start, LocalDateTime end) {
        // count between dates
        long years = ChronoUnit.YEARS.between(start, end);
        long months = ChronoUnit.MONTHS.between(start, end);
        long weeks = ChronoUnit.WEEKS.between(start, end);
        long days = ChronoUnit.DAYS.between(start, end);
        long hours = ChronoUnit.HOURS.between(start, end);
        long minutes = ChronoUnit.MINUTES.between(start, end);
        long seconds = ChronoUnit.SECONDS.between(start, end);
        long milis = ChronoUnit.MILLIS.between(start, end);
        return new Interval(years, months, weeks, days, hours, minutes, seconds, milis);
    }

    public static Interval calculate(String start, String end) {
        LocalDateTime startDate = LocalDateTime.parse(start, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        LocalDateTime endDate = LocalDateTime.parse(end, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        return calculate(startDate, endDate);
    }

    public static boolean isPastDate(LocalDateTime date) {
        LocalDateTime now = LocalDateTime.now();
        return date.isBefore(now);
    }

    public static boolean isPastDate(String date) {
        LocalDateTime startDate = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        return isPastDate(startDate);
    }

    public static String getCurrentTimeFormat(String format) {
        LocalDateTime now = LocalDateTime.now();
        return now.format(DateTimeFormatter.ofPattern(format));
    }

    public static boolean compare(LocalDateTime dt1, String operator, LocalDateTime dt2) {
        Interval interval = DateTimeHelper.calculate(dt1, dt2);
        if (operator.equals(">")) {
            return interval.getMilliseconds() < 0;
        }
        if (operator.equals(">=")) {
            return interval.getMilliseconds() <= 0;
        }
        if (operator.equals("<")) {
            return interval.getMilliseconds() > 0;
        }
        if (operator.equals("<=")) {
            return interval.getMilliseconds() >= 0;
        }

        throw new RuntimeException("Invalid operator");
    }

    public static boolean compare(LocalDate dt1, String operator, LocalDate dt2) {
        return DateTimeHelper.compare(
                LocalDateTime.of(dt1, LocalTime.MIN),
                operator,
                LocalDateTime.of(dt2, LocalTime.MIN)
        );
    }


}
