package com.apssouza.mytrade.feed.price;

import java.sql.Connection;
import java.time.LocalDateTime;

public class SqlPriceDao implements PriceDao {

    private final Connection connection;

    public SqlPriceDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void loadData(LocalDateTime start, LocalDateTime end) {

    }
}
