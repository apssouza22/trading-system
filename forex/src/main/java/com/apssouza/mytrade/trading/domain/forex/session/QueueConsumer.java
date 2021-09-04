package com.apssouza.mytrade.trading.domain.forex.session;

import com.apssouza.mytrade.trading.domain.forex.portfolio.PortfolioHandler;
import com.apssouza.mytrade.trading.domain.forex.session.event.Event;
import com.apssouza.mytrade.trading.domain.forex.session.event.EventType;
import com.apssouza.mytrade.trading.domain.forex.statistics.HistoryBookHandler;
import com.apssouza.mytrade.trading.domain.forex.common.ForexException;

import java.time.LocalDateTime;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

public class QueueConsumer extends Thread {
    private final BlockingQueue<Event> eventQueue;
    private final HistoryBookHandler historyHandler;
    private final PortfolioHandler portfolioHandler;
    private final EventNotifier notifier;
    private final LocalDateTime endDate;

    private static Logger log = Logger.getLogger(QueueConsumer.class.getSimpleName());

    public QueueConsumer(
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
                var logMsg = String.format(
                        "%s - %s - %s",
                        event.getType().name(),
                        event.getTimestamp(),
                        event.getPrice().get("AUDUSD").close()
                );
                log.info(logMsg);
                historyHandler.startCycle(event.getTimestamp());

                notifier.notify(event);
                if (event.getType() == EventType.PRICE_CHANGED) {
                    this.portfolioHandler.createStopOrder(event);
                }

                portfolioHandler.getPortfolio().printPortfolio();

                historyHandler.endCycle();

                if (event.getType() == EventType.SESSION_FINISHED) {
                    return;
                }
            } catch (InterruptedException ex) {
                throw new ForexException(ex);
            }
        }
    }
}
