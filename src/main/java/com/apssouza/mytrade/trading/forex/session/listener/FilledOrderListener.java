package com.apssouza.mytrade.trading.forex.session.listener;

import com.apssouza.mytrade.trading.forex.order.OrderAction;
import com.apssouza.mytrade.trading.forex.portfolio.*;
import com.apssouza.mytrade.trading.forex.session.HistoryBookHandler;
import com.apssouza.mytrade.trading.forex.session.event.Event;
import com.apssouza.mytrade.trading.forex.session.event.OrderFilledEvent;
import com.apssouza.mytrade.trading.forex.statistics.TransactionState;
import com.apssouza.mytrade.trading.misc.helper.config.Properties;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class FilledOrderListener implements PropertyChangeListener {

    private final Portfolio portfolio;
    private final HistoryBookHandler historyHandler;

    public FilledOrderListener(Portfolio portfolio, HistoryBookHandler historyHandler) {
        this.portfolio = portfolio;
        this.historyHandler = historyHandler;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Event event = (Event) evt.getNewValue();
        if (!(event instanceof OrderFilledEvent)) {
            return;
        }
        OrderFilledEvent orderFilledEvent = (OrderFilledEvent) event;
        FilledOrderDto filledOrder = orderFilledEvent.getFilledOrder();
        if (!this.portfolio.getPositions().containsKey(filledOrder.getIdentifier())) {
            createNewPosition(filledOrder);
            return;
        }
        Position ps = this.portfolio.getPosition(filledOrder.getIdentifier());
        if (!Properties.trading_position_edit_enabled) {
            if (filledOrder.getQuantity() != ps.getQuantity()) {
                throw new RuntimeException("Not allowed units to be added/removed");
            }
        }
        handleExistingPosition(filledOrder, ps);

    }

    private void handleExistingPosition(FilledOrderDto filledOrder, Position ps) {
        if (filledOrder.getAction().equals(OrderAction.SELL) && ps.getPositionType().equals(PositionType.LONG)) {
            handleOppositeDirection(filledOrder, ps);
            this.historyHandler.addPosition(ps);
            return;
        }
        if (filledOrder.getAction().equals(OrderAction.BUY) && ps.getPositionType().equals(PositionType.SHORT)) {
            handleOppositeDirection(filledOrder, ps);
            this.historyHandler.addPosition(ps);
            return;
        }
        handleSameDirection(filledOrder, ps);
    }

    private void handleSameDirection(FilledOrderDto filledOrder, Position ps) {
        if (filledOrder.getAction().equals(OrderAction.BUY) && ps.getPositionType().equals(PositionType.LONG)) {
            this.portfolio.addPositionQtd(filledOrder.getIdentifier(), filledOrder.getQuantity(), filledOrder.getPriceWithSpread());
            this.historyHandler.setState(TransactionState.ADD_QTD, filledOrder.getIdentifier());

        }
        if (filledOrder.getAction().equals(OrderAction.SELL) && ps.getPositionType().equals(PositionType.SHORT)) {
            this.portfolio.addPositionQtd(filledOrder.getIdentifier(), filledOrder.getQuantity(), filledOrder.getPriceWithSpread());
            this.historyHandler.setState(TransactionState.ADD_QTD, filledOrder.getIdentifier());
        }
    }

    private void handleOppositeDirection(FilledOrderDto filledOrder, Position ps) {
        if (filledOrder.getQuantity() == ps.getQuantity()) {
            this.portfolio.closePosition(filledOrder.getIdentifier());
            this.historyHandler.setState(TransactionState.EXIT, filledOrder.getIdentifier());
        } else {
            this.portfolio.removePositionQtd(filledOrder.getIdentifier(), filledOrder.getQuantity());
            this.historyHandler.setState(TransactionState.REMOVE_QTD, filledOrder.getIdentifier());
        }
    }

    private void createNewPosition(FilledOrderDto filledOrder) {
        PositionType position_type = filledOrder.getAction().equals(OrderAction.BUY) ? PositionType.LONG : PositionType.SHORT;

        Position ps1 = new Position(
                position_type,
                filledOrder.getSymbol(),
                filledOrder.getQuantity(),
                filledOrder.getPriceWithSpread(),
                filledOrder.getTime(),
                filledOrder.getIdentifier(),
                filledOrder,
                null,
                PositionStatus.FILLED
        );
        this.portfolio.addNewPosition(ps1);
        this.historyHandler.setState(TransactionState.ENTRY, ps1.getIdentifier());
        this.historyHandler.addPosition(ps1);
    }

}
