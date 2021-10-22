package com.apssouza.mytrade.trading.domain.forex.session;

import com.apssouza.mytrade.feed.api.SignalDto;
import com.apssouza.mytrade.trading.domain.forex.common.TradingParams;
import com.apssouza.mytrade.trading.domain.forex.common.observer.PropertyChangeEvent;
import com.apssouza.mytrade.trading.domain.forex.common.observer.PropertyChangeListener;
import com.apssouza.mytrade.trading.domain.forex.common.Event;
import com.apssouza.mytrade.trading.domain.forex.broker.BrokerHandler;
import com.apssouza.mytrade.trading.domain.forex.feed.pricefeed.PriceChangedEvent;
import com.apssouza.mytrade.trading.domain.forex.feed.signalfeed.SignalCreatedEvent;
import com.apssouza.mytrade.trading.domain.forex.feed.signalfeed.SignalFeedHandler;
import com.apssouza.mytrade.trading.domain.forex.order.OrderDto;
import com.apssouza.mytrade.trading.domain.forex.order.OrderFoundEvent;
import com.apssouza.mytrade.trading.domain.forex.order.OrderHandler;
import com.apssouza.mytrade.trading.domain.forex.portfolio.PortfolioHandler;

import java.time.LocalDateTime;
import java.util.List;

class PriceChangedListener implements PropertyChangeListener {

    private final BrokerHandler executionHandler;
    private final PortfolioHandler portfolioHandler;
    private final SignalFeedHandler signalFeedHandler;
    private final OrderHandler orderHandler;
    private final EventNotifier eventNotifier;

    public PriceChangedListener(
            BrokerHandler executionHandler,
            PortfolioHandler portfolioHandler,
            SignalFeedHandler signalFeedHandler,
            OrderHandler orderHandler,
            EventNotifier eventNotifier
    ) {
        this.executionHandler = executionHandler;
        this.portfolioHandler = portfolioHandler;
        this.signalFeedHandler = signalFeedHandler;
        this.orderHandler = orderHandler;
        this.eventNotifier = eventNotifier;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Event e = (Event) evt.getNewValue();
        if (!(e instanceof PriceChangedEvent event)) {
            return;
        }
        try {
            process(event);
        } catch (InterruptedException e1) {
            throw new RuntimeException(e1);
        }
    }

    private void process(PriceChangedEvent event) throws InterruptedException {
        LocalDateTime currentTime = event.getTimestamp();
        portfolioHandler.getPortfolio().printPortfolio();
        portfolioHandler.createStopOrder(event);
        executionHandler.setCurrentTime(currentTime);
        executionHandler.setPriceMap(event.getPrice());
        portfolioHandler.updatePositionsPrices(event);
        portfolioHandler.handleStopOrder(event);

        List<SignalDto> signals = processSignals(event, currentTime);
        portfolioHandler.processExits(event, signals);
        processOrders(event, currentTime);
    }

    private List<SignalDto> processSignals(PriceChangedEvent event, LocalDateTime currentTime) {
        var signals = this.signalFeedHandler.getSignal(TradingParams.systemName, event.getTimestamp());

        for (SignalDto signal : signals) {
            eventNotifier.notify(new SignalCreatedEvent(
                    currentTime,
                    event.getPrice(),
                    signal
            ));
        }
        return signals;
    }

    private void processOrders(PriceChangedEvent event, LocalDateTime currentTime) {
        List<OrderDto> orders = this.orderHandler.getOrderByStatus(OrderDto.OrderStatus.CREATED);
        List<OrderDto> orderList = MultiPositionHandler.createPositionIdentifier(orders);

        if (orderList.isEmpty()) {
            return;
        }

        eventNotifier.notify(new OrderFoundEvent(
                currentTime,
                event.getPrice(),
                orderList
        ));
    }
}
