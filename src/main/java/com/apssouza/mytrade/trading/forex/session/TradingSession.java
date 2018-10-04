package com.apssouza.mytrade.trading.forex.session;

import com.apssouza.mytrade.feed.price.MemoryPriceDao;
import com.apssouza.mytrade.feed.price.PriceDao;
import com.apssouza.mytrade.feed.price.PriceHandler;
import com.apssouza.mytrade.feed.price.SqlPriceDao;
import com.apssouza.mytrade.feed.signal.SignalDao;
import com.apssouza.mytrade.feed.signal.SignalDto;
import com.apssouza.mytrade.feed.signal.SignalHandler;
import com.apssouza.mytrade.feed.signal.SqlSignalDao;
import com.apssouza.mytrade.trading.forex.execution.ExecutionHandler;
import com.apssouza.mytrade.trading.forex.execution.InteractiveBrokerExecutionHandler;
import com.apssouza.mytrade.trading.forex.execution.SimulatedExecutionHandler;
import com.apssouza.mytrade.trading.forex.order.MemoryOrderDao;
import com.apssouza.mytrade.trading.forex.order.OrderDto;
import com.apssouza.mytrade.trading.forex.order.OrderHandler;
import com.apssouza.mytrade.trading.forex.order.OrderStatus;
import com.apssouza.mytrade.trading.forex.portfolio.Portfolio;
import com.apssouza.mytrade.trading.forex.portfolio.PortfolioHandler;
import com.apssouza.mytrade.trading.forex.portfolio.ReconciliationHandler;
import com.apssouza.mytrade.trading.forex.portfolio.StopOrderHandler;
import com.apssouza.mytrade.trading.forex.risk.PositionExitHandler;
import com.apssouza.mytrade.trading.forex.risk.PositionSizer;
import com.apssouza.mytrade.trading.forex.risk.PositionSizerFixed;
import com.apssouza.mytrade.trading.forex.risk.RiskManagementHandler;
import com.apssouza.mytrade.trading.misc.helper.TradingHelper;
import com.apssouza.mytrade.trading.misc.helper.config.Properties;
import com.apssouza.mytrade.trading.misc.helper.time.DateRangeHelper;
import com.apssouza.mytrade.trading.misc.helper.time.DayHelper;
import com.apssouza.mytrade.trading.misc.loop.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
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
    private EventLoop eventLoop;
    private PortfolioHandler portfolioHandler;
    private boolean processedEndDay;
    private RiskManagementHandler riskManagementHandler;

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
        this.riskManagementHandler = new RiskManagementHandler(this.portfolio,new PositionSizerFixed());

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
            this.eventLoop = new RealEventLoop(
                    LocalDateTime.now(),
                    this.endDate,
                    Duration.ofSeconds(1),
                    new CurrentTimeCreator(),
                    this.priceHandler
            );
        } else {
            List<LocalDateTime> range = DateRangeHelper.getSecondsBetween(this.startDate, this.endDate);
            this.eventLoop = new RangeEventLoop(range, this.priceHandler);
        }
    }

    private void runSession() {
        if (this.sessionType == SessionType.BACK_TEST) {
            System.out.println(String.format("Running Backtest from %s to %s", this.startDate, this.endDate));
        } else {
            System.out.println(String.format("Running Real-time Session until %s", this.endDate));
        }
        this.executionHandler.closeAllPositions();
        this.executionHandler.cancelOpenLimitOrders();
        LocalDate lastDayProcessed = this.startDate.toLocalDate().minusDays(1);
        this.priceMemoryDao.loadData(startDate, startDate.plusDays(30));
        while (this.eventLoop.hasNext()) {
            LoopEvent loopEvent = this.eventLoop.next();
            LocalDateTime currentTime = loopEvent.getTime();
            System.out.println(currentTime);

            if (DayHelper.isWeekend(currentTime.toLocalDate())) {
                continue;
            }
            if (!TradingHelper.isTradingTime(currentTime)) {
                return;
            }
            if (lastDayProcessed.compareTo(currentTime.toLocalDate()) < 0 ) {
                this.processStartDay(currentTime);
            }

            this.processNext(loopEvent);
            this.eventLoop.sleep();
            lastDayProcessed = currentTime.toLocalDate();
        }
    }

    private void processNext(LoopEvent loopEvent) {

        if (this.sessionType == SessionType.BACK_TEST) {
            this.executionHandler.setCurrentTime(loopEvent.getTime());
        }
        this.executionHandler.setPriceMap(loopEvent.getPrice());
        List<SignalDto> signals;
        if (this.sessionType == SessionType.LIVE) {
            signals = this.signalHandler.getRealtimeSignal(this.systemName);
        } else {
            signals = this.signalHandler.findbySecondAndSource(this.systemName, loopEvent.getTime());
        }
        if (!signals.isEmpty()){
            System.out.println("signal");
        }
        this.portfolioHandler.updatePortfolioValue(loopEvent);

        this.portfolioHandler.stopOrderHandle(loopEvent);
        this.portfolioHandler.processExits(loopEvent, signals);
        this.portfolioHandler.onSignal(loopEvent, signals);

        List<OrderDto> orders = this.orderDao.getOrderByStatus(OrderStatus.CREATED);
        orders = this.createPositionIdentifier(orders);
        this.riskManagementHandler.checkOrders(orders);
        this.historyHandler.addSignal(signals, orders);

        this.portfolioHandler.onOrder(orders);
        this.portfolioHandler.processReconciliation();
        this.portfolioHandler.createStopOrder(loopEvent);
        this.historyHandler.process(loopEvent);

        System.out.println(this.portfolio.getPositions().size());
    }

    private void processStartDay(LocalDateTime currentTime) {
        if (Properties.sessionType == SessionType.BACK_TEST)
            this.priceMemoryDao.loadData(currentTime, currentTime.plusDays(1));
    }

    public void start() {
        this.runSession();
    }

    private boolean isEndOfDay(LocalDateTime currentTime) {
        return currentTime.getHour() > 22;
    }

    private List<OrderDto> createPositionIdentifier(List<OrderDto> orders) {
        List<OrderDto> list = new LinkedList<>();
        for (OrderDto order : orders) {
            list.add(new OrderDto(
                    MultiPositionHandler.getIdentifierFromOrder(order),
                    order
            ));
        }
        return list;
    }
}


