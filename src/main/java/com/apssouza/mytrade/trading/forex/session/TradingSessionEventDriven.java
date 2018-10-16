package com.apssouza.mytrade.trading.forex.session;

import com.apssouza.mytrade.trading.forex.session.event.EventProcessor;
import com.apssouza.mytrade.trading.misc.helper.TradingHelper;
import com.apssouza.mytrade.trading.misc.helper.time.DayHelper;
import com.apssouza.mytrade.trading.misc.loop.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class TradingSessionEventDriven extends AbstractTradingSession {

    private final BlockingQueue<LoopEvent> eventQueue;

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
        this.eventQueue = new LinkedBlockingDeque<>();
    }

    protected void runSession() {
        if (this.sessionType == SessionType.BACK_TEST) {
            System.out.println(String.format("Running Backtest from %s to %s", this.startDate, this.endDate));
        } else {
            System.out.println(String.format("Running Real-time Session until %s", this.endDate));
        }
        this.executionHandler.closeAllPositions();
        this.executionHandler.cancelOpenLimitOrders();
        LocalDate lastDayProcessed = this.startDate.toLocalDate().minusDays(1);
        this.priceMemoryDao.loadData(startDate, startDate.plusDays(30));

        EventProcessor eventProcessor = new EventProcessor(eventQueue, this);
        eventProcessor.start();
        while (this.eventLoop.hasNext()) {
            LoopEvent loopEvent = this.eventLoop.next();
            LocalDateTime currentTime = loopEvent.getTime();
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

            try {
                eventQueue.put(loopEvent);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.eventLoop.sleep();
            lastDayProcessed = currentTime.toLocalDate();
        }
    }

}


