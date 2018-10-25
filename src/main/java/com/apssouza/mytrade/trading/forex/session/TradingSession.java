package com.apssouza.mytrade.trading.forex.session;

import com.apssouza.mytrade.feed.price.MemoryPriceDao;
import com.apssouza.mytrade.feed.price.PriceDao;
import com.apssouza.mytrade.feed.price.PriceHandler;
import com.apssouza.mytrade.feed.price.SqlPriceDao;
import com.apssouza.mytrade.feed.signal.SignalDao;
import com.apssouza.mytrade.feed.signal.SignalHandler;
import com.apssouza.mytrade.feed.signal.SqlSignalDao;
import com.apssouza.mytrade.trading.forex.execution.ExecutionHandler;
import com.apssouza.mytrade.trading.forex.execution.InteractiveBrokerExecutionHandler;
import com.apssouza.mytrade.trading.forex.execution.SimulatedExecutionHandler;
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
import com.apssouza.mytrade.trading.forex.session.event.EventNotifier;
import com.apssouza.mytrade.trading.forex.session.event.EventProcessor;
import com.apssouza.mytrade.trading.forex.session.event.LoopEvent;
import com.apssouza.mytrade.trading.forex.session.listener.*;
import com.apssouza.mytrade.trading.misc.helper.TradingHelper;
import com.apssouza.mytrade.trading.misc.helper.config.Properties;
import com.apssouza.mytrade.trading.misc.helper.time.DateRangeHelper;
import com.apssouza.mytrade.trading.misc.loop.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Logger;

public class TradingSession {

    protected final BigDecimal equity;
    protected final LocalDateTime startDate;
    protected final LocalDateTime endDate;
    protected final Connection connection;
    protected final SessionType sessionType;
    protected final String systemName;
    protected final ExecutionType executionType;

    protected MemoryOrderDao orderDao;
    protected PriceDao priceSqlDao;
    protected SignalDao signalDao;
    protected MemoryPriceDao priceMemoryDao;
    protected PriceHandler priceHandler;
    protected ExecutionHandler executionHandler;
    protected PositionSizer positionSizer;
    protected Portfolio portfolio;
    protected PositionExitHandler positionExitHandler;
    protected OrderHandler orderHandler;
    protected SignalHandler signalHandler;
    protected ReconciliationHandler reconciliationHandler;
    protected HistoryBookHandler historyHandler;
    protected EventLoop eventLoop;
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
            Connection connection,
            SessionType sessionType,
            String systemName,
            ExecutionType executionType
    ) {

        this.equity = equity;
        this.startDate = startDate;
        this.endDate = endDate;
        this.connection = connection;
        this.sessionType = sessionType;
        this.systemName = systemName;
        this.executionType = executionType;
        this.eventQueue = new LinkedBlockingDeque<>();
        this.configSession();
    }

    private void configSession() {
        this.orderDao = new MemoryOrderDao();
        this.priceSqlDao = new SqlPriceDao(this.connection);
        this.signalDao = new SqlSignalDao(this.connection);

        this.priceHandler = getPriceHandler();
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


        this.portfolioHandler = new PortfolioHandler(
                this.equity,
                this.orderHandler,
                this.positionExitHandler,
                this.executionHandler,
                this.portfolio,
                this.reconciliationHandler,
                this.historyHandler,
                this.riskManagementHandler,
                eventQueue
        );

        this.eventNotifier = getEventNotifier();
        this.eventLoop = getEventLoop();
    }

    private EventLoop getEventLoop() {
        if (this.sessionType == SessionType.LIVE) {
            return new RealEventLoop(
                    LocalDateTime.now(),
                    this.endDate,
                    Duration.ofSeconds(1),
                    new CurrentTimeCreator(),
                    this.priceHandler
            );
        }
        List<LocalDateTime> range = DateRangeHelper.getSecondsBetween(this.startDate, this.endDate);
        return new RangeEventLoop(range, this.priceHandler);

    }

    private ExecutionHandler getExecutionHandler() {
        if (this.executionType == ExecutionType.BROKER) {
            return new InteractiveBrokerExecutionHandler(
                    Properties.brokerHost,
                    Properties.brokerPort,
                    Properties.brokerClientId
            );
        }
        return new SimulatedExecutionHandler(this.priceHandler);

    }

    private PriceHandler getPriceHandler() {
        if (this.sessionType == SessionType.BACK_TEST) {
            this.priceMemoryDao = new MemoryPriceDao(this.priceSqlDao);
            return new PriceHandler(this.priceMemoryDao);
        }
        return new PriceHandler(this.priceSqlDao);

    }

    private EventNotifier getEventNotifier() {
        EventNotifier eventNotifier = new EventNotifier();
        eventNotifier.addPropertyChangeListener(new FilledOrderListener(portfolio, historyHandler, eventQueue));
        eventNotifier.addPropertyChangeListener(new OrderCreatedListener(orderHandler));
        eventNotifier.addPropertyChangeListener(new OrderFoundListener(executionHandler, historyHandler, orderHandler, eventQueue, riskManagementHandler));
        eventNotifier.addPropertyChangeListener(new PortfolioChangedListener(reconciliationHandler));
        eventNotifier.addPropertyChangeListener(new SignalCreatedListener(riskManagementHandler, orderHandler, eventQueue, historyHandler));
        eventNotifier.addPropertyChangeListener(new StopOrderFilledListener(portfolio, historyHandler));
        eventNotifier.addPropertyChangeListener(new LoopFoundNext(executionHandler, portfolioHandler, signalHandler, orderDao, eventQueue));
        return eventNotifier;
    }


    protected void processStartDay(LocalDateTime currentTime) {
        if (Properties.sessionType == SessionType.BACK_TEST)
            this.priceMemoryDao.loadData(currentTime, currentTime.plusDays(1));
    }

    public void start() {
        try {
            this.runSession();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    protected void runSession() throws InterruptedException {
        printSessionStartMsg();
        this.executionHandler.closeAllPositions();
        this.executionHandler.cancelOpenLimitOrders();
        this.priceMemoryDao.loadData(startDate, startDate.plusDays(30));
        LocalDate lastDayProcessed = this.startDate.toLocalDate().minusDays(1);

        startEventProcessor();
        while (this.eventLoop.hasNext()) {
            LoopEvent loopEvent = this.eventLoop.next();
            LocalDateTime currentTime = loopEvent.getTimestamp();
            log.info(currentTime.toString());
            if (!TradingHelper.isTradingTime(currentTime)) {
                continue;
            }
            if (lastDayProcessed.compareTo(currentTime.toLocalDate()) < 0) {
                this.processStartDay(currentTime);
                lastDayProcessed = currentTime.toLocalDate();
            }
            eventQueue.put(loopEvent);
            this.eventLoop.sleep();
        }
    }

    private void printSessionStartMsg() {
        if (this.sessionType == SessionType.BACK_TEST) {
            System.out.println(String.format("Running Backtest from %s to %s", this.startDate, this.endDate));
        } else {
            System.out.println(String.format("Running Real-time Session until %s", this.endDate));
        }
    }

    private void startEventProcessor() {
        EventProcessor eventProcessor = new EventProcessor(
                eventQueue,
                historyHandler,
                portfolioHandler,
                eventNotifier
        );
        eventProcessor.start();
    }

    private boolean isEndOfDay(LocalDateTime currentTime) {
        return currentTime.getHour() > 22;
    }


}



