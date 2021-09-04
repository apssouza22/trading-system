package com.apssouza.mytrade.trading.domain.forex.feed.pricefeed;

import com.apssouza.mytrade.trading.api.SessionType;
import com.apssouza.mytrade.trading.domain.forex.session.event.Event;

import java.util.concurrent.BlockingQueue;


/**
 * Price feed stream API factory
 */
public class PriceStreamFactory {

    private PriceStreamFactory() {
    }

    public static PriceStream factory(
            final SessionType sessionType,
            final BlockingQueue<Event> eventQueue,
            final PriceFeedHandler priceFeedHandler
    ) {
        if (sessionType == SessionType.LIVE) {
            return new RealtimePriceStream(
                    eventQueue,
                    priceFeedHandler
            );
        }
        return new HistoricalPriceStream(eventQueue, priceFeedHandler);
    }
}
