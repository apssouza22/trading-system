package com.apssouza.mytrade.feed.signal;

import java.sql.Connection;

public class SqlSignalDao implements SignalDao {

    private Connection connection;

    public SqlSignalDao(Connection connection) {
        this.connection = connection;
    }
}
