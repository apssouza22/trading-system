package com.apssouza.mytrade.trading.forex.session;

import com.apssouza.mytrade.feed.signal.SignalDto;
import com.apssouza.mytrade.trading.forex.order.OrderDto;
import com.apssouza.mytrade.trading.forex.order.OrderStatus;
import com.apssouza.mytrade.trading.forex.session.event.*;
import com.apssouza.mytrade.trading.misc.helper.TradingHelper;
import com.apssouza.mytrade.trading.misc.helper.time.DayHelper;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class TradingSessionEventDriven extends AbstractTradingSession {

    public TradingSessionEventDriven(
            BigDecimal equity,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Connection connection,
            SessionType sessionType,
            String systemName,
            ExecutionType executionType
    ) {
        super(equity, startDate, endDate, connection, sessionType, systemName, executionType);
    }

    protected void runSession() throws InterruptedException {
        if (this.sessionType == SessionType.BACK_TEST) {
            System.out.println(String.format("Running Backtest from %s to %s", this.startDate, this.endDate));
        } else {
            System.out.println(String.format("Running Real-time Session until %s", this.endDate));
        }
        this.executionHandler.closeAllPositions();
        this.executionHandler.cancelOpenLimitOrders();
        LocalDate lastDayProcessed = this.startDate.toLocalDate().minusDays(1);
        this.priceMemoryDao.loadData(startDate, startDate.plusDays(30));

        EventProcessor eventProcessor = new EventProcessor(
                eventQueue,
                orderDao,
                executionHandler,
                portfolio,
                historyHandler,
                portfolioHandler,
                riskManagementHandler
        );
        eventProcessor.start();
        while (this.eventLoop.hasNext()) {
            LoopEvent loopEvent = this.eventLoop.next();
            LocalDateTime currentTime = loopEvent.getTimestamp();
            System.out.println(currentTime);

            if (DayHelper.isWeekend(currentTime.toLocalDate())) {
                continue;
            }
            if (!TradingHelper.isTradingTime(currentTime)) {
                continue;
            }
            if (lastDayProcessed.compareTo(currentTime.toLocalDate()) < 0) {
                this.processStartDay(currentTime);
            }

            List<SignalDto> signals;
            if (this.sessionType == SessionType.LIVE) {
                signals = this.signalHandler.getRealtimeSignal(this.systemName);
            } else {
                signals = this.signalHandler.findbySecondAndSource(this.systemName, loopEvent.getTimestamp());
            }
            this.historyHandler.addSignal(signals);
            this.executionHandler.setPriceMap(loopEvent.getPrice());
            this.portfolioHandler.updatePortfolioValue(loopEvent);
            this.portfolioHandler.stopOrderHandle(loopEvent);
            this.portfolioHandler.processExits(loopEvent, signals);

            eventQueue.put(loopEvent);

            for (SignalDto signal : signals) {
                eventQueue.put(new SignalCreatedEvent(
                        EventType.SIGNAL_CREATED, currentTime, loopEvent.getPrice(), signal
                ));
            }


            List<OrderDto> orders = this.orderDao.getOrderByStatus(OrderStatus.CREATED);


            this.eventLoop.sleep();
            lastDayProcessed = currentTime.toLocalDate();
        }
    }

}


