package com.apssouza.mytrade.trading.forex.session.event;

import com.apssouza.mytrade.trading.forex.portfolio.PortfolioHandler;
import com.apssouza.mytrade.trading.forex.session.HistoryBookHandler;

import java.time.LocalDateTime;
import java.util.concurrent.BlockingQueue;

public class EventProcessor extends Thread {
    private final BlockingQueue<Event> eventQueue;
    private final HistoryBookHandler historyHandler;
    private final PortfolioHandler portfolioHandler;
    private final EventNotifier notifier;
    private final LocalDateTime endDate;


    public EventProcessor(
            BlockingQueue<Event> eventQueue,
            HistoryBookHandler historyHandler,
            PortfolioHandler portfolioHandler,
            EventNotifier notifier,
            LocalDateTime endDate

    ) {
        super();

        this.eventQueue = eventQueue;
        this.historyHandler = historyHandler;
        this.portfolioHandler = portfolioHandler;
        this.notifier = notifier;
        this.endDate = endDate;
    }

    @Override
    public void run() {
        for (; ; ) {
            try {
                Event event = eventQueue.take();
                notifier.notify(event);
                if (event.getType() == EventType.PRICE_CHANGED)
                    this.portfolioHandler.createStopOrder(event);

                if (endDate.equals(event.getTimestamp())) {
                    notifier.notify(new SessionFinishedEvent(
                            EventType.SESSION_FINISHED,
                            endDate,
                            event.getPrice()
                    ));
                    return;
                }
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
