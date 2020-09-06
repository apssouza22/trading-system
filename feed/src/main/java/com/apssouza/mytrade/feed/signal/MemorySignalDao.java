package com.apssouza.mytrade.feed.signal;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class MemorySignalDao implements SignalDao {
    private final SignalDao signalSource;
    private List<SignalDto> signals;

    public MemorySignalDao(SignalDao signalSource) {
        this.signalSource = signalSource;
    }

    @Override
    public List<SignalDto> getBySecondAndSource(String systemName, LocalDateTime currentTime) {
        return signals.stream()
                .filter(signal -> signal.getSourceName().equals(systemName) && signal.getCreatedAt().equals(currentTime))
                .collect(Collectors.toList());
    }

    @Override
    public void loadData(LocalDateTime start, LocalDateTime end) {
        this.signals = signalSource.getSignals(start, end, "signal_test");
    }

    @Override
    public List<SignalDto> getSignals(LocalDateTime start, LocalDateTime end, String signal_test) {
        return null;
    }
}
