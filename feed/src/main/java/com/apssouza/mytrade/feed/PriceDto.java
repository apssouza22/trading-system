package com.apssouza.mytrade.feed;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;

public class PriceDto {

    private final LocalDateTime timestamp;
    private final BigDecimal open;
    private final BigDecimal close;
    private final BigDecimal high;
    private final BigDecimal low;
    private final String symbol;

    public String getSymbol() {
        return symbol;
    }

    public PriceDto(
            LocalDateTime timestamp,
            BigDecimal open,
            BigDecimal close,
            BigDecimal high,
            BigDecimal low,
            String symbol
    ) {
        this.timestamp = timestamp;
        this.open = open;
        this.close = close;
        this.high = high;
        this.low = low;
        this.symbol = symbol;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public BigDecimal getOpen() {
        return open;
    }

    public BigDecimal getClose() {
        return close;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public BigDecimal getLow() {
        return low;
    }
}
