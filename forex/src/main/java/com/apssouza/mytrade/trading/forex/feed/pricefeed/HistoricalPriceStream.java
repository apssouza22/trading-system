package com.apssouza.mytrade.trading.forex.feed.pricefeed;

import com.apssouza.mytrade.trading.forex.session.event.EndedTradingDayEvent;
import com.apssouza.mytrade.trading.forex.session.event.Event;
import com.apssouza.mytrade.trading.forex.session.event.EventType;
import com.apssouza.mytrade.trading.forex.session.event.PriceChangedEvent;
import com.apssouza.mytrade.trading.forex.session.event.SessionFinishedEvent;
import com.apssouza.mytrade.trading.forex.common.ForexException;
import com.apssouza.mytrade.trading.forex.common.TradingHelper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.BlockingQueue;

/**
 * Provide a feed price steam based on historical price
 */
class HistoricalPriceStream implements PriceStream{

    private final BlockingQueue<Event> eventQueue;
    private final PriceFeedHandler priceFeedHandler;

    public HistoricalPriceStream(BlockingQueue<Event> eventQueue, PriceFeedHandler priceFeedHandler) {
        this.eventQueue = eventQueue;
        this.priceFeedHandler = priceFeedHandler;
    }

    @Override
    public void start(LocalDateTime start, LocalDateTime end) {
        LocalDateTime current = start;
        LocalDate lastDayProcessed = start.toLocalDate().minusDays(1);
        boolean trading = true;
        while (current.compareTo(end) <= 0) {
            if (TradingHelper.hasEndedTradingTime(current) && trading){
                addToQueue(new EndedTradingDayEvent(
                        EventType.ENDED_TRADING_DAY,
                        current,
                        priceFeedHandler.getPriceSymbolMapped(current)
                ));
                trading = false;
            }
            if (!TradingHelper.isTradingTime(current)) {
                current = current.plusSeconds(1L);
                continue;
            }
            trading = true;
            if (lastDayProcessed.compareTo(current.toLocalDate()) < 0) {
                lastDayProcessed = current.toLocalDate();
            }
            PriceChangedEvent event = new PriceChangedEvent(
                    EventType.PRICE_CHANGED,
                    current,
                    priceFeedHandler.getPriceSymbolMapped(current)
            );

            addToQueue(event);
            current = current.plusSeconds(1L);
        }
        SessionFinishedEvent endEvent = new SessionFinishedEvent(
                EventType.SESSION_FINISHED,
                current,
                priceFeedHandler.getPriceSymbolMapped(current)
        );
        addToQueue(endEvent);

    }

    private void addToQueue(Event event) {
        try {
            eventQueue.put(event);
        } catch (InterruptedException e) {
            throw new ForexException(e);
        }
    }
}
