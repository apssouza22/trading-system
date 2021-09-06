package com.apssouza.mytrade.trading.domain.forex.orderbook;

import com.apssouza.mytrade.trading.domain.forex.common.Event;
import com.apssouza.mytrade.trading.domain.forex.common.observer.PropertyChangeEvent;
import com.apssouza.mytrade.trading.domain.forex.common.observer.PropertyChangeListener;
import com.apssouza.mytrade.trading.domain.forex.order.OrderDto;
import com.apssouza.mytrade.trading.domain.forex.order.OrderFoundEvent;
import com.apssouza.mytrade.trading.domain.forex.risk.RiskManagementHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

class HistoryOrderFoundListener implements PropertyChangeListener {

    private static Logger log = Logger.getLogger(HistoryOrderFoundListener.class.getSimpleName());
    private final HistoryBookHandler historyHandler;

    private final RiskManagementHandler riskManagementHandler;

    public HistoryOrderFoundListener(
            HistoryBookHandler historyHandler,
            RiskManagementHandler riskManagementHandler
    ) {

        this.historyHandler = historyHandler;
        this.riskManagementHandler = riskManagementHandler;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        var event = (Event) evt.getNewValue();
        if (!(event instanceof OrderFoundEvent)) {
            return;
        }
        OrderFoundEvent orderFoundEvent = (OrderFoundEvent) event;

        List<OrderDto> orders = orderFoundEvent.getOrders();

        List<String> exitedPositions = new ArrayList<>();
        for (OrderDto order : orders) {
            if (order.getOrigin() == OrderDto.OrderOrigin.EXITS) {
                exitedPositions.add(order.getSymbol());
            }
        }

        for (OrderDto order : orders) {
            if (!riskManagementHandler.canExecuteOrder(event, order, new ArrayList<>(), exitedPositions)) {
                continue;
            }
            this.historyHandler.addOrder(order);
        }
    }


}
