package com.apssouza.mytrade.trading.domain.forex.feed.signalfeed;

import com.apssouza.mytrade.feed.api.FeedModule;
import com.apssouza.mytrade.trading.domain.forex.common.observer.PropertyChangeListener;
import com.apssouza.mytrade.trading.domain.forex.feed.FeedService;
import com.apssouza.mytrade.trading.domain.forex.order.OrderHandler;
import com.apssouza.mytrade.trading.domain.forex.risk.RiskManagementHandler;
import com.apssouza.mytrade.trading.domain.forex.session.EventNotifier;

import java.util.Collections;
import java.util.List;

public class SignalFeedFactory {

    public static SignalFeedHandler create(FeedService feed) {
        return new SignalFeedHandler(feed);
    }

    public static List<PropertyChangeListener> createListeners(
            final RiskManagementHandler riskManagementHandler,
            final OrderHandler orderHandler,
            final EventNotifier eventNotifier
    ) {
        return Collections.singletonList(new SignalCreatedListener(
                riskManagementHandler,
                orderHandler,
                eventNotifier
        ));
    }
}
