package com.apssouza.mytrade.common.misc.helper.time;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DateRangeHelper {

    public static List<LocalDate> getDatesBetween(LocalDate startDate, LocalDate endDate) {

        long intervalBetween = ChronoUnit.DAYS.between(startDate, endDate);
        return IntStream.iterate(0, i -> i + 1)
                .limit(intervalBetween)
                .mapToObj(i -> startDate.plusDays(i))
                .collect(Collectors.toList());
    }
    
    public static List<LocalDateTime> getMinutesBetween(LocalDateTime startDate, LocalDateTime endDate) {
        long intervalBetween = ChronoUnit.MINUTES.between(startDate, endDate);
        return IntStream.iterate(0, i -> i + 1)
                .limit(intervalBetween)
                .mapToObj(i -> startDate.plusMinutes(i))
                .collect(Collectors.toList());
    }

    public static List<LocalDateTime> getSecondsBetween(LocalDateTime startDate, LocalDateTime endDate) {
        long intervalBetween = ChronoUnit.SECONDS.between(startDate, endDate);
        return IntStream.iterate(0, i -> i + 1)
                .limit(intervalBetween)
                .mapToObj(i -> startDate.plusSeconds(i))
                .collect(Collectors.toList());
    }
    
    
}
