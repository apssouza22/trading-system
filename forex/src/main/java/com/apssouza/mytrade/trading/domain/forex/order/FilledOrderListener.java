package com.apssouza.mytrade.trading.domain.forex.order;

import com.apssouza.mytrade.trading.domain.forex.common.TradingParams;
import com.apssouza.mytrade.trading.domain.forex.common.observer.PropertyChangeEvent;
import com.apssouza.mytrade.trading.domain.forex.common.observer.PropertyChangeListener;
import com.apssouza.mytrade.trading.domain.forex.event.Event;
import com.apssouza.mytrade.trading.domain.forex.event.EventType;
import com.apssouza.mytrade.trading.domain.forex.portfolio.FilledOrderDto;
import com.apssouza.mytrade.trading.domain.forex.portfolio.PortfolioChangedEvent;
import com.apssouza.mytrade.trading.domain.forex.portfolio.PortfolioModel;
import com.apssouza.mytrade.trading.domain.forex.portfolio.Position;
import com.apssouza.mytrade.trading.domain.forex.session.EventNotifier;
import com.apssouza.mytrade.trading.domain.forex.session.TransactionDto;
import com.apssouza.mytrade.trading.domain.forex.statistics.HistoryBookHandler;
import static com.apssouza.mytrade.trading.domain.forex.portfolio.Position.PositionStatus.FILLED;


class FilledOrderListener implements PropertyChangeListener {

    private final PortfolioModel portfolio;
    private final HistoryBookHandler historyHandler;
    private final EventNotifier eventNotifier;

    public FilledOrderListener(PortfolioModel portfolio, HistoryBookHandler historyHandler, EventNotifier eventNotifier) {
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
        if (filledOrder.getAction().equals(OrderDto.OrderAction.SELL) && ps.getPositionType().equals(Position.PositionType.LONG)) {
            handleOppositeDirection(filledOrder, ps);
            return ps;
        }
        if (filledOrder.getAction().equals(OrderDto.OrderAction.BUY) && ps.getPositionType().equals(Position.PositionType.SHORT)) {
            handleOppositeDirection(filledOrder, ps);
            return ps;
        }
        handleSameDirection(filledOrder, ps);
        return ps;
    }

    private void handleSameDirection(FilledOrderDto filledOrder, Position ps) {
        if (filledOrder.getAction().equals(OrderDto.OrderAction.BUY) && ps.getPositionType().equals(Position.PositionType.LONG)) {
            this.portfolio.addPositionQtd(filledOrder.getIdentifier(), filledOrder.getQuantity(), filledOrder.getPriceWithSpread());
            this.historyHandler.setState(TransactionDto.TransactionState.ADD_QTD, filledOrder.getIdentifier());

        }
        if (filledOrder.getAction().equals(OrderDto.OrderAction.SELL) && ps.getPositionType().equals(Position.PositionType.SHORT)) {
            this.portfolio.addPositionQtd(filledOrder.getIdentifier(), filledOrder.getQuantity(), filledOrder.getPriceWithSpread());
            this.historyHandler.setState(TransactionDto.TransactionState.ADD_QTD, filledOrder.getIdentifier());
        }
    }

    private void handleOppositeDirection(FilledOrderDto filledOrder, Position ps) {
        if (filledOrder.getQuantity() == ps.getQuantity()) {
            this.portfolio.closePosition(filledOrder.getIdentifier());
            this.historyHandler.setState(TransactionDto.TransactionState.EXIT, filledOrder.getIdentifier());
        } else {
            this.portfolio.removePositionQtd(filledOrder.getIdentifier(), filledOrder.getQuantity());
            this.historyHandler.setState(TransactionDto.TransactionState.REMOVE_QTD, filledOrder.getIdentifier());
        }
    }

    private Position createNewPosition(FilledOrderDto filledOrder) {
        Position.PositionType position_type = filledOrder.getAction().equals(OrderDto.OrderAction.BUY) ? Position.PositionType.LONG : Position.PositionType.SHORT;

        Position ps1 = new Position(
                position_type,
                filledOrder.getSymbol(),
                filledOrder.getQuantity(),
                filledOrder.getPriceWithSpread(),
                filledOrder.getTime(),
                filledOrder.getIdentifier(),
                filledOrder,
                null,
                FILLED
        );

        portfolio.addNewPosition(ps1);
        historyHandler.setState(TransactionDto.TransactionState.ENTRY, ps1.getIdentifier());
        historyHandler.addPosition(ps1);
        return ps1;
    }

}
