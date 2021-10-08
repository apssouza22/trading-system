package com.apssouza.mytrade.feed.domain.price;

import com.apssouza.mytrade.feed.api.PriceDto;

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
    private static final Random RANDOM = new Random();

    static {
        RANDOM.setSeed(1);
    }

    public MemoryPriceDao(LocalDateTime start, LocalDateTime end) {
        this.prices = getPrices(start, end);
        prices.sort(Comparator.comparing(PriceDto::timestamp));
    }

    @Override
    public List<PriceDto> getPriceInterval(LocalDateTime start, LocalDateTime end) {
        return prices.parallelStream()
                .filter(i -> i.timestamp().compareTo(start) >= 0 && i.timestamp().compareTo(end) <= 0)
                .collect(Collectors.toList());
    }

    @Override
    public List<PriceDto> getClosestPrice(LocalDateTime time) {
        Optional<PriceDto> first = prices.stream()
                .sorted(Comparator.comparing(PriceDto::timestamp).reversed())
                .filter(i -> i.timestamp().compareTo(time) <= 0)
                .findFirst();

        return prices.stream()
                .filter(i -> i.timestamp().equals(first.get().timestamp()))
                .collect(Collectors.toList());
    }


    public static List<PriceDto> getPrices(LocalDateTime start, LocalDateTime end) {
        LocalDateTime current = start;
        List<PriceDto> prices = new ArrayList<>();

        while (current.compareTo(end) <= 0) {
            BigDecimal close = BigDecimal.valueOf(getRandomPrice(RANDOM));
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
