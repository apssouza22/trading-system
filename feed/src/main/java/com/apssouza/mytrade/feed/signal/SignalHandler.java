package com.apssouza.mytrade.feed.signal;

import java.time.LocalDateTime;
import java.util.List;

public class SignalHandler {
    private SignalDao signalDao;

    public SignalHandler(SignalDao signalDao) {
        this.signalDao = signalDao;
    }

    public List<SignalDto> getRealtimeSignal(String sourceName) {
        return null;
    }

    public List<SignalDto> findbySecondAndSource(String sourceName, LocalDateTime currentTime) {
        return this.signalDao.getBySecondAndSource(sourceName, currentTime);
    }
}
