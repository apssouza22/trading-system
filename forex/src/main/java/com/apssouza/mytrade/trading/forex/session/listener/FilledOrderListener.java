package com.apssouza.mytrade.trading.forex.session.listener;

import com.apssouza.mytrade.trading.forex.order.OrderAction;
import com.apssouza.mytrade.trading.forex.portfolio.*;
import com.apssouza.mytrade.trading.forex.session.EventNotifier;
import com.apssouza.mytrade.trading.forex.statistics.HistoryBookHandler;
import com.apssouza.mytrade.trading.forex.session.event.*;
import com.apssouza.mytrade.trading.forex.statistics.TransactionState;
import com.apssouza.mytrade.trading.misc.helper.TradingParams;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class FilledOrderListener implements PropertyChangeListener {

    private final Portfolio portfolio;
    private final HistoryBookHandler historyHandler;
    private final EventNotifier eventNotifier;

    public FilledOrderListener(Portfolio portfolio, HistoryBookHandler historyHandler, EventNotifier eventNotifier) {
        this.portfolio = portfolio;
        this.historyHandler = historyHandler;
        this.eventNotifier = eventNotifier;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Event event = (Event) evt.getNewValue();
        if (!(event instanceof OrderFilledEvent)) {
            return;
        }

        OrderFilledEvent orderFilledEvent = (OrderFilledEvent) event;
        this.historyHandler.addOrderFilled(orderFilledEvent.getFilledOrder());

        FilledOrderDto filledOrder = orderFilledEvent.getFilledOrder();
        if (!this.portfolio.getPositions().containsKey(filledOrder.getIdentifier())) {
            Position newPosition = createNewPosition(filledOrder);
            emitEvent(orderFilledEvent, newPosition);
            return;
        }
        Position ps = this.portfolio.getPosition(filledOrder.getIdentifier());
        if (!TradingParams.trading_position_edit_enabled) {
            if (filledOrder.getQuantity() != ps.getQuantity()) {
                throw new RuntimeException("Not allowed units to be added/removed");
            }
        }
        Position position = handleExistingPosition(filledOrder, ps);
        this.historyHandler.addPosition(ps);
        emitEvent(orderFilledEvent, position);
    }

    private void emitEvent(OrderFilledEvent orderFilledEvent, Position position) {
            eventNotifier.notify(new PortfolioChangedEvent(
                    EventType.PORTFOLIO_CHANGED,
                    orderFilledEvent.getTimestamp(),
                    orderFilledEvent.getPrice(),
                    position
            ));
    }


    private Position handleExistingPosition(FilledOrderDto filledOrder, Position ps) {
        if (filledOrder.getAction().equals(OrderAction.SELL) && ps.getPositionType().equals(PositionType.LONG)) {
            handleOppositeDirection(filledOrder, ps);
            return ps;
        }
        if (filledOrder.getAction().equals(OrderAction.BUY) && ps.getPositionType().equals(PositionType.SHORT)) {
            handleOppositeDirection(filledOrder, ps);
            return ps;
        }
        handleSameDirection(filledOrder, ps);
        return ps;
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

    private Position createNewPosition(FilledOrderDto filledOrder) {
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

        portfolio.addNewPosition(ps1);
        historyHandler.setState(TransactionState.ENTRY, ps1.getIdentifier());
        historyHandler.addPosition(ps1);
        return ps1;
    }

}
