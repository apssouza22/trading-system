package com.apssouza.mytrade.trading.domain.forex.feed;

import com.apssouza.mytrade.feed.api.PriceDto;
import com.apssouza.mytrade.feed.api.SignalDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface FeedService {

    List<SignalDto> getSignal(String systemName, final LocalDateTime currentTime);

    Map<String, PriceDto> getPriceSymbolMapped(LocalDateTime time);
}
