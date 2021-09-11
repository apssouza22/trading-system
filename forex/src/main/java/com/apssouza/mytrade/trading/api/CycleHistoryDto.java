package com.apssouza.mytrade.trading.api;


import java.time.LocalDateTime;
import java.util.List;

public record CycleHistoryDto(LocalDateTime time, List<TransactionDto> events) {
}
