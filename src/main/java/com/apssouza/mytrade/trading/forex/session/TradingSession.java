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
import com.apssouza.mytrade.trading.forex.portfolio.StopOrderHandler;
import com.apssouza.mytrade.trading.forex.risk.PositionExitHandler;
import com.apssouza.mytrade.trading.forex.risk.PositionSizer;
import com.apssouza.mytrade.trading.forex.risk.PositionSizerFixed;
import com.apssouza.mytrade.trading.misc.helper.config.Properties;
import com.apssouza.mytrade.trading.misc.helper.time.DateRangeHelper;
import com.apssouza.mytrade.trading.misc.loop.CurrentTimeCreator;
import com.apssouza.mytrade.trading.misc.loop.RangeTimeEventLoop;
import com.apssouza.mytrade.trading.misc.loop.RealTimeEventLoop;
import com.apssouza.mytrade.trading.misc.loop.TimeEventLoop;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class TradingSession {

    private final BigDecimal equity;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final Connection connection;
    private final SessionType sessionType;
    private final String systemName;
    private final ExecutionType executionType;

    private MemoryOrderDao orderDao;
    private PriceDao priceSqlDao;
    private SignalDao signalDao;
    private MemoryPriceDao priceMemoryDao;
    private PriceHandler priceHandler;
    private ExecutionHandler executionHandler;
    private PositionSizer positionSizer;
    private Portfolio portfolio;
    private PositionExitHandler positionExitHandler;
    private OrderHandler orderHandler;
    private StopOrderHandler stopOrderHandler;
    private SignalHandler signalHandler;
    private ReconciliationHandler reconciliationHandler;
    private HistoryBookHandler historyHandler;
    private TimeEventLoop eventLoop;
    private PortfolioHandler portfolioHandler;

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
        this.configSession();
    }

    private void configSession() {
        this.orderDao = new MemoryOrderDao();
        this.priceSqlDao = new SqlPriceDao(this.connection);
        this.signalDao = new SqlSignalDao(this.connection);

        if (this.sessionType == SessionType.BACK_TEST) {
            this.priceMemoryDao = new MemoryPriceDao(this.priceSqlDao);
            this.priceHandler = new PriceHandler(this.priceMemoryDao);

        } else {
            this.priceHandler = new PriceHandler(this.priceSqlDao);
        }

        if (this.executionType == ExecutionType.BROKER) {
            this.executionHandler = new InteractiveBrokerExecutionHandler(
                    Properties.brokerHost,
                    Properties.brokerPort,
                    Properties.brokerClientId
            );
        } else {
            this.executionHandler = new SimulatedExecutionHandler(this.priceHandler);
        }

        this.positionSizer = new PositionSizerFixed();
        this.portfolio = new Portfolio(this.priceHandler, this.equity);
        this.positionExitHandler = new PositionExitHandler(this.portfolio, this.priceHandler);
        this.orderHandler = new OrderHandler(this.orderDao, this.positionSizer, this.equity, this.priceHandler, this.portfolio);
        this.stopOrderHandler = new StopOrderHandler(this.portfolio, this.priceHandler);
        this.signalHandler = new SignalHandler(this.signalDao);
        this.reconciliationHandler = new ReconciliationHandler(this.portfolio, this.executionHandler);
        this.historyHandler = new HistoryBookHandler(this.portfolio, this.priceHandler);

        this.portfolioHandler = new PortfolioHandler(
                this.equity,
                this.priceHandler,
                this.orderHandler,
                this.positionSizer,
                this.positionExitHandler,
                this.executionHandler,
                this.stopOrderHandler,
                this.portfolio,
                this.reconciliationHandler,
                this.historyHandler
        );

        if (this.sessionType == SessionType.LIVE) {
            this.eventLoop = new RealTimeEventLoop(
                    LocalDateTime.now(),
                    this.endDate,
                    Duration.ofHours(1),
                    new CurrentTimeCreator()
            );
        } else {
            List<LocalDateTime> range = DateRangeHelper.getSecondsBetween(this.startDate, this.endDate);
            this.eventLoop = new RangeTimeEventLoop(range);

        }
    }

    private void deleteLogFiles() {

    }

}
