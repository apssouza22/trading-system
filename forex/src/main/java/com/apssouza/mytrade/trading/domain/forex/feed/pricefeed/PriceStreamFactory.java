package com.apssouza.mytrade.trading.domain.forex.feed.pricefeed;

import com.apssouza.mytrade.feed.api.FeedModule;
import com.apssouza.mytrade.trading.api.SessionType;
import com.apssouza.mytrade.trading.domain.forex.common.Event;
import com.apssouza.mytrade.trading.domain.forex.execution.OrderExecution;
import com.apssouza.mytrade.trading.domain.forex.feed.FeedService;
import com.apssouza.mytrade.trading.domain.forex.feed.signalfeed.SignalFeedHandler;
import com.apssouza.mytrade.trading.domain.forex.order.OrderHandler;
import com.apssouza.mytrade.trading.domain.forex.portfolio.PortfolioHandler;
import com.apssouza.mytrade.trading.domain.forex.session.EventNotifier;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;


/**
 * Price feed stream API factory
 */
public class PriceStreamFactory {

    public static PriceStream create(
             SessionType sessionType,
             BlockingQueue<Event> eventQueue,
             FeedService feed
    ) {
        var priceFeedHandler = new PriceFeedHandler(feed);
        if (sessionType == SessionType.LIVE) {
            return new RealtimePriceStream(
                    eventQueue,
                    priceFeedHandler
            );
        }
        return new HistoricalPriceStream(eventQueue, priceFeedHandler);
    }

    public static List<PriceChangedListener> createListeners(
             OrderExecution executionHandler,  PortfolioHandler portfolioHandler,
             SignalFeedHandler signalFeedHandler,
             OrderHandler orderHandler,
             EventNotifier eventNotifier
    ) {
        return Collections.singletonList(new PriceChangedListener(
                executionHandler,
                portfolioHandler,
                signalFeedHandler,
                orderHandler,
                eventNotifier
        ));
    }
}
