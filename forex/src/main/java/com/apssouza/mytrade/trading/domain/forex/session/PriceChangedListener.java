package com.apssouza.mytrade.trading.domain.forex.session;

import com.apssouza.mytrade.feed.api.SignalDto;
import com.apssouza.mytrade.trading.domain.forex.broker.BrokerService;
import com.apssouza.mytrade.trading.domain.forex.common.events.Event;
import com.apssouza.mytrade.trading.domain.forex.common.TradingParams;
import com.apssouza.mytrade.trading.domain.forex.common.observerinfra.EventNotifier;
import com.apssouza.mytrade.trading.domain.forex.common.observerinfra.Observer;
import com.apssouza.mytrade.trading.domain.forex.common.events.PriceChangedEvent;
import com.apssouza.mytrade.trading.domain.forex.common.events.SignalCreatedEvent;
import com.apssouza.mytrade.trading.domain.forex.feed.signalfeed.SignalFeedHandler;
import com.apssouza.mytrade.trading.domain.forex.order.OrderDto;
import com.apssouza.mytrade.trading.domain.forex.common.events.OrderFoundEvent;
import com.apssouza.mytrade.trading.domain.forex.order.OrderService;
import com.apssouza.mytrade.trading.domain.forex.portfolio.PortfolioService;

import java.time.LocalDateTime;
import java.util.List;

class PriceChangedListener implements Observer {

    private final BrokerService executionHandler;
    private final PortfolioService portfolioService;
    private final SignalFeedHandler signalFeedHandler;
    private final OrderService orderService;
    private final EventNotifier eventNotifier;

    public PriceChangedListener(
            BrokerService executionHandler,
            PortfolioService portfolioService,
            SignalFeedHandler signalFeedHandler,
            OrderService orderService,
            EventNotifier eventNotifier
    ) {
        this.executionHandler = executionHandler;
        this.portfolioService = portfolioService;
        this.signalFeedHandler = signalFeedHandler;
        this.orderService = orderService;
        this.eventNotifier = eventNotifier;
    }

    @Override
    public void update(final Event e) {
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
        portfolioService.getPortfolio().printPortfolio();
        portfolioService.createStopOrder(event);
        executionHandler.setCurrentTime(currentTime);
        executionHandler.setPriceMap(event.getPrice());
        portfolioService.updatePositionsPrices(event.getPrice());
        portfolioService.handleStopOrder(event);

        List<SignalDto> signals = processSignals(event, currentTime);
        portfolioService.processExits(event, signals);
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
        List<OrderDto> orders = this.orderService.getOrderByStatus(OrderDto.OrderStatus.CREATED);
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
