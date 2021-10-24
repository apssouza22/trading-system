package com.apssouza.mytrade.trading.domain.forex.portfolio;

import com.apssouza.mytrade.trading.domain.forex.common.Event;
import com.apssouza.mytrade.trading.domain.forex.common.TradingParams;
import com.apssouza.mytrade.trading.domain.forex.common.observerinfra.Observer;
import com.apssouza.mytrade.trading.domain.forex.order.OrderDto;
import com.apssouza.mytrade.trading.domain.forex.order.OrderFilledEvent;


class FilledOrderListener implements Observer {

    private final PortfolioModel portfolio;
    private final PortfolioService portfolioService;

    public FilledOrderListener(PortfolioModel portfolio, PortfolioService portfolioService) {
        this.portfolio = portfolio;
        this.portfolioService = portfolioService;
    }

    @Override
    public void update(final Event e) {
        if (!(e instanceof OrderFilledEvent event)) {
            return;
        }

        FilledOrderDto filledOrder = event.getFilledOrder();
        if (!this.portfolio.getPositions().containsKey(filledOrder.identifier())) {
            createNewPosition(filledOrder);
            portfolioService.processReconciliation(event);
            return;
        }
        PositionDto ps = this.portfolio.getPosition(filledOrder.identifier());
        if (!TradingParams.trading_position_edit_enabled) {
            if (filledOrder.quantity() != ps.quantity()) {
                throw new RuntimeException("Not allowed units to be added/removed");
            }
        }

        try {
            handleExistingPosition(filledOrder, ps);
        } catch (PortfolioException ex) {
            portfolioService.closeAllPositions(PositionDto.ExitReason.PORTFOLIO_EXCEPTION, e);
            return;
        }
        portfolioService.processReconciliation(event);

    }


    private PositionDto handleExistingPosition(FilledOrderDto filledOrder, PositionDto ps) throws PortfolioException {
        if (filledOrder.action().equals(OrderDto.OrderAction.SELL) && ps.positionType().equals(PositionDto.PositionType.LONG)) {
            handleOppositeDirection(filledOrder, ps);
            return ps;
        }
        if (filledOrder.action().equals(OrderDto.OrderAction.BUY) && ps.positionType().equals(PositionDto.PositionType.SHORT)) {
            handleOppositeDirection(filledOrder, ps);
            return ps;
        }
        handleSameDirection(filledOrder, ps);
        return ps;
    }

    private void handleSameDirection(FilledOrderDto filledOrder, PositionDto ps) throws PortfolioException {
        if (filledOrder.action().equals(OrderDto.OrderAction.BUY) && ps.positionType().equals(PositionDto.PositionType.LONG)) {
            this.portfolio.addPositionQtd(filledOrder.identifier(), filledOrder.quantity(), filledOrder.priceWithSpread());
        }
        if (filledOrder.action().equals(OrderDto.OrderAction.SELL) && ps.positionType().equals(PositionDto.PositionType.SHORT)) {
            this.portfolio.addPositionQtd(filledOrder.identifier(), filledOrder.quantity(), filledOrder.priceWithSpread());
        }
    }

    private void handleOppositeDirection(FilledOrderDto filledOrder, PositionDto ps) throws PortfolioException {
        if (filledOrder.quantity() == ps.quantity()) {
            this.portfolio.closePosition(filledOrder.identifier(), PositionDto.ExitReason.STOP_ORDER_FILLED);
            return;
        }
        this.portfolio.removePositionQtd(filledOrder.identifier(), filledOrder.quantity());

    }

    private PositionDto createNewPosition(FilledOrderDto filledOrder) {
        PositionDto.PositionType position_type = filledOrder.action().equals(OrderDto.OrderAction.BUY) ? PositionDto.PositionType.LONG : PositionDto.PositionType.SHORT;
        return portfolio.addNewPosition(position_type, filledOrder);
    }

}
