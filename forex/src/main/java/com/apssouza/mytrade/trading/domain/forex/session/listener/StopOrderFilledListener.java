package com.apssouza.mytrade.trading.domain.forex.session.listener;

import com.apssouza.mytrade.trading.domain.forex.order.StopOrderDto;
import com.apssouza.mytrade.trading.domain.forex.portfolio.FilledOrderDto;
import com.apssouza.mytrade.trading.domain.forex.portfolio.PortfolioModel;
import com.apssouza.mytrade.trading.domain.forex.portfolio.Position;
import com.apssouza.mytrade.trading.domain.forex.session.EventNotifier;
import com.apssouza.mytrade.trading.domain.forex.session.MultiPositionHandler;
import com.apssouza.mytrade.trading.domain.forex.session.TransactionDto;
import com.apssouza.mytrade.trading.domain.forex.event.Event;
import com.apssouza.mytrade.trading.domain.forex.event.EventType;
import com.apssouza.mytrade.trading.domain.forex.portfolio.PortfolioChangedEvent;
import com.apssouza.mytrade.trading.domain.forex.order.StopOrderFilledEvent;
import com.apssouza.mytrade.trading.domain.forex.statistics.HistoryBookHandler;
import com.apssouza.mytrade.trading.domain.forex.common.observer.PropertyChangeEvent;
import com.apssouza.mytrade.trading.domain.forex.common.observer.PropertyChangeListener;

import java.time.LocalDateTime;

public class StopOrderFilledListener implements PropertyChangeListener {

    private final PortfolioModel portfolio;
    private final HistoryBookHandler historyHandler;
    private final EventNotifier eventNotifier;

    public StopOrderFilledListener(PortfolioModel portfolio, HistoryBookHandler historyHandler, EventNotifier eventNotifier) {
        this.portfolio = portfolio;
        this.historyHandler = historyHandler;
        this.eventNotifier = eventNotifier;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Event event = (Event) evt.getNewValue();
        if (!(event instanceof StopOrderFilledEvent)) {
            return;
        }
        StopOrderFilledEvent orderFilledEvent = (StopOrderFilledEvent) event;
        StopOrderDto stopOrder = orderFilledEvent.getStopOrder();
        LocalDateTime time = orderFilledEvent.getTimestamp();
        Position ps = MultiPositionHandler.getPositionByStopOrder(stopOrder);
        ps = ps.closePosition(Position.ExitReason.STOP_ORDER_FILLED);
        this.portfolio.closePosition(ps.getIdentifier());
        this.historyHandler.setState(TransactionDto.TransactionState.EXIT, ps.getIdentifier());
        this.historyHandler.addPosition(ps);

        this.historyHandler.addOrderFilled(new FilledOrderDto(
                time,
                stopOrder.getSymbol(),
                stopOrder.getAction(),
                stopOrder.getQuantity(),
                stopOrder.getFilledPrice(),
                ps.getIdentifier(),
                stopOrder.getId()
        ));

        eventNotifier.notify(new PortfolioChangedEvent(
                EventType.PORTFOLIO_CHANGED,
                event.getTimestamp(),
                event.getPrice(),
                ps
        ));
    }
}
