package com.apssouza.mytrade.feed.domain.signal;

import com.apssouza.mytrade.feed.api.SignalDto;

import java.time.LocalDateTime;
import java.util.List;

public interface SignalDao {

    List<SignalDto> getSignal(String systemName, LocalDateTime currentTime);

}
