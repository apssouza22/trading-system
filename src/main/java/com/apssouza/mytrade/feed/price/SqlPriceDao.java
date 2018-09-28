package com.apssouza.mytrade.feed.price;

import java.sql.Connection;

public class SqlPriceDao implements PriceDao {

    private final Connection connection;

    public SqlPriceDao(Connection connection) {
        this.connection = connection;
    }
}
