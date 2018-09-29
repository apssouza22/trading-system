package com.apssouza.mytrade.feed.price;

import java.math.BigDecimal;

public class PriceDto {

    private final BigDecimal open;
    private final BigDecimal close;
    private final BigDecimal high;
    private final BigDecimal low;
    private final Long volume;

    public PriceDto(BigDecimal open, BigDecimal close, BigDecimal high, BigDecimal low, Long volume) {
        this.open = open;
        this.close = close;
        this.high = high;
        this.low = low;
        this.volume = volume;
    }
}
