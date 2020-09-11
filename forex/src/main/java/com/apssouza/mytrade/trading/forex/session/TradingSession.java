package com.apssouza.mytrade.trading.forex.session;

import com.apssouza.mytrade.trading.forex.execution.ExecutionHandler;
import com.apssouza.mytrade.trading.forex.execution.InteractiveBrokerExecutionHandler;
import com.apssouza.mytrade.trading.forex.execution.SimulatedExecutionHandler;
import com.apssouza.mytrade.trading.forex.feed.price.HistoricalDbPriceStream;
import com.apssouza.mytrade.trading.forex.feed.price.PriceFeed;
import com.apssouza.mytrade.trading.forex.feed.price.PriceStream;
import com.apssouza.mytrade.trading.forex.feed.price.RealTimeDbPriceStream;
import com.apssouza.mytrade.trading.forex.feed.signal.SignalFeed;
import com.apssouza.mytrade.trading.forex.order.MemoryOrderDao;
import com.apssouza.mytrade.trading.forex.order.OrderHandler;
import com.apssouza.mytrade.trading.forex.portfolio.Portfolio;
import com.apssouza.mytrade.trading.forex.portfolio.PortfolioHandler;
import com.apssouza.mytrade.trading.forex.portfolio.ReconciliationHandler;
import com.apssouza.mytrade.trading.forex.risk.PositionExitHandler;
import com.apssouza.mytrade.trading.forex.risk.PositionSizer;
import com.apssouza.mytrade.trading.forex.risk.PositionSizerFixed;
import com.apssouza.mytrade.trading.forex.risk.RiskManagementHandler;
import com.apssouza.mytrade.trading.forex.risk.stoporder.PriceDistanceObject;
import com.apssouza.mytrade.trading.forex.risk.stoporder.fixed.StopOrderCreatorFixed;
import com.apssouza.mytrade.trading.forex.session.event.Event;
import com.apssouza.mytrade.trading.forex.session.listener.*;
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

    protected MemoryOrderDao orderDao;
    protected final PriceFeed priceFeed;
    protected final SignalFeed signalFeed;
    protected ExecutionHandler executionHandler;
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
        this.orderDao = new MemoryOrderDao();

        this.executionHandler = getExecutionHandler();

        this.positionSizer = new PositionSizerFixed();
        this.portfolio = new Portfolio(this.equity);
        this.positionExitHandler = new PositionExitHandler(this.portfolio, this.priceFeed);
        this.orderHandler = new OrderHandler(this.orderDao, this.positionSizer);

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
            return new RealTimeDbPriceStream(
                    eventQueue,
                    this.priceFeed
            );
        }
        return new HistoricalDbPriceStream(eventQueue, priceFeed);
    }

    private ExecutionHandler getExecutionHandler() {
        if (this.executionType == ExecutionType.BROKER) {
            return new InteractiveBrokerExecutionHandler(
                    TradingParams.brokerHost,
                    TradingParams.brokerPort,
                    TradingParams.brokerClientId
            );
        }
        return new SimulatedExecutionHandler();

    }


    private EventNotifier setListeners() {
        eventNotifier.addPropertyChangeListener(new FilledOrderListener(portfolio, historyHandler, eventNotifier));
        eventNotifier.addPropertyChangeListener(new OrderCreatedListener(orderHandler));
        eventNotifier.addPropertyChangeListener(new OrderFoundListener(executionHandler, historyHandler, orderHandler, eventNotifier, riskManagementHandler));
        eventNotifier.addPropertyChangeListener(new PortfolioChangedListener(reconciliationHandler, portfolioHandler));
        eventNotifier.addPropertyChangeListener(new SignalCreatedListener(riskManagementHandler, orderHandler, eventNotifier, historyHandler));
        eventNotifier.addPropertyChangeListener(new StopOrderFilledListener(portfolio, historyHandler, eventNotifier));
        eventNotifier.addPropertyChangeListener(new PriceChangedListener(executionHandler, portfolioHandler, signalFeed, orderDao, eventNotifier));
        eventNotifier.addPropertyChangeListener(new SessionFinishedListener(historyHandler));
        eventNotifier.addPropertyChangeListener(new EndedTradingDayListener(portfolioHandler));
        return eventNotifier;
    }


    public void start() {
        this.runSession();
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



