package com.apssouza.mytrade.trading.forex.session;

import com.apssouza.mytrade.trading.api.ExecutionType;
import com.apssouza.mytrade.trading.api.SessionType;
import com.apssouza.mytrade.trading.forex.execution.OrderExecution;
import com.apssouza.mytrade.trading.forex.execution.OrderExecutionFactory;
import com.apssouza.mytrade.trading.forex.feed.PriceFeed;
import com.apssouza.mytrade.trading.forex.feed.SignalFeed;
import com.apssouza.mytrade.trading.forex.feed.pricestream.HistoricalPriceStream;
import com.apssouza.mytrade.trading.forex.feed.pricestream.PriceStream;
import com.apssouza.mytrade.trading.forex.feed.pricestream.RealtimePriceStream;
import com.apssouza.mytrade.trading.forex.order.OrderHandler;
import com.apssouza.mytrade.trading.forex.order.OrderHandlerFactory;
import com.apssouza.mytrade.trading.forex.portfolio.Portfolio;
import com.apssouza.mytrade.trading.forex.portfolio.PortfolioHandler;
import com.apssouza.mytrade.trading.forex.portfolio.ReconciliationHandler;
import com.apssouza.mytrade.trading.forex.risk.PositionExitHandler;
import com.apssouza.mytrade.trading.forex.risk.PositionSizer;
import com.apssouza.mytrade.trading.forex.risk.PositionSizerFixed;
import com.apssouza.mytrade.trading.forex.risk.RiskManagementHandler;
import com.apssouza.mytrade.trading.forex.risk.stoporder.PriceDistanceObject;
import com.apssouza.mytrade.trading.forex.risk.stoporder.fixed.StopOrderCreatorFixed;
import com.apssouza.mytrade.trading.forex.session.event.EndedTradingDayEvent;
import com.apssouza.mytrade.trading.forex.session.event.Event;
import com.apssouza.mytrade.trading.forex.session.event.EventType;
import com.apssouza.mytrade.trading.forex.session.listener.EndedTradingDayListener;
import com.apssouza.mytrade.trading.forex.session.listener.FilledOrderListener;
import com.apssouza.mytrade.trading.forex.session.listener.OrderCreatedListener;
import com.apssouza.mytrade.trading.forex.session.listener.OrderFoundListener;
import com.apssouza.mytrade.trading.forex.session.listener.PortfolioChangedListener;
import com.apssouza.mytrade.trading.forex.session.listener.PriceChangedListener;
import com.apssouza.mytrade.trading.forex.session.listener.SessionFinishedListener;
import com.apssouza.mytrade.trading.forex.session.listener.SignalCreatedListener;
import com.apssouza.mytrade.trading.forex.session.listener.StopOrderFilledListener;
import com.apssouza.mytrade.trading.forex.statistics.HistoryBookHandler;
import com.apssouza.mytrade.trading.forex.statistics.TransactionsExporter;
import com.apssouza.mytrade.trading.misc.helper.TradingParams;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Logger;

public class TradingSession {

    protected final BigDecimal equity;
    protected final LocalDateTime startDate;
    protected final LocalDateTime endDate;
    protected final SessionType sessionType;
    protected final String systemName;
    protected final ExecutionType executionType;

    protected final PriceFeed priceFeed;
    protected final SignalFeed signalFeed;
    protected OrderExecution executionHandler;
    protected PositionSizer positionSizer;
    protected Portfolio portfolio;
    protected PositionExitHandler positionExitHandler;
    protected OrderHandler orderHandler;
    protected ReconciliationHandler reconciliationHandler;
    protected HistoryBookHandler historyHandler;
    protected PriceStream priceStream;
    protected PortfolioHandler portfolioHandler;
    protected boolean processedEndDay;
    protected RiskManagementHandler riskManagementHandler;
    protected final BlockingQueue<Event> eventQueue;
    protected EventNotifier eventNotifier;

    private static Logger log = Logger.getLogger(PortfolioHandler.class.getSimpleName());

    public TradingSession(
            BigDecimal equity,
            LocalDateTime startDate,
            LocalDateTime endDate,
            SignalFeed signalFeed,
            SessionType sessionType,
            String systemName,
            ExecutionType executionType,
            PriceFeed priceFeed
    ) {
        this.equity = equity;
        this.startDate = startDate;
        this.endDate = endDate;
        this.sessionType = sessionType;
        this.systemName = systemName;
        this.executionType = executionType;
        this.eventQueue = new LinkedBlockingDeque<>();
        this.priceFeed = priceFeed;
        this.signalFeed = signalFeed;
        this.configSession();
    }

    private void configSession() {
        this.executionHandler = OrderExecutionFactory.factory(this.executionType);
        this.positionSizer = new PositionSizerFixed();
        this.portfolio = new Portfolio(this.equity);
        this.positionExitHandler = new PositionExitHandler(this.portfolio, this.priceFeed);
        this.orderHandler = OrderHandlerFactory.factory(this.positionSizer);

        this.reconciliationHandler = new ReconciliationHandler(this.portfolio, this.executionHandler);
        this.historyHandler = new HistoryBookHandler(new TransactionsExporter());
        this.riskManagementHandler = new RiskManagementHandler(
                this.portfolio,
                new PositionSizerFixed(),
                new StopOrderCreatorFixed(new PriceDistanceObject(
                        TradingParams.hard_stop_loss_distance,
                        TradingParams.take_profit_distance_fixed,
                        TradingParams.entry_stop_loss_distance_fixed,
                        TradingParams.trailing_stop_loss_distance
                ))
        );

        eventNotifier = new EventNotifier();
        this.portfolioHandler = new PortfolioHandler(
                this.equity,
                this.orderHandler,
                this.positionExitHandler,
                this.executionHandler,
                this.portfolio,
                this.reconciliationHandler,
                this.historyHandler,
                this.riskManagementHandler,
                eventNotifier
        );
        this.eventNotifier = setListeners();

        this.priceStream = getPriceStream();
    }

    private PriceStream getPriceStream() {
        if (this.sessionType == SessionType.LIVE) {
            return new RealtimePriceStream(
                    eventQueue,
                    this.priceFeed
            );
        }
        return new HistoricalPriceStream(eventQueue, priceFeed);
    }

    private EventNotifier setListeners() {
        eventNotifier.addPropertyChangeListener(new FilledOrderListener(portfolio, historyHandler, eventNotifier));
        eventNotifier.addPropertyChangeListener(new OrderCreatedListener(orderHandler));
        eventNotifier.addPropertyChangeListener(new OrderFoundListener(executionHandler, historyHandler, orderHandler, eventNotifier, riskManagementHandler));
        eventNotifier.addPropertyChangeListener(new PortfolioChangedListener(reconciliationHandler, portfolioHandler));
        eventNotifier.addPropertyChangeListener(new SignalCreatedListener(riskManagementHandler, orderHandler, eventNotifier, historyHandler));
        eventNotifier.addPropertyChangeListener(new StopOrderFilledListener(portfolio, historyHandler, eventNotifier));
        eventNotifier.addPropertyChangeListener(new PriceChangedListener(executionHandler, portfolioHandler, signalFeed, orderHandler, eventNotifier));
        eventNotifier.addPropertyChangeListener(new SessionFinishedListener(historyHandler));
        eventNotifier.addPropertyChangeListener(new EndedTradingDayListener(portfolioHandler));
        return eventNotifier;
    }


    public void start() {
        this.runSession();
    }

    public void shutdown() {
        log.warning("Shutting down the application");
        var current = LocalDateTime.now();
        var event = new EndedTradingDayEvent(
                EventType.ENDED_TRADING_DAY,
                current,
                priceFeed.getPriceSymbolMapped(current)
        );
        eventNotifier.notify(event);
    }

    protected void runSession() {
        printSessionStartMsg();
        this.executionHandler.closeAllPositions();
        this.executionHandler.cancelOpenLimitOrders();
        startEventProcessor();
        priceStream.start(startDate, endDate);
    }

    private void printSessionStartMsg() {
        if (this.sessionType == SessionType.BACK_TEST) {
            System.out.println(String.format("Running Backtest from %s to %s", this.startDate, this.endDate));
        } else {
            System.out.println(String.format("Running Real-time Session until %s", this.endDate));
        }
    }

    private void startEventProcessor() {
        QueueConsumer queueConsumer = new QueueConsumer(
                eventQueue,
                historyHandler,
                portfolioHandler,
                eventNotifier,
                endDate
        );
        queueConsumer.start();
    }

}



