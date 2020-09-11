package com.apssouza.mytrade.feed.price;

import java.time.LocalDateTime;
import java.util.List;

public interface PriceDao {

    List<PriceDto> getPriceInterval(LocalDateTime start, LocalDateTime end);

    List<PriceDto> getClosestPrice(LocalDateTime time);
}
