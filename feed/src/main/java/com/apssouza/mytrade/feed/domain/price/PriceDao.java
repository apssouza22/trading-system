package com.apssouza.mytrade.feed.domain.price;

import com.apssouza.mytrade.feed.api.PriceDto;

import java.time.LocalDateTime;
import java.util.List;

public interface PriceDao {

    List<PriceDto> getPriceInterval(LocalDateTime start, LocalDateTime end);

    List<PriceDto> getClosestPrice(LocalDateTime time);
}
