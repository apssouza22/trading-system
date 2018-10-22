package com.apssouza.mytrade.trading.forex.session.event;

import com.apssouza.mytrade.trading.forex.portfolio.PortfolioHandler;
import com.apssouza.mytrade.trading.forex.session.HistoryBookHandler;

import java.util.concurrent.BlockingQueue;

public class EventProcessor extends Thread {
    private final BlockingQueue<Event> eventQueue;
    private final HistoryBookHandler historyHandler;
    private final PortfolioHandler portfolioHandler;
    private final EventNotifier notifier;


    public EventProcessor(
            BlockingQueue<Event> eventQueue,
            HistoryBookHandler historyHandler,
            PortfolioHandler portfolioHandler,
            EventNotifier notifier

    ) {
        super();

        this.eventQueue = eventQueue;
        this.historyHandler = historyHandler;
        this.portfolioHandler = portfolioHandler;
        this.notifier = notifier;
    }

    @Override
    public void run() {
        for (; ; ) {
            try {
                Event event = eventQueue.take();
                notifier.notify(event);
                this.portfolioHandler.createStopOrder(event);
                this.historyHandler.process(event);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
