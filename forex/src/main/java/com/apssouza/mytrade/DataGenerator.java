package com.apssouza.mytrade;

import com.apssouza.mytrade.feed.price.PriceDao;
import com.apssouza.mytrade.feed.price.PriceDto;
import com.apssouza.mytrade.feed.signal.SignalDao;
import com.apssouza.mytrade.feed.signal.SignalDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DataGenerator implements PriceDao, SignalDao {


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

    @Override
    public List<SignalDto> getSignal(String systemName, LocalDateTime currentTime) {
        return null;
    }

    @Override
    public void loadData(LocalDateTime start, LocalDateTime end) {

    }

    @Override
    public List<PriceDto> getPriceInterval(LocalDateTime start, LocalDateTime end) {
        return getPrices(start, end);
    }

    @Override
    public List<PriceDto> getClosestPrice(LocalDateTime time) {
        return null;
    }
}
