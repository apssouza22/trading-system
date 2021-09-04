package com.apssouza.mytrade.trading.domain.forex.session;

import com.apssouza.mytrade.trading.api.ExecutionType;
import com.apssouza.mytrade.trading.api.SessionType;
import com.apssouza.mytrade.trading.domain.forex.common.TradingParams;
import com.apssouza.mytrade.trading.domain.forex.execution.OrderExecution;
import com.apssouza.mytrade.trading.domain.forex.execution.OrderExecutionFactory;
import com.apssouza.mytrade.trading.domain.forex.feed.pricefeed.PriceFeedHandler;
import com.apssouza.mytrade.trading.domain.forex.feed.pricefeed.PriceStream;
import com.apssouza.mytrade.trading.domain.forex.feed.pricefeed.PriceStreamFactory;
import com.apssouza.mytrade.trading.domain.forex.feed.signalfeed.SignalFeedHandler;
import com.apssouza.mytrade.trading.domain.forex.order.OrderHandler;
import com.apssouza.mytrade.trading.domain.forex.order.OrderHandlerFactory;
import com.apssouza.mytrade.trading.domain.forex.portfolio.PortfolioFactory;
import com.apssouza.mytrade.trading.domain.forex.portfolio.PortfolioHandler;
import com.apssouza.mytrade.trading.domain.forex.portfolio.PortfolioModel;
import com.apssouza.mytrade.trading.domain.forex.risk.RiskManagementFactory;
import com.apssouza.mytrade.trading.domain.forex.risk.RiskManagementHandler;
import com.apssouza.mytrade.trading.domain.forex.risk.stoporder.StopOrderConfigDto;
import com.apssouza.mytrade.trading.domain.forex.risk.stoporder.StopOrderFactory;
import com.apssouza.mytrade.trading.domain.forex.event.Event;
import com.apssouza.mytrade.trading.domain.forex.event.EventType;
import com.apssouza.mytrade.trading.domain.forex.session.listener.EndedTradingDayListener;
import com.apssouza.mytrade.trading.domain.forex.session.listener.FilledOrderListener;
import com.apssouza.mytrade.trading.domain.forex.session.listener.OrderCreatedListener;
import com.apssouza.mytrade.trading.domain.forex.session.listener.OrderFoundListener;
import com.apssouza.mytrade.trading.domain.forex.session.listener.PortfolioChangedListener;
import com.apssouza.mytrade.trading.domain.forex.session.listener.PriceChangedListener;
import com.apssouza.mytrade.trading.domain.forex.session.listener.SessionFinishedListener;
import com.apssouza.mytrade.trading.domain.forex.session.listener.SignalCreatedListener;
import com.apssouza.mytrade.trading.domain.forex.session.listener.StopOrderFilledListener;
import com.apssouza.mytrade.trading.domain.forex.statistics.HistoryBookHandler;
import com.apssouza.mytrade.trading.domain.forex.statistics.HistoryBookHandlerFactory;

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

    protected final PriceFeedHandler priceFeedHandler;
    protected final SignalFeedHandler signalFeedHandler;
    protected OrderExecution executionHandler;
    protected PortfolioModel portfolio;
    protected OrderHandler orderHandler;
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
            SignalFeedHandler signalFeedHandler,
            SessionType sessionType,
            String systemName,
            ExecutionType executionType,
            PriceFeedHandler priceFeedHandler
    ) {
        this.equity = equity;
        this.startDate = startDate;
        this.endDate = endDate;
        this.sessionType = sessionType;
        this.systemName = systemName;
        this.executionType = executionType;
        this.eventQueue = new LinkedBlockingDeque<>();
        this.priceFeedHandler = priceFeedHandler;
        this.signalFeedHandler = signalFeedHandler;
        this.configSession();
    }

    private void configSession() {
        this.executionHandler = OrderExecutionFactory.factory(this.executionType);
        this.portfolio = new PortfolioModel(this.equity);
        this.historyHandler = HistoryBookHandlerFactory.create();
        this.riskManagementHandler = RiskManagementFactory.create(
                this.portfolio,
                StopOrderFactory.factory(new StopOrderConfigDto(
                        TradingParams.hard_stop_loss_distance,
                        TradingParams.take_profit_distance_fixed,
                        TradingParams.entry_stop_loss_distance_fixed,
                        TradingParams.trailing_stop_loss_distance
                ))
        );
        this.orderHandler = OrderHandlerFactory.create(this.riskManagementHandler);

        eventNotifier = new EventNotifier();
        this.portfolioHandler = PortfolioFactory.factory(
                this.orderHandler,
                this.executionHandler,
                this.portfolio,
                this.riskManagementHandler,
                eventNotifier
        );
        this.eventNotifier = setListeners();

        this.priceStream = PriceStreamFactory.factory(this.sessionType, eventQueue, this.priceFeedHandler);
    }

    private EventNotifier setListeners() {
        eventNotifier.addPropertyChangeListener(new FilledOrderListener(portfolio, historyHandler, eventNotifier));
        eventNotifier.addPropertyChangeListener(new OrderCreatedListener(orderHandler));
        eventNotifier.addPropertyChangeListener(new OrderFoundListener(
                executionHandler,
                historyHandler,
                orderHandler,
                eventNotifier,
                riskManagementHandler
        ));
        eventNotifier.addPropertyChangeListener(new PortfolioChangedListener(portfolioHandler));
        eventNotifier.addPropertyChangeListener(new SignalCreatedListener(
                riskManagementHandler,
                orderHandler,
                eventNotifier,
                historyHandler
        ));
        eventNotifier.addPropertyChangeListener(new StopOrderFilledListener(portfolio, historyHandler, eventNotifier));
        eventNotifier.addPropertyChangeListener(new PriceChangedListener(
                executionHandler,
                portfolioHandler,
                signalFeedHandler,
                orderHandler,
                eventNotifier
        ));
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
                priceFeedHandler.getPriceSymbolMapped(current)
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



