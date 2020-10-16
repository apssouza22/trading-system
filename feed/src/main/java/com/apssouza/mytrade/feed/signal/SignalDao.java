package com.apssouza.mytrade.feed.signal;

import com.apssouza.mytrade.feed.SignalDto;

import java.time.LocalDateTime;
import java.util.List;

public interface SignalDao {

    List<SignalDto> getSignal(String systemName, LocalDateTime currentTime);

}
