package com.apssouza.mytrade.trading.misc.helper.time;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

/**
 * Date time converter helper class
 */
public class DateTimeConverter {


    public static Date getDateFromLocalDate(LocalDate localDate) {
        return java.sql.Date.valueOf(localDate);
    }

    public static LocalDate getLocalDateFromDate(Date date) {
        return new java.sql.Date(date.getTime()).toLocalDate();
    }


    public static Date getDateFromStringAndFormat(String sDate, String sformat) {
        SimpleDateFormat format = new SimpleDateFormat(sformat);
        try {
            return format.parse(sDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

}
