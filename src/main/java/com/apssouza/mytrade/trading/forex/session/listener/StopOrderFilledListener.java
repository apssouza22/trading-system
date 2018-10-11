package com.apssouza.mytrade.trading.forex.session.listener;

import com.apssouza.mytrade.trading.forex.order.StopOrderDto;
import com.apssouza.mytrade.trading.forex.portfolio.ExitReason;
import com.apssouza.mytrade.trading.forex.portfolio.FilledOrderDto;
import com.apssouza.mytrade.trading.forex.portfolio.Portfolio;
import com.apssouza.mytrade.trading.forex.portfolio.Position;
import com.apssouza.mytrade.trading.forex.session.HistoryBookHandler;
import com.apssouza.mytrade.trading.forex.session.MultiPositionHandler;
import com.apssouza.mytrade.trading.forex.session.listener.EventListener;
import com.apssouza.mytrade.trading.forex.statistics.TransactionState;

import java.time.LocalDateTime;

public class StopOrderFilledListener implements EventListener {

    private final Portfolio portfolio;
    private final HistoryBookHandler historyHandler;

    public StopOrderFilledListener(Portfolio portfolio, HistoryBookHandler historyHandler) {
        this.portfolio = portfolio;
        this.historyHandler = historyHandler;
    }

    public void process(StopOrderDto stopOrder, LocalDateTime time){
        Position ps = MultiPositionHandler.getPositionByStopOrder(stopOrder);
        ps.closePosition(ExitReason.STOP_ORDER_FILLED);
        this.portfolio.closePosition(ps.getIdentifier());
        this.historyHandler.setState(TransactionState.EXIT, ps.getIdentifier());
        this.historyHandler.addPosition(ps);

        this.historyHandler.addOrderFilled(new FilledOrderDto(
                time,
                stopOrder.getSymbol(),
                stopOrder.getAction(),
                stopOrder.getQuantity(),
                stopOrder.getFilledPrice(),
                ps.getIdentifier(),
                stopOrder.getId()
        ));
    }
}
