package com.apssouza.mytrade.trading.forex.feed.price;

import com.apssouza.mytrade.feed.signal.SignalDto;

import java.time.LocalDateTime;
import java.util.List;

public interface SignalFeed {

    List<SignalDto> getSignal(String systemName, LocalDateTime currentTime);
}
