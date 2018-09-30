package com.apssouza.mytrade.feed.price;

import tech.tablesaw.api.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MemoryPriceDao implements PriceDao {

    private final PriceDao priceDao;
    private Table priceTable;

    public MemoryPriceDao(PriceDao priceDao) {
        this.priceDao = priceDao;
    }

    @Override
    public void loadData(LocalDateTime start, LocalDateTime end) {
        List<PriceDto> prices = this.priceDao.getPriceInterval(start, end);
        List<String> symbol = new ArrayList<>();
        List<LocalDateTime> timestamp = new ArrayList<>();
        List<Double> open = new ArrayList<>();
        List<Double> close = new ArrayList<>();
        List<Double> high = new ArrayList<>();
        List<Double> low = new ArrayList<>();

        for (PriceDto price : prices) {
            symbol.add(price.getSymbol());
            timestamp.add(price.getTimestamp());
            open.add(price.getOpen().doubleValue());
            close.add(price.getClose().doubleValue());
            high.add(price.getHigh().doubleValue());
            low.add(price.getLow().doubleValue());
        }

        this.priceTable = Table.create("Prices");

        this.priceTable.addColumns(
                StringColumn.create("symbol", symbol),
                DateTimeColumn.create("timestamp", timestamp),
                DoubleColumn.create("open", open.toArray(new Double[open.size()])),
                DoubleColumn.create("close", close.toArray(new Double[open.size()])),
                DoubleColumn.create("high", high.toArray(new Double[open.size()])),
                DoubleColumn.create("low", low.toArray(new Double[open.size()]))
        );

    }

    @Override
    public List<PriceDto> getPriceInterval(LocalDateTime start, LocalDateTime end) {
        DateTimeColumn ts = this.priceTable.dateTimeColumn("timestamp");
        Table result = this.priceTable.where(ts.isOnOrAfter(start).and(ts.isOnOrBefore(end)));
        List<PriceDto> list = new ArrayList<>();
        for (Row row : result) {
            list.add(new PriceDto(
                    row.getDateTime("timestamp"),
                    BigDecimal.valueOf(row.getDouble("open")),
                    BigDecimal.valueOf(row.getDouble("close")),
                    BigDecimal.valueOf(row.getDouble("high")),
                    BigDecimal.valueOf(row.getDouble("low")),
                    row.getString("symbol")
            ));
        }
        return list;
    }
}
