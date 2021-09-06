package com.apssouza.mytrade.trading.domain.forex.risk.stoporder;

import com.apssouza.mytrade.trading.domain.forex.common.Event;
import com.apssouza.mytrade.trading.domain.forex.common.observer.PropertyChangeEvent;
import com.apssouza.mytrade.trading.domain.forex.common.observer.PropertyChangeListener;
import com.apssouza.mytrade.trading.domain.forex.portfolio.PortfolioChangedEvent;
import com.apssouza.mytrade.trading.domain.forex.portfolio.PortfolioModel;
import com.apssouza.mytrade.trading.domain.forex.portfolio.Position;
import com.apssouza.mytrade.trading.domain.forex.session.EventNotifier;
import com.apssouza.mytrade.trading.domain.forex.session.MultiPositionHandler;
import static com.apssouza.mytrade.trading.domain.forex.portfolio.Position.ExitReason;

class StopOrderFilledListener implements PropertyChangeListener {

    private final PortfolioModel portfolio;
    private final EventNotifier eventNotifier;

    public StopOrderFilledListener(PortfolioModel portfolio, EventNotifier eventNotifier) {
        this.portfolio = portfolio;
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
        Position ps = MultiPositionHandler.getPositionByStopOrder(stopOrder);
        ps = ps.closePosition(ExitReason.STOP_ORDER_FILLED);
        this.portfolio.closePosition(ps.getIdentifier());

        eventNotifier.notify(new PortfolioChangedEvent(
                event.getTimestamp(),
                event.getPrice(),
                ps
        ));
    }
}
