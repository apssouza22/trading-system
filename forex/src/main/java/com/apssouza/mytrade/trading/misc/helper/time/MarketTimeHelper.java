package com.apssouza.mytrade.trading.misc.helper.time;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.TimeZone;


public class MarketTimeHelper {

    private static final LocalTime OPEN_WEEEK_MONDAY = LocalTime.of(6, 0);

    private static final LocalTime CLOSE_WEEEK_SATURDAY = LocalTime.of(6, 0);

    private static final LocalTime END_OF_DAY = LocalTime.of(18, 0);


    private static final LocalTime START_OF_DAY = LocalTime.of(5, 55);

    private static final TimeZone UTC_TIMEZONE = TimeZone.getTimeZone("utc");


    public static boolean isMarketOpened(LocalDateTime dateTime) {
        switch (dateTime.getDayOfWeek()) {
            case TUESDAY:
            case WEDNESDAY:
            case THURSDAY:
            case FRIDAY:
                return true;
            case MONDAY:
                return dateTime.toLocalTime().isBefore(START_OF_DAY) ? false : true;
            case SATURDAY:
                return dateTime.toLocalTime().isAfter(CLOSE_WEEEK_SATURDAY) ? false : true;
            case SUNDAY:
                return false;
            default:
                return false;
        }
    }

    /**
     * @param dateTime
     * @return
     */
    public static LocalDateTime getNextOpenTime(LocalDateTime dateTime) {
        LocalDateTime nextOpenTime = LocalDateTime.of(
                dateTime.getYear(),
                dateTime.getMonth(),
                dateTime.getDayOfMonth(),
                START_OF_DAY.getHour(), START_OF_DAY.getMinute()
        );

        while (nextOpenTime.getDayOfWeek() != DayOfWeek.MONDAY) {
            nextOpenTime = nextOpenTime.plusDays(1);
        }
        return nextOpenTime;
    }


    /**
     * Is end of day
     * @param dateTime
     * @return
     */
    public static boolean isEOD(LocalDateTime dateTime) {
        if (dateTime.toLocalTime().isAfter(END_OF_DAY)) {
            return true;
        }
        return false;
    }

    /**
     *
     * @param date
     * @return
     */
    public static LocalTime getOpenWeekTimeMonday(LocalDate date) {
        return START_OF_DAY;
    }

    /**
     *
     * @param date
     * @return
     */
    public static LocalTime getCloseWeekTimeSaturday(LocalDate date) {
        return CLOSE_WEEEK_SATURDAY;
    }

}
