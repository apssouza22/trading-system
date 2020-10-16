package com.apssouza.mytrade.feed;

import java.time.LocalDateTime;

public record SignalDto(LocalDateTime createdAt, String action, String symbol, String sourceName) {}
