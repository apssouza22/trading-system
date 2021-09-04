package com.apssouza.mytrade.trading.domain.forex.risk.stoporder;

import com.apssouza.mytrade.trading.domain.forex.common.observer.PropertyChangeListener;
import com.apssouza.mytrade.trading.domain.forex.portfolio.PortfolioModel;
import com.apssouza.mytrade.trading.domain.forex.session.EventNotifier;
import com.apssouza.mytrade.trading.domain.forex.statistics.HistoryBookHandler;

import java.util.Collections;
import java.util.List;

public class StopOrderFactory {

    public static StopOrderCreator factory(
            final StopOrderConfigDto stopOrderDto
    ) {
        return new StopOrderCreatorFixed(stopOrderDto);
    }

    public static List<PropertyChangeListener> createListeners(
            final PortfolioModel portfolio, final HistoryBookHandler historyHandler,
            final EventNotifier eventNotifier
    ){
        return Collections.singletonList(new StopOrderFilledListener(portfolio, historyHandler, eventNotifier));
    }
}
