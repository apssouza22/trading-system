package com.apssouza.mytrade.trading.misc.helper.time;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

/**
 * A day helper
 */
public class DayHelper {

    public static boolean isWeekend(LocalDate date) {
        Date dtDate  = DateTimeConverter.getDateFromLocalDate(date);
        Calendar c1 = Calendar.getInstance();
        c1.setTime(dtDate);
        return isSaturday(dtDate) || isSunday(dtDate);
    }

    public static boolean isWeekDay(LocalDate date) {
        return !isWeekend(date);
    }

    public static boolean isMonday(Date date) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(date);
        return c1.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY;
    }

    public static boolean isTusday(Date date) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(date);
        return c1.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY;
    }

    public static boolean isWednesday(Date date) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(date);
        return c1.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY;
    }

    public static boolean isThursday(Date date) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(date);
        return c1.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY;
    }

    public static boolean isFriday(Date date) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(date);
        return c1.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY;
    }

    public static boolean isSaturday(Date date) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(date);
        System.out.println(Calendar.SATURDAY);
        return c1.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY;
    }

    public static boolean isSunday(Date date) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(date);
        return c1.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
    }
    
    public static boolean isToday(Date date){
        Calendar today = Calendar.getInstance();
        Calendar dateCalender = Calendar.getInstance();
        dateCalender.setTime(date);
        return  today.get(Calendar.YEAR) == dateCalender.get(Calendar.YEAR) &&
                          today.get(Calendar.DAY_OF_YEAR) == dateCalender.get(Calendar.DAY_OF_YEAR);

    }
}
