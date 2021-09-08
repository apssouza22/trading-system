package com.apssouza.mytrade.feed.api;

import com.apssouza.mytrade.feed.domain.price.MemoryPriceDao;
import com.apssouza.mytrade.feed.domain.price.PriceHandler;
import com.apssouza.mytrade.feed.domain.price.SqlPriceDao;
import com.apssouza.mytrade.feed.domain.signal.MemorySignalDao;
import com.apssouza.mytrade.feed.domain.signal.SignalHandler;
import com.apssouza.mytrade.feed.domain.signal.SqlSignalDao;

import java.sql.Connection;
import java.time.LocalDateTime;

public class FeedBuilder {

    private Connection connection;
    private String signalName;
    private LocalDateTime end;
    private LocalDateTime start;

    public FeedBuilder withConnection(Connection connection) {
        this.connection = connection;
        return this;
    }

    public FeedBuilder withStartTime(LocalDateTime start) {
        this.start = start;
        return this;
    }

    public FeedBuilder withEndTime(LocalDateTime end) {
        this.end = end;
        return this;
    }

    public FeedBuilder withSignalName(String signalName) {
        this.signalName = signalName;
        return this;
    }


    public FeedModule build() {
        if (this.connection != null) {
            return new FeedModule(
                    new SignalHandler(new SqlSignalDao(this.connection)),
                    new PriceHandler(new SqlPriceDao(this.connection))
            );
        }
        return new FeedModule(
                new SignalHandler(new MemorySignalDao(this.start, this.end, this.signalName)),
                new PriceHandler(new MemoryPriceDao(this.start, this.end))
        );
    }

}
