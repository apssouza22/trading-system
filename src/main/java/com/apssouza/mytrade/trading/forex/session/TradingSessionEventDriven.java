package com.apssouza.mytrade.trading.forex.session;

import com.apssouza.mytrade.feed.price.PriceDto;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
                historyHandler,
                portfolioHandler,
                eventNotifier
        );
        eventProcessor.start();
        Map<String, PriceDto> lastPrice = Collections.emptyMap();
        while (this.eventLoop.hasNext()) {
            LoopEvent loopEvent = this.eventLoop.next();
            LocalDateTime currentTime = loopEvent.getTimestamp();
            System.out.println(currentTime);
            if (!TradingHelper.isTradingTime(currentTime)){
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

}


