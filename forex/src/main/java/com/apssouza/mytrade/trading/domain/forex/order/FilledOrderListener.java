package com.apssouza.mytrade.trading.domain.forex.order;

import com.apssouza.mytrade.trading.domain.forex.common.Event;
import com.apssouza.mytrade.trading.domain.forex.common.TradingParams;
import com.apssouza.mytrade.trading.domain.forex.common.observer.PropertyChangeEvent;
import com.apssouza.mytrade.trading.domain.forex.common.observer.PropertyChangeListener;
import com.apssouza.mytrade.trading.domain.forex.portfolio.FilledOrderDto;
import com.apssouza.mytrade.trading.domain.forex.portfolio.PortfolioChangedEvent;
import com.apssouza.mytrade.trading.domain.forex.portfolio.PortfolioModel;
import com.apssouza.mytrade.trading.domain.forex.portfolio.Position;
import com.apssouza.mytrade.trading.domain.forex.session.EventNotifier;
import static com.apssouza.mytrade.trading.domain.forex.portfolio.Position.PositionStatus.FILLED;


class FilledOrderListener implements PropertyChangeListener {

    private final PortfolioModel portfolio;
    private final EventNotifier eventNotifier;

    public FilledOrderListener(PortfolioModel portfolio, EventNotifier eventNotifier) {
        this.portfolio = portfolio;
        this.eventNotifier = eventNotifier;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Event event = (Event) evt.getNewValue();
        if (!(event instanceof OrderFilledEvent)) {
            return;
        }

        OrderFilledEvent orderFilledEvent = (OrderFilledEvent) event;

        FilledOrderDto filledOrder = orderFilledEvent.getFilledOrder();
        if (!this.portfolio.getPositions().containsKey(filledOrder.identifier())) {
            Position newPosition = createNewPosition(filledOrder);
            emitEvent(orderFilledEvent, newPosition);
            return;
        }
        Position ps = this.portfolio.getPosition(filledOrder.identifier());
        if (!TradingParams.trading_position_edit_enabled) {
            if (filledOrder.quantity() != ps.getQuantity()) {
                throw new RuntimeException("Not allowed units to be added/removed");
            }
        }
        Position position = handleExistingPosition(filledOrder, ps);
        emitEvent(orderFilledEvent, position);
    }

    private void emitEvent(OrderFilledEvent orderFilledEvent, Position position) {
            eventNotifier.notify(new PortfolioChangedEvent(
                    orderFilledEvent.getTimestamp(),
                    orderFilledEvent.getPrice(),
                    position
            ));
    }


    private Position handleExistingPosition(FilledOrderDto filledOrder, Position ps) {
        if (filledOrder.action().equals(OrderDto.OrderAction.SELL) && ps.getPositionType().equals(Position.PositionType.LONG)) {
            handleOppositeDirection(filledOrder, ps);
            return ps;
        }
        if (filledOrder.action().equals(OrderDto.OrderAction.BUY) && ps.getPositionType().equals(Position.PositionType.SHORT)) {
            handleOppositeDirection(filledOrder, ps);
            return ps;
        }
        handleSameDirection(filledOrder, ps);
        return ps;
    }

    private void handleSameDirection(FilledOrderDto filledOrder, Position ps) {
        if (filledOrder.action().equals(OrderDto.OrderAction.BUY) && ps.getPositionType().equals(Position.PositionType.LONG)) {
            this.portfolio.addPositionQtd(filledOrder.identifier(), filledOrder.quantity(), filledOrder.priceWithSpread());

        }
        if (filledOrder.action().equals(OrderDto.OrderAction.SELL) && ps.getPositionType().equals(Position.PositionType.SHORT)) {
            this.portfolio.addPositionQtd(filledOrder.identifier(), filledOrder.quantity(), filledOrder.priceWithSpread());
        }
    }

    private void handleOppositeDirection(FilledOrderDto filledOrder, Position ps) {
        if (filledOrder.quantity() == ps.getQuantity()) {
            this.portfolio.closePosition(filledOrder.identifier());
            return;
        }
            this.portfolio.removePositionQtd(filledOrder.identifier(), filledOrder.quantity());

    }

    private Position createNewPosition(FilledOrderDto filledOrder) {
        Position.PositionType position_type = filledOrder.action().equals(OrderDto.OrderAction.BUY) ? Position.PositionType.LONG : Position.PositionType.SHORT;

        Position ps1 = new Position(
                position_type,
                filledOrder.symbol(),
                filledOrder.quantity(),
                filledOrder.priceWithSpread(),
                filledOrder.time(),
                filledOrder.identifier(),
                filledOrder,
                null,
                FILLED
        );

        portfolio.addNewPosition(ps1);
        return ps1;
    }

}
