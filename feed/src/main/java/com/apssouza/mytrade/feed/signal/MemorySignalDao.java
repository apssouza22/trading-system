package com.apssouza.mytrade.feed.signal;

import com.apssouza.mytrade.feed.api.SignalDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class MemorySignalDao implements SignalDao {
    private final List<SignalDto> signals;

    public MemorySignalDao(LocalDateTime start, LocalDateTime end, String signalName) {
        this.signals = loadSignals( start,  end,  signalName);
    }

    public static List<SignalDto>  loadSignals(LocalDateTime start, LocalDateTime end, String signalName) {
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

    private static int getRandomNumberInRange(int min, int max, Random r) {
        return r.ints(min, (max + 1))
                .limit(1)
                .findFirst()
                .getAsInt();
    }

    @Override
    public List<SignalDto> getSignal(String systemName, LocalDateTime currentTime) {
        return signals.stream()
                .filter(signal -> signal.sourceName().equals(systemName) && signal.createdAt().equals(currentTime))
                .collect(Collectors.toList());
    }

}
