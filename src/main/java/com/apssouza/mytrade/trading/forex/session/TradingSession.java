package com.apssouza.mytrade.trading.forex.session;

import com.apssouza.mytrade.feed.price.PriceDao;
import com.apssouza.mytrade.feed.price.PriceHandler;
import com.apssouza.mytrade.feed.signal.SignalDao;
import com.apssouza.mytrade.feed.signal.SignalHandler;
import com.apssouza.mytrade.trading.forex.execution.ExecutionHandler;
import com.apssouza.mytrade.trading.forex.execution.InteractiveBrokerExecutionHandler;
import com.apssouza.mytrade.trading.forex.execution.SimulatedExecutionHandler;
import com.apssouza.mytrade.trading.forex.feed.HistoricalDbPriceStream;
import com.apssouza.mytrade.trading.forex.feed.PriceStream;
import com.apssouza.mytrade.trading.forex.feed.RealTimeDbPriceStream;
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
import com.apssouza.mytrade.trading.misc.helper.config.Properties;

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
    protected SignalDao signalDao;
    private final PriceDao priceDao;
    protected PriceHandler priceHandler;
    protected ExecutionHandler executionHandler;
    protected PositionSizer positionSizer;
    protected Portfolio portfolio;
    protected PositionExitHandler positionExitHandler;
    protected OrderHandler orderHandler;
    protected SignalHandler signalHandler;
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
            SignalDao signalDao,
            PriceDao priceDao,
            SessionType sessionType,
            String systemName,
            ExecutionType executionType
    ) {
        this.equity = equity;
        this.startDate = startDate;
        this.endDate = endDate;
        this.signalDao = signalDao;
        this.priceDao = priceDao;
        this.sessionType = sessionType;
        this.systemName = systemName;
        this.executionType = executionType;
        this.eventQueue = new LinkedBlockingDeque<>();
        this.configSession();
    }

    private void configSession() {
        this.orderDao = new MemoryOrderDao();
        this.priceHandler = new PriceHandler(this.priceDao);
        this.executionHandler = getExecutionHandler();

        this.positionSizer = new PositionSizerFixed();
        this.portfolio = new Portfolio(this.equity);
        this.positionExitHandler = new PositionExitHandler(this.portfolio, this.priceHandler);
        this.orderHandler = new OrderHandler(this.orderDao, this.positionSizer, this.equity, this.priceHandler, this.portfolio);
        this.signalHandler = new SignalHandler(this.signalDao);
        this.reconciliationHandler = new ReconciliationHandler(this.portfolio, this.executionHandler);
        this.historyHandler = new HistoryBookHandler(this.portfolio, this.priceHandler);
        this.riskManagementHandler = new RiskManagementHandler(
                this.portfolio,
                new PositionSizerFixed(),
                new StopOrderCreatorFixed(new PriceDistanceObject(
                        Properties.hard_stop_loss_distance,
                        Properties.take_profit_distance_fixed,
                        Properties.entry_stop_loss_distance_fixed,
                        Properties.trailing_stop_loss_distance
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
                    this.priceHandler
            );
        }
        return new HistoricalDbPriceStream(eventQueue, priceHandler, priceDao);
    }

    private ExecutionHandler getExecutionHandler() {
        if (this.executionType == ExecutionType.BROKER) {
            return new InteractiveBrokerExecutionHandler(
                    Properties.brokerHost,
                    Properties.brokerPort,
                    Properties.brokerClientId
            );
        }
        return new SimulatedExecutionHandler();

    }


    private EventNotifier setListeners() {
        eventNotifier.addPropertyChangeListener(new FilledOrderListener(portfolio, historyHandler, eventNotifier));
        eventNotifier.addPropertyChangeListener(new OrderCreatedListener(orderHandler));
        eventNotifier.addPropertyChangeListener(new OrderFoundListener(executionHandler, historyHandler, orderHandler, eventNotifier, riskManagementHandler));
        eventNotifier.addPropertyChangeListener(new PortfolioChangedListener(reconciliationHandler));
        eventNotifier.addPropertyChangeListener(new SignalCreatedListener(riskManagementHandler, orderHandler, eventNotifier, historyHandler));
        eventNotifier.addPropertyChangeListener(new StopOrderFilledListener(portfolio, historyHandler));
        eventNotifier.addPropertyChangeListener(new PriceChangedListener(executionHandler, portfolioHandler, signalHandler, orderDao, eventNotifier));
        eventNotifier.addPropertyChangeListener(new SessionFinishedListener(historyHandler));
        return eventNotifier;
    }


    public void start() {
        this.runSession();
    }

    protected void runSession() {
        printSessionStartMsg();
        this.executionHandler.closeAllPositions();
        this.executionHandler.cancelOpenLimitOrders();
        this.priceDao.loadData(startDate, startDate.plusDays(1));
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



