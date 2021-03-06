package com.apssouza.mytrade.trading.forex.feed;

import com.apssouza.mytrade.trading.api.SessionType;
import com.apssouza.mytrade.trading.forex.session.event.Event;

import java.util.concurrent.BlockingQueue;

public class PriceStreamFactory {

    private PriceStreamFactory() {
    }

    public static PriceStream factory(
            final SessionType sessionType,
            final BlockingQueue<Event> eventQueue,
            final PriceFeed priceFeed
    ) {
        if (sessionType == SessionType.LIVE) {
            return new RealtimePriceStream(
                    eventQueue,
                    priceFeed
            );
        }
        return new HistoricalPriceStream(eventQueue, priceFeed);
    }
}
