package com.apssouza.mytrade.trading.domain.forex.feed.pricefeed;

import com.apssouza.mytrade.feed.api.PriceDto;
import com.apssouza.mytrade.trading.domain.forex.common.events.PriceChangedEvent;
import com.apssouza.mytrade.trading.domain.forex.common.events.EndedTradingDayEvent;
import com.apssouza.mytrade.trading.domain.forex.common.events.Event;
import com.apssouza.mytrade.trading.domain.forex.common.events.SessionFinishedEvent;
import com.apssouza.mytrade.trading.domain.forex.common.ForexException;
import com.apssouza.mytrade.trading.domain.forex.common.TradingHelper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
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
                    current,
                    priceFeedHandler.getPriceSymbolMapped(current)
            );

            addToQueue(event);
            current = current.plusSeconds(1L);
        }
        SessionFinishedEvent endEvent = new SessionFinishedEvent(
                current,
                priceFeedHandler.getPriceSymbolMapped(current)
        );
        addToQueue(endEvent);
    }

    @Override
    public Map<String, PriceDto> getPriceSymbolMapped(LocalDateTime current) {
        return priceFeedHandler.getPriceSymbolMapped(current);
    }

    private void addToQueue(Event event) {
        try {
            eventQueue.put(event);
        } catch (InterruptedException e) {
            throw new ForexException(e);
        }
    }
}
