package com.apssouza.mytrade.feed.price;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

public class MemoryPriceDao implements PriceDao {

    private List<PriceDto> prices;

    public MemoryPriceDao(LocalDateTime start, LocalDateTime end) {
        this.prices = getPrices(start, end);
        prices.sort(Comparator.comparing(PriceDto::getTimestamp));
    }

    @Override
    public List<PriceDto> getPriceInterval(LocalDateTime start, LocalDateTime end) {
        return prices.parallelStream()
                .filter( i -> i.getTimestamp().compareTo(start) >= 0 && i.getTimestamp().compareTo(end) <= 0 )
                .collect(Collectors.toList());
    }

    @Override
    public List<PriceDto> getClosestPrice(LocalDateTime time) {
        Optional<PriceDto> first = prices.stream()
                .sorted(Comparator.comparing(PriceDto::getTimestamp).reversed())
                .filter(i -> i.getTimestamp().compareTo(time) <= 0)
                .findFirst();

        return prices.stream()
                .filter(i -> i.getTimestamp().equals(first.get().getTimestamp()))
                .collect(Collectors.toList());
    }


    public static List<PriceDto> getPrices(LocalDateTime start, LocalDateTime end) {
        LocalDateTime current = start;
        List<PriceDto> prices = new ArrayList<>();
        Random r = new Random();
        r.setSeed(1);
        while (current.compareTo(end) <= 0) {
            BigDecimal close = BigDecimal.valueOf(getRandomPrice(r));
            prices.add(new PriceDto(
                    current,
                    close,
                    close,
                    close,
                    close,
                    "AUDUSD"
            ));
            current = current.plusMinutes(1L);
        }
        return prices;
    }


    private static double getRandomPrice(Random r) {
        return r.doubles(1, 2).limit(1).findFirst().getAsDouble();
    }

}
