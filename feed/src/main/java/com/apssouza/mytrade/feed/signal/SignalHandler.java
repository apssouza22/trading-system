package com.apssouza.mytrade.feed.signal;

import com.apssouza.mytrade.feed.api.SignalDto;

import java.time.LocalDateTime;
import java.util.List;

public class SignalHandler {
    private SignalDao signalDao;

    public SignalHandler(SignalDao signalDao) {
        this.signalDao = signalDao;
    }

    public List<SignalDto> getSignal(String sourceName, LocalDateTime currentTime) {
        return this.signalDao.getSignal(sourceName, currentTime);
    }
}
