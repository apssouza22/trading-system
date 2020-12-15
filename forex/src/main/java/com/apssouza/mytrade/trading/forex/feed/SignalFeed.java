package com.apssouza.mytrade.trading.forex.feed;

import com.apssouza.mytrade.feed.api.SignalDto;

import java.time.LocalDateTime;
import java.util.List;

public interface SignalFeed {

    List<SignalDto> getSignal(String systemName, LocalDateTime currentTime);
}
