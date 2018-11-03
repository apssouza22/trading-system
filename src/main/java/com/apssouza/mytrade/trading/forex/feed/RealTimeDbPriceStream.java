package com.apssouza.mytrade.trading.forex.feed;

import com.apssouza.mytrade.feed.price.PriceDto;
import com.apssouza.mytrade.feed.price.PriceHandler;
import com.apssouza.mytrade.trading.forex.session.event.Event;
import com.apssouza.mytrade.trading.forex.session.event.EventType;
import com.apssouza.mytrade.trading.forex.session.event.LoopEvent;
import com.apssouza.mytrade.trading.forex.session.event.PriceChangedEvent;
import com.apssouza.mytrade.trading.misc.helper.TradingHelper;
import com.apssouza.mytrade.trading.misc.helper.time.DateTimeHelper;
import com.apssouza.mytrade.trading.misc.helper.time.MarketTimeHelper;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;

public class RealTimeDbPriceStream implements PriceStream {

    private final BlockingQueue<Event> eventQueue;
    private final PriceHandler priceHandler;

    public RealTimeDbPriceStream(BlockingQueue<Event> eventQueue, PriceHandler priceHandler) {
        this.eventQueue = eventQueue;
        this.priceHandler = priceHandler;
    }

    public void start(LocalDateTime start, LocalDateTime end) {
        TimerTask repeatedTask = new TimerTask() {
            public void run() {
                LocalDateTime time = LocalDateTime.now(DateTimeHelper.ZONEID_UTC);
                if (!TradingHelper.isTradingTime(time)) {
                    return;
                }
                PriceChangedEvent event = new PriceChangedEvent(
                        EventType.LOOP_FOUND_NEXT,
                        time,
                        priceHandler.getPriceSymbolMapped(time)
                );

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
}
