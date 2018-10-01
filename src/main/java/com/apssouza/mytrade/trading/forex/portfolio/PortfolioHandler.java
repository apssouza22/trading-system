package com.apssouza.mytrade.trading.forex.portfolio;

import com.apssouza.mytrade.feed.price.PriceHandler;
import com.apssouza.mytrade.feed.signal.SignalDao;
import com.apssouza.mytrade.feed.signal.SignalDto;
import com.apssouza.mytrade.trading.forex.execution.ExecutionHandler;
import com.apssouza.mytrade.trading.forex.order.*;
import com.apssouza.mytrade.trading.forex.risk.PositionExitHandler;
import com.apssouza.mytrade.trading.forex.risk.PositionSizer;
import com.apssouza.mytrade.trading.forex.session.HistoryBookHandler;
import com.apssouza.mytrade.trading.forex.session.MultiPositionHandler;
import com.apssouza.mytrade.trading.forex.statistics.TransactionState;
import com.apssouza.mytrade.trading.misc.loop.LoopEvent;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class PortfolioHandler {
    private final BigDecimal equity;
    private final PriceHandler priceHandler;
    private final OrderHandler orderHandler;
    private final PositionSizer positionSizer;
    private final PositionExitHandler positionExitHandler;
    private final ExecutionHandler executionHandler;
    private final StopOrderHandler stopOrderHandler;
    private final Portfolio portfolio;
    private final ReconciliationHandler reconciliationHandler;
    private final HistoryBookHandler historyHandler;
    private static Logger log = Logger.getLogger(PortfolioHandler.class.getName());
    private Map<Integer, StopOrderDto> currentStopOrders = new HashMap<>();

    public PortfolioHandler(
            BigDecimal equity,
            PriceHandler priceHandler,
            OrderHandler orderHandler,
            PositionSizer positionSizer,
            PositionExitHandler positionExitHandler,
            ExecutionHandler executionHandler,
            StopOrderHandler stopOrderHandler,
            Portfolio portfolio,
            ReconciliationHandler reconciliationHandler,
            HistoryBookHandler historyHandler
    ) {

        this.equity = equity;
        this.priceHandler = priceHandler;
        this.orderHandler = orderHandler;
        this.positionSizer = positionSizer;
        this.positionExitHandler = positionExitHandler;
        this.executionHandler = executionHandler;
        this.stopOrderHandler = stopOrderHandler;
        this.portfolio = portfolio;
        this.reconciliationHandler = reconciliationHandler;
        this.historyHandler = historyHandler;
    }

    public void updatePortfolioValue(LoopEvent event) {
        this.portfolio.updatePortfolioValue(event);
    }

    public void createStopOrder(LoopEvent event) {

    }

    public void processReconciliation() {

    }

    public void onOrder(List<OrderDto> orders) {

    }

    public void stopOrderHandle(LoopEvent event) {
        this.cancelOpenStopOrders();
        List<StopOrderDto> filledOrders = this.getFilledStopOrders();
        log.info("Total stop loss order filled " + filledOrders.size());
        this.closePositionWithStopOrderFilled(filledOrders, event.getTime());
    }

    private void closePositionWithStopOrderFilled(List<StopOrderDto> filledOrders, LocalDateTime time) {
        for (StopOrderDto stopOrder : filledOrders) {
            Position ps = MultiPositionHandler.getPositionByStopOrder(stopOrder);
            ps.closePosition(ExitReason.STOP_ORDER_FILLED);
            this.portfolio.closePosition(ps.getIdentifier());
            this.historyHandler.setState(TransactionState.EXIT, ps.getIdentifier());
            this.historyHandler.addPosition(ps);

            BigDecimal priceWithSpread;
            if (stopOrder.getAction() == OrderAction.SELL)
                priceWithSpread = stopOrder.getPrice().subtract(stopOrder.getSpread());
            else {
                priceWithSpread = stopOrder.getPrice().add(stopOrder.getSpread());
            }

            this.historyHandler.addOrderFilled(new FilledOrderDto(
                    time,
                    stopOrder.getSymbol(),
                    stopOrder.getAction(),
                    stopOrder.getQuantity(),
                    priceWithSpread,
                    ps.getIdentifier(),
                    stopOrder.getId(),
                    stopOrder.getSpread()
            ));
        }
    }

    private List<StopOrderDto> getFilledStopOrders() {
        List<StopOrderDto> filledStopLoss = new ArrayList<>();
        List<StopOrderDto> stopOrders = this.executionHandler.getStopLossOrders();
        List<StopOrderDto> limitOrders = this.executionHandler.getLimitOrders();
        stopOrders.addAll(limitOrders);
        if (stopOrders.isEmpty())
            return filledStopLoss;

        for (Map.Entry<Integer, StopOrderDto> entry : this.currentStopOrders.entrySet()) {
            StopOrderDto stopOrder = stopOrders.get(entry.getKey());
            if (stopOrder.getStatus() == StopOrderStatus.FILLED) {
                filledStopLoss.add(stopOrder);
            }
        }
        return filledStopLoss;

    }

    private void cancelOpenStopOrders() {
        if (!this.currentStopOrders.isEmpty()) {
            int count = this.executionHandler.cancelOpenStopOrders();
            log.info("Cancelled " + count + " stop loss");

            count = this.executionHandler.cancelOpenLimitOrders();
            log.info("Cancelled " + count + " limit orders");
        }
    }

    public void processExits(LoopEvent event, List<SignalDto> signals) {
        List<Position> exited_positions = this.positionExitHandler.process(event, signals);
        this.createOrderFromClosedPosition(exited_positions, event);
    }

    private void createOrderFromClosedPosition(List<Position> positions, LoopEvent event) {
        for (Position position : positions) {
            if (position.getStatus() == PositionStatus.CLOSED) {
                OrderDto order = this.orderHandler.createOrderFromClosedPosition(position, event.getTime());
                this.orderHandler.persist(order);
            }
        }
    }

    public void onSignal(LoopEvent event, List<SignalDto> signals) {

    }
}
