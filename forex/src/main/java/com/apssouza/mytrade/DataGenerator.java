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

    public List<SignalDto> getSignals(LocalDateTime start, LocalDateTime end, String signalName) {
        LocalDateTime current = start;
        List<SignalDto> signals = new ArrayList<>();
        Random r = new Random();
        r.setSeed(1);
        while (current.compareTo(end) <= 0) {
            current = current.plusMinutes(1L);
            int hasSignal = getRandomNumberInRange(0, 1, r);
            int signalAction = getRandomNumberInRange(0, 1, r);
            if (hasSignal > 0) {
                signals.add(new SignalDto(
                        current,
                        signalAction > 0 ? "Buy" : "Sell",
                        "AUDUSD",
                        signalName
                ));
            }

        }
        return signals;
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

    private static int getRandomNumberInRange(int min, int max, Random r) {
        return r.ints(min, (max + 1)).limit(1).findFirst().getAsInt();
    }

    private static double getRandomPrice(Random r) {
        return r.doubles(1, 2).limit(1).findFirst().getAsDouble();
    }

    @Override
    public List<SignalDto> getBySecondAndSource(String systemName, LocalDateTime currentTime) {
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
