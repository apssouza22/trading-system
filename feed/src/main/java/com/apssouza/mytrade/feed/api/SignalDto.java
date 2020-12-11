package com.apssouza.mytrade.feed.api;

import java.time.LocalDateTime;

public record SignalDto(LocalDateTime createdAt, String action, String symbol, String sourceName) {}
