package com.apssouza.mytrade.trading.domain.forex.feed.pricefeed;

import com.apssouza.mytrade.trading.api.SessionType;
import com.apssouza.mytrade.trading.domain.forex.common.Event;
import com.apssouza.mytrade.trading.domain.forex.feed.FeedService;

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

}
