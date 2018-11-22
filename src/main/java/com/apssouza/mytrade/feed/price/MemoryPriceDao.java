package com.apssouza.mytrade.feed.price;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MemoryPriceDao implements PriceDao {

    private final PriceDao priceDao;
    private List<PriceDto> prices;

    public MemoryPriceDao(PriceDao priceDao) {
        this.priceDao = priceDao;
    }

    @Override
    public void loadData(LocalDateTime start, LocalDateTime end) {
        this.prices = this.priceDao.getPriceInterval(start, end);
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

}
