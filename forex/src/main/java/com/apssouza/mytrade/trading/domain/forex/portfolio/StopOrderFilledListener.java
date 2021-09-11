package com.apssouza.mytrade.trading.domain.forex.portfolio;

import com.apssouza.mytrade.trading.domain.forex.common.Event;
import com.apssouza.mytrade.trading.domain.forex.common.observer.PropertyChangeEvent;
import com.apssouza.mytrade.trading.domain.forex.common.observer.PropertyChangeListener;
import com.apssouza.mytrade.trading.domain.forex.risk.stoporder.StopOrderDto;
import com.apssouza.mytrade.trading.domain.forex.risk.stoporder.StopOrderFilledEvent;
import com.apssouza.mytrade.trading.domain.forex.session.EventNotifier;
import com.apssouza.mytrade.trading.domain.forex.session.MultiPositionHandler;
import static com.apssouza.mytrade.trading.domain.forex.portfolio.Position.ExitReason;

class StopOrderFilledListener implements PropertyChangeListener {

    private final PortfolioModel portfolio;
    private final PortfolioHandler portfolioHandler;
    private final EventNotifier eventNotifier;

    public StopOrderFilledListener(
            PortfolioModel portfolio,
            PortfolioHandler portfolioHandler,
            EventNotifier eventNotifier
    ) {
        this.portfolio = portfolio;
        this.portfolioHandler = portfolioHandler;
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

        portfolio.closePosition(ps.getIdentifier());
        portfolioHandler.processReconciliation(event);
        eventNotifier.notify(new PortfolioChangedEvent(
                event.getTimestamp(),
                event.getPrice(),
                ps
        ));
    }
}
