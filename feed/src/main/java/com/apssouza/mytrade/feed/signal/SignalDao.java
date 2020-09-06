package com.apssouza.mytrade.feed.signal;

import java.time.LocalDateTime;
import java.util.List;

public interface SignalDao {

    List<SignalDto> getBySecondAndSource(String systemName, LocalDateTime currentTime);

    void loadData(LocalDateTime start, LocalDateTime end);

    List<SignalDto> getSignals(LocalDateTime start, LocalDateTime end, String signal_test);
}
