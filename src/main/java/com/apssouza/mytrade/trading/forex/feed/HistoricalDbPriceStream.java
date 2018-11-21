package com.apssouza.mytrade.trading.forex.feed;

import com.apssouza.mytrade.feed.price.MemoryPriceDao;
import com.apssouza.mytrade.feed.price.PriceDao;
import com.apssouza.mytrade.feed.price.PriceHandler;
import com.apssouza.mytrade.trading.forex.session.SessionType;
import com.apssouza.mytrade.trading.forex.session.event.Event;
import com.apssouza.mytrade.trading.forex.session.event.EventType;
import com.apssouza.mytrade.trading.forex.session.event.PriceChangedEvent;
import com.apssouza.mytrade.trading.forex.session.event.SessionFinishedEvent;
import com.apssouza.mytrade.trading.misc.helper.TradingHelper;
import com.apssouza.mytrade.trading.misc.helper.config.Properties;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.BlockingQueue;

public class HistoricalDbPriceStream implements PriceStream{

    private final BlockingQueue<Event> eventQueue;
    private final PriceHandler priceHandler;
    private final PriceDao priceMemoryDao;

    public HistoricalDbPriceStream(BlockingQueue<Event> eventQueue, PriceHandler priceHandler, PriceDao priceMemoryDao) {
        this.eventQueue = eventQueue;
        this.priceHandler = priceHandler;
        this.priceMemoryDao = priceMemoryDao;
    }

    public void start(LocalDateTime start, LocalDateTime end) {
        LocalDateTime current = start;
        LocalDate lastDayProcessed = start.toLocalDate().minusDays(1);
        while (current.compareTo(end) <= 0) {
            if (!TradingHelper.isTradingTime(current)) {
                continue;
            }
            if (lastDayProcessed.compareTo(current.toLocalDate()) < 0) {
                this.processStartDay(current);
                lastDayProcessed = current.toLocalDate();
            }
            PriceChangedEvent event = new PriceChangedEvent(
                    EventType.PRICE_CHANGED,
                    current,
                    priceHandler.getPriceSymbolMapped(current)
            );

            addToQueue(event);
            current = current.plusSeconds(1L);
        }
        SessionFinishedEvent endEvent = new SessionFinishedEvent(
                EventType.PRICE_CHANGED,
                current,
                priceHandler.getPriceSymbolMapped(current)
        );
        addToQueue(endEvent);

    }

    private void addToQueue(Event event) {
        try {
            eventQueue.put(event);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    protected void processStartDay(LocalDateTime currentTime) {
        if (Properties.sessionType == SessionType.BACK_TEST)
            this.priceMemoryDao.loadData(currentTime, currentTime.plusDays(1));
    }
}
