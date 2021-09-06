package com.apssouza.mytrade.trading.domain.forex.risk.stoporder;

import com.apssouza.mytrade.trading.domain.forex.common.observer.PropertyChangeListener;
import com.apssouza.mytrade.trading.domain.forex.portfolio.PortfolioModel;
import com.apssouza.mytrade.trading.domain.forex.session.EventNotifier;

import java.util.Collections;
import java.util.List;

public class StopOrderFactory {

    public static StopOrderCreator factory(
            StopOrderConfigDto stopOrderDto
    ) {
        return new StopOrderCreatorFixed(stopOrderDto);
    }

    public static List<PropertyChangeListener> createListeners(PortfolioModel portfolio, EventNotifier eventNotifier) {
        return Collections.singletonList(new StopOrderFilledListener(portfolio, eventNotifier));
    }
}
