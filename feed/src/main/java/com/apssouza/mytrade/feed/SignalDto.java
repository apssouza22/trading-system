package com.apssouza.mytrade.feed;

import java.time.LocalDateTime;

public class SignalDto {
    private final LocalDateTime createdAt;
    private final String action;
    private final String symbol;
    private final String sourceName;

    public SignalDto(LocalDateTime createdAt, String action, String symbol, String sourceName) {
        this.createdAt = createdAt;
        this.action = action;
        this.symbol = symbol;
        this.sourceName = sourceName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getAction() {
        return action;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getSourceName() {
        return sourceName;
    }
}
