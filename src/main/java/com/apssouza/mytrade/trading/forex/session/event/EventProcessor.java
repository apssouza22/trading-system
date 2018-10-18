package com.apssouza.mytrade.trading.forex.session.event;

import com.apssouza.mytrade.trading.forex.execution.ExecutionHandler;
import com.apssouza.mytrade.trading.forex.order.MemoryOrderDao;
import com.apssouza.mytrade.trading.forex.order.OrderDto;
import com.apssouza.mytrade.trading.forex.order.OrderStatus;
import com.apssouza.mytrade.trading.forex.portfolio.Portfolio;
import com.apssouza.mytrade.trading.forex.portfolio.PortfolioHandler;
import com.apssouza.mytrade.trading.forex.risk.RiskManagementHandler;
import com.apssouza.mytrade.trading.forex.session.HistoryBookHandler;
import com.apssouza.mytrade.trading.forex.session.MultiPositionHandler;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class EventProcessor extends Thread {
    private final BlockingQueue<Event> eventQueue;
    private final MemoryOrderDao orderDao;
    private final ExecutionHandler executionHandler;
    private final Portfolio portfolio;
    private final HistoryBookHandler historyHandler;
    private final PortfolioHandler portfolioHandler;
    private final RiskManagementHandler riskManagementHandler;


    public EventProcessor(
            BlockingQueue<Event> eventQueue,
            MemoryOrderDao orderDao,
            ExecutionHandler executionHandler,
            Portfolio portfolio,
            HistoryBookHandler historyHandler,
            PortfolioHandler portfolioHandler,
            RiskManagementHandler riskManagementHandler

    ) {
        super();
        this.eventQueue = eventQueue;
        this.orderDao = orderDao;
        this.executionHandler = executionHandler;
        this.portfolio = portfolio;
        this.historyHandler = historyHandler;
        this.portfolioHandler = portfolioHandler;
        this.riskManagementHandler = riskManagementHandler;
    }

    @Override
    public void run() {
        for (; ; ) {
            try {
                Event event = eventQueue.take();
                process(event);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void process(Event event) {

        switch (event.getType()) {
            case SIGNAL_CREATED:
                SignalCreatedEvent signalEvent = (SignalCreatedEvent) event;
                this.portfolioHandler.onSignal(signalEvent);
                break;

            case ORDER_CREATED:
                OrderCreatedEvent orderEvent = (OrderCreatedEvent) event;
                this.portfolioHandler.onOrderCreated(orderEvent);
                break;

            case ORDER_FOUND:
                OrderFoundEvent orderFoundEvent = (OrderFoundEvent) event;
                this.portfolioHandler.onOrderFound(orderFoundEvent);
        }


        this.portfolioHandler.processReconciliation();
        this.portfolioHandler.createStopOrder(event);
        this.historyHandler.process(event);

        System.out.println(this.portfolio.getPositions().size());
    }


}
