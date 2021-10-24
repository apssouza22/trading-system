package com.apssouza.mytrade.trading.domain.forex.session;

import com.apssouza.mytrade.trading.api.ExecutionType;
import com.apssouza.mytrade.trading.api.SessionType;
import com.apssouza.mytrade.trading.domain.forex.common.Event;
import com.apssouza.mytrade.trading.domain.forex.common.TradingParams;
import com.apssouza.mytrade.trading.domain.forex.broker.BrokerService;
import com.apssouza.mytrade.trading.domain.forex.broker.OrderExecutionFactory;
import com.apssouza.mytrade.trading.domain.forex.feed.FeedService;
import com.apssouza.mytrade.trading.domain.forex.feed.pricefeed.PriceStream;
import com.apssouza.mytrade.trading.domain.forex.feed.pricefeed.PriceStreamFactory;
import com.apssouza.mytrade.trading.domain.forex.feed.signalfeed.SignalFeedFactory;
import com.apssouza.mytrade.trading.domain.forex.feed.signalfeed.SignalFeedHandler;
import com.apssouza.mytrade.trading.domain.forex.order.OrderService;
import com.apssouza.mytrade.trading.domain.forex.order.OrderHandlerFactory;
import com.apssouza.mytrade.trading.domain.forex.orderbook.BookHistoryService;
import com.apssouza.mytrade.trading.domain.forex.orderbook.BookHistoryHandlerFactory;
import com.apssouza.mytrade.trading.domain.forex.portfolio.PortfolioFactory;
import com.apssouza.mytrade.trading.domain.forex.portfolio.PortfolioService;
import com.apssouza.mytrade.trading.domain.forex.portfolio.PortfolioModel;
import com.apssouza.mytrade.trading.domain.forex.risk.RiskManagementFactory;
import com.apssouza.mytrade.trading.domain.forex.risk.RiskManagementService;
import com.apssouza.mytrade.trading.domain.forex.risk.stopordercreation.StopOrderConfigDto;
import com.apssouza.mytrade.trading.domain.forex.risk.stopordercreation.StopOrderFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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

    protected SignalFeedHandler signalFeedHandler;
    private final FeedService feedModule;
    protected BrokerService executionHandler;
    protected PortfolioModel portfolio;
    protected OrderService orderService;
    protected BookHistoryService historyHandler;
    protected PriceStream priceStream;
    protected PortfolioService portfolioService;
    protected boolean processedEndDay;
    protected RiskManagementService riskManagementService;
    protected final BlockingQueue<Event> eventQueue;
    protected EventNotifier eventNotifier;

    private static Logger log = Logger.getLogger(PortfolioService.class.getSimpleName());

    public TradingSession(
            BigDecimal equity,
            LocalDateTime startDate,
            LocalDateTime endDate,
            SessionType sessionType,
            String systemName,
            ExecutionType executionType,
            FeedService feedModule
    ) {
        this.equity = equity;
        this.startDate = startDate;
        this.endDate = endDate;
        this.sessionType = sessionType;
        this.systemName = systemName;
        this.executionType = executionType;
        this.eventQueue = new LinkedBlockingDeque<>();
        this.feedModule = feedModule;
        this.configSession();
    }

    private void configSession() {
        this.signalFeedHandler = SignalFeedFactory.create(feedModule);
        this.executionHandler = OrderExecutionFactory.factory(this.executionType);
        this.portfolio = new PortfolioModel(this.equity);
        this.historyHandler = BookHistoryHandlerFactory.create();
        this.riskManagementService = RiskManagementFactory.create(
                this.portfolio,
                StopOrderFactory.factory(new StopOrderConfigDto(
                        TradingParams.hard_stop_loss_distance,
                        TradingParams.take_profit_distance_fixed,
                        TradingParams.entry_stop_loss_distance_fixed,
                        TradingParams.trailing_stop_loss_distance
                ))
        );
        this.orderService = OrderHandlerFactory.create(this.riskManagementService);

        eventNotifier = new EventNotifier();
        this.portfolioService = PortfolioFactory.create(
                this.orderService,
                this.executionHandler,
                this.portfolio,
                this.riskManagementService,
                eventNotifier
        );
        this.eventNotifier = setListeners();

        this.priceStream = PriceStreamFactory.create(this.sessionType, eventQueue, this.feedModule);
    }

    private EventNotifier setListeners() {
        var eventListeners = OrderHandlerFactory.createListeners(portfolio, orderService, riskManagementService, executionHandler, eventNotifier);
        eventListeners.addAll(PortfolioFactory.createListeners(portfolioService, portfolio, eventNotifier));

        eventListeners.add(new PriceChangedListener(
                executionHandler,
                portfolioService,
                signalFeedHandler,
                orderService,
                eventNotifier
        ));
        eventListeners.addAll(BookHistoryHandlerFactory.createListeners(historyHandler));
        eventListeners.forEach(eventNotifier::addPropertyChangeListener);
        return eventNotifier;
    }


    public void start() {
        this.runSession();
    }

    public void shutdown() {
        log.warning("Shutting down the application");
        var current = LocalDateTime.now();
        var event = new EndedTradingDayEvent(
                current,
                priceStream.getPriceSymbolMapped(current)
        );
        eventNotifier.notify(event);
    }

    public List<CycleHistory> getHistory() {
        return this.historyHandler.getTransactions();
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
                eventNotifier,
                endDate
        );
        queueConsumer.start();
    }

}



