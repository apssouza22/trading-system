package com.apssouza.mytrade.common.time;

/**
 *
 */
public class Interval {

    private final long years;
    private final long months;
    private final long weeks;
    private final long days;
    private final long hours;
    private final long minutes;
    private final long seconds;
    private final long milliseconds;

    public Interval(
            long years,
            long months,
            long weekends,
            long days,
            long hours,
            long minutes,
            long seconds,
            long milliseconds
    ) {
        this.years = years;
        this.months = months;
        this.weeks = weekends;
        this.days = days;
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
        this.milliseconds = milliseconds;
    }

    public long getDays() {
        return days;
    }

    public long getHours() {
        return hours;
    }

    public long getMinutes() {
        return minutes;
    }

    public long getSeconds() {
        return seconds;
    }

    public long getMilliseconds() {
        return milliseconds;
    }

    public long getYears() {
        return years;
    }

    public long getMonths() {
        return months;
    }

    public long getWeeks() {
        return weeks;
    }

}
