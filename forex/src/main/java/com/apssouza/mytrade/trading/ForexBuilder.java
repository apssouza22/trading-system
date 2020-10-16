package com.apssouza.mytrade.trading;

import com.apssouza.mytrade.feed.FeedModule;
import com.apssouza.mytrade.trading.forex.feed.price.PriceFeedAdapter;
import com.apssouza.mytrade.trading.forex.feed.signal.SignalFeedAdapter;
import com.apssouza.mytrade.trading.forex.session.ExecutionType;
import com.apssouza.mytrade.trading.forex.session.SessionType;
import com.apssouza.mytrade.trading.forex.session.TradingSession;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ForexBuilder {

    private FeedModule feed;
    private String systemName;
    private LocalDateTime start;
    private LocalDateTime end;
    private SessionType sessionType;
    private ExecutionType executionType;
    private BigDecimal equity;

    public ForexBuilder withFeed(FeedModule feed) {
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

    public TradingSession build() {
        var priceAdapter = new PriceFeedAdapter(feed);
        var signalFeed = new SignalFeedAdapter(feed);

        var tradingSession = new TradingSession(
                equity,
                start,
                end,
                signalFeed,
                sessionType,
                systemName,
                getSafeExecutionType(),
                priceAdapter
        );
        registerShutDownListener(tradingSession);
        return tradingSession;
    }

    private static void registerShutDownListener(final TradingSession tradingSession) {
        Runtime.getRuntime().addShutdownHook(new Thread(tradingSession::shutdown));
    }

    private ExecutionType getSafeExecutionType() {
        return sessionType == SessionType.BACK_TEST ? ExecutionType.SIMULATED : executionType;
    }
}
