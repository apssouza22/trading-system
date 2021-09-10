package com.apssouza.mytrade.trading.api;

import com.apssouza.mytrade.feed.api.FeedBuilder;
import com.apssouza.mytrade.feed.api.FeedModule;
import com.apssouza.mytrade.trading.domain.forex.feed.FeedService;
import com.apssouza.mytrade.trading.domain.forex.feed.TradingFeed;
import com.apssouza.mytrade.trading.domain.forex.feed.pricefeed.PriceFeedHandler;
import com.apssouza.mytrade.trading.domain.forex.feed.signalfeed.SignalFeedFactory;
import com.apssouza.mytrade.trading.domain.forex.feed.signalfeed.SignalFeedHandler;
import com.apssouza.mytrade.trading.domain.forex.session.TradingSession;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ForexBuilder {

    private FeedService feed;
    private String systemName;
    private LocalDateTime start;
    private LocalDateTime end;
    private SessionType sessionType;
    private ExecutionType executionType;
    private BigDecimal equity;

    public ForexBuilder withFeed(FeedService feed) {
        this.feed = feed;
        return this;
    }

    public ForexBuilder withSystemName(String systemName) {
        this.systemName = systemName;
        return this;
    }

    public ForexBuilder withStartTime(LocalDateTime start) {
        this.start = start;
        return this;
    }

    public ForexBuilder withEndTime(LocalDateTime end) {
        this.end = end;
        return this;
    }

    public ForexBuilder withSessionType(SessionType sessionType) {
        this.sessionType = sessionType;
        return this;
    }

    public ForexBuilder withExecutionType(ExecutionType executionType) {
        this.executionType = executionType;
        return this;
    }

    public ForexBuilder withEquity(BigDecimal equity) {
        this.equity = equity;
        return this;
    }

    public ForexEngine build() {
        FeedModule feedModule = new FeedBuilder()
                .withStartTime(start)
                .withEndTime(end)
                .withSignalName(systemName)
                .build();

        if (feed == null){
            this.feed = new TradingFeed(feedModule);
        }
        var tradingSession = new TradingSession(
                equity,
                start,
                end,
                sessionType,
                systemName,
                getSafeExecutionType(),
                feed
        );
        registerShutDownListener(tradingSession);
        return new ForexEngine(tradingSession);
    }

    private static void registerShutDownListener(TradingSession tradingSession) {
        Thread shutdown = new Thread(tradingSession::shutdown);
        shutdown.setDaemon(true);
        Runtime.getRuntime().addShutdownHook(shutdown);
    }

    private ExecutionType getSafeExecutionType() {
        return sessionType == SessionType.BACK_TEST ? ExecutionType.SIMULATED : executionType;
    }
}
