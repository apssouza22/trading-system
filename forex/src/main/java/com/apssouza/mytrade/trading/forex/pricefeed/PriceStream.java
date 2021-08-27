package com.apssouza.mytrade.trading.forex.pricefeed;

import java.time.LocalDateTime;


/**
 * Price feed stream API for the Forex system
 */
public interface PriceStream {

    void start(LocalDateTime start, LocalDateTime end);

}
