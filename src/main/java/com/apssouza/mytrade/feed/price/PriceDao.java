package com.apssouza.mytrade.feed.price;

import java.time.LocalDateTime;

public interface PriceDao {
    void loadData(LocalDateTime start, LocalDateTime end);
}
