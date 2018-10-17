package com.apssouza.mytrade.trading.forex.session.event;

import com.apssouza.mytrade.feed.signal.SignalDto;
import com.apssouza.mytrade.trading.forex.execution.ExecutionHandler;
import com.apssouza.mytrade.trading.forex.order.MemoryOrderDao;
import com.apssouza.mytrade.trading.forex.order.OrderDto;
import com.apssouza.mytrade.trading.forex.order.OrderStatus;
import com.apssouza.mytrade.trading.forex.portfolio.Portfolio;
import com.apssouza.mytrade.trading.forex.portfolio.PortfolioHandler;
import com.apssouza.mytrade.trading.forex.risk.RiskManagementHandler;
import com.apssouza.mytrade.trading.forex.session.HistoryBookHandler;
import com.apssouza.mytrade.trading.forex.session.MultiPositionHandler;
import com.apssouza.mytrade.trading.misc.loop.LoopEvent;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class EventProcessor extends Thread {
    private final BlockingQueue<LoopEvent> eventQueue;
    private final MemoryOrderDao orderDao;
    private final ExecutionHandler executionHandler;
    private final Portfolio portfolio;
    private final HistoryBookHandler historyHandler;
    private final PortfolioHandler portfolioHandler;
    private final RiskManagementHandler riskManagementHandler;


    public EventProcessor(
            BlockingQueue<LoopEvent> eventQueue,
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
                LoopEvent loopEvent = eventQueue.take();
                process(loopEvent);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void process(LoopEvent loopEvent) {
        this.executionHandler.setPriceMap(loopEvent.getPrice());
        List<SignalDto> signals = loopEvent.getSignals();
        if (!signals.isEmpty()) {
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
