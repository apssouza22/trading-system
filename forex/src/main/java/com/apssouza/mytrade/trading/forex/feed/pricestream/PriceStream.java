package com.apssouza.mytrade.trading.forex.feed.pricestream;

import java.time.LocalDateTime;

public interface PriceStream {

    void start(LocalDateTime start, LocalDateTime end);

}
