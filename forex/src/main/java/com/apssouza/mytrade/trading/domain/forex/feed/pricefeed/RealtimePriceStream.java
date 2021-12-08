package com.apssouza.mytrade.trading.domain.forex.feed.pricefeed;

import com.apssouza.mytrade.common.time.DateTimeHelper;
import com.apssouza.mytrade.feed.api.PriceDto;
import com.apssouza.mytrade.trading.domain.forex.common.events.Event;
import com.apssouza.mytrade.trading.domain.forex.common.TradingHelper;
import com.apssouza.mytrade.trading.domain.forex.common.events.PriceChangedEvent;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;

/**
 * Provide a feed price steam based on real time price feed
 */
class RealtimePriceStream implements PriceStream {

    private final BlockingQueue<Event> eventQueue;
    private final PriceFeedHandler priceHandler;

    public RealtimePriceStream(BlockingQueue<Event> eventQueue, PriceFeedHandler priceHandler) {
        this.eventQueue = eventQueue;
        this.priceHandler = priceHandler;
    }

    @Override
    public void start(LocalDateTime start, LocalDateTime end) {
        TimerTask repeatedTask = new TimerTask() {
            @Override
            public void run() {
                LocalDateTime time = LocalDateTime.now(DateTimeHelper.ZONEID_UTC);
                if (!TradingHelper.isTradingTime(time)) {
                    return;
                }
                var event = new PriceChangedEvent(time, priceHandler.getPriceSymbolMapped(time));

                try {
                    eventQueue.put(event);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        Timer timer = new Timer("PriceStream");

        long delay = 1000L;
        long period = 1000L;
        timer.scheduleAtFixedRate(repeatedTask, delay, period);
    }

    @Override
    public Map<String, PriceDto> getPriceSymbolMapped(LocalDateTime current) {
        return priceHandler.getPriceSymbolMapped(current);
    }
}
