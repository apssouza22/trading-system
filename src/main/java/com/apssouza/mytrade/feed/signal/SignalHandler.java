package com.apssouza.mytrade.feed.signal;

import java.time.LocalDateTime;
import java.util.List;

public class SignalHandler {
    private SignalDao signalDao;

    public SignalHandler(SignalDao signalDao) {

        this.signalDao = signalDao;
    }

    public List<SignalDto> getRealtimeSignal(String systemName) {
        return null;
    }

    public List<SignalDto> findbySecondSystem(String systemName, LocalDateTime currentTime) {
        return null;
    }
}
