package com.apssouza.mytrade.trading.forex.session.listener;

import com.apssouza.mytrade.feed.signal.SignalDto;
import com.apssouza.mytrade.feed.signal.SignalHandler;
import com.apssouza.mytrade.trading.forex.execution.ExecutionHandler;
import com.apssouza.mytrade.trading.forex.order.OrderDao;
import com.apssouza.mytrade.trading.forex.order.OrderDto;
import com.apssouza.mytrade.trading.forex.order.OrderStatus;
import com.apssouza.mytrade.trading.forex.portfolio.PortfolioHandler;
import com.apssouza.mytrade.trading.forex.session.MultiPositionHandler;
import com.apssouza.mytrade.trading.forex.session.SessionType;
import com.apssouza.mytrade.trading.forex.session.event.*;
import com.apssouza.mytrade.trading.misc.helper.config.Properties;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class PriceChangedListener implements PropertyChangeListener {

    private final ExecutionHandler executionHandler;
    private final PortfolioHandler portfolioHandler;
    private final SignalHandler signalHandler;
    private final OrderDao orderDao;
    private final BlockingQueue<Event> eventQueue;

    public PriceChangedListener(
            ExecutionHandler executionHandler,
            PortfolioHandler portfolioHandler,
            SignalHandler signalHandler,
            OrderDao orderDao,
            BlockingQueue<Event> eventQueue
    ) {
        this.executionHandler = executionHandler;
        this.portfolioHandler = portfolioHandler;
        this.signalHandler = signalHandler;
        this.orderDao = orderDao;
        this.eventQueue = eventQueue;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Event e = (Event) evt.getNewValue();
        if (!(e instanceof PriceChangedEvent)) {
            return;
        }
        PriceChangedEvent event = (PriceChangedEvent) e;

        try {
            process(event);
        } catch (InterruptedException e1) {
            throw new RuntimeException(e1);
        }
    }

    private void process(PriceChangedEvent event) throws InterruptedException {
        LocalDateTime currentTime = event.getTimestamp();
        this.executionHandler.setCurrentTime(currentTime);
        this.executionHandler.setPriceMap(event.getPrice());
        this.portfolioHandler.updatePortfolioValue(event);
        this.portfolioHandler.stopOrderHandle(event);

        List<SignalDto> signals;
        if (Properties.sessionType == SessionType.LIVE) {
            signals = this.signalHandler.getRealtimeSignal(Properties.systemName);
        } else {
            signals = this.signalHandler.findbySecondAndSource(Properties.systemName, event.getTimestamp());
        }

        for (SignalDto signal : signals) {
            eventQueue.put(new SignalCreatedEvent(
                    EventType.SIGNAL_CREATED,
                    currentTime,
                    event.getPrice(),
                    signal
            ));
        }
        this.portfolioHandler.processExits(event, signals);

        List<OrderDto> orders = this.orderDao.getOrderByStatus(OrderStatus.CREATED);
        List<OrderDto> orderList = MultiPositionHandler.createPositionIdentifier(orders);
        eventQueue.put(new OrderFoundEvent(
                EventType.ORDER_FOUND,
                currentTime,
                event.getPrice(),
                orderList
        ));
    }
}
