package com.apssouza.mytrade.trading.forex.portfolio;

import com.apssouza.mytrade.feed.price.PriceHandler;
import com.apssouza.mytrade.feed.signal.SignalDto;
import com.apssouza.mytrade.trading.forex.execution.ExecutionHandler;
import com.apssouza.mytrade.trading.forex.order.*;
import com.apssouza.mytrade.trading.forex.risk.PositionExitHandler;
import com.apssouza.mytrade.trading.forex.risk.PositionSizer;
import com.apssouza.mytrade.trading.forex.session.HistoryBookHandler;
import com.apssouza.mytrade.trading.forex.session.MultiPositionHandler;
import com.apssouza.mytrade.trading.forex.statistics.TransactionState;
import com.apssouza.mytrade.trading.misc.loop.LoopEvent;
import com.sun.tools.corba.se.idl.constExpr.Not;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
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
        if (orders.isEmpty()) {
            log.info("No orders");
            return;
        }

        log.info(orders.size() + " new orders");
        List<String> processedOrders = new ArrayList<>();
        List<String> exitedPositions = new ArrayList<>();
        for (OrderDto order : orders) {
            if (order.getOrigin().equals(OrderOrigin.STOP_ORDER)) {
                exitedPositions.add(order.getSymbol());
            }
        }

        for (OrderDto order : orders) {
            if (!this.canExecuteOrder(order, processedOrders, exitedPositions)) {
                continue;
            }
            this.historyHandler.addOrder(order);
            FilledOrderDto filledOrder = executionHandler.executeOrder(order);
            if (filledOrder != null) {
                this.on_fill(filledOrder);
                this.historyHandler.addOrderFilled(filledOrder);
                orderHandler.updateOrderStatus(order.getId(), OrderStatus.EXECUTED);
                processedOrders.add(order.getSymbol());
            } else {
                orderHandler.updateOrderStatus(order.getId(), OrderStatus.FAILED);
            }
        }
    }

    private boolean canExecuteOrder(OrderDto order, List<String> processedOrders, List<String> exitedPositions) {
        /**
         # Avoiding process more than one order for a currency pair in a cycle
         # possibility of more than one order by cycle:
         #     - many signals
         #     - order generated by exits and by the signals
         **/
        if (order.getOrigin().equals(OrderOrigin.SIGNAL)) {
            if (processedOrders.contains(order.getSymbol())) {
                return false;
            }

//            Not process order coming from signal if exists a exit for the currency
            if (exitedPositions.contains(order.getSymbol())) {
                return false;
            }
        }
        return true;
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

    private List<StopOrderDto> getFilledStopOrders() {
        List<StopOrderDto> filledStopLoss = new ArrayList<>();
        Map<Integer, StopOrderDto> stopOrders = this.executionHandler.getStopLossOrders();
        Map<Integer, StopOrderDto> limitOrders = this.executionHandler.getLimitOrders();
        stopOrders.putAll(limitOrders);
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
        List<Position> exitedPositionss = this.positionExitHandler.process(event, signals);
        this.createOrderFromClosedPosition(exitedPositionss, event);
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
        if (signals.isEmpty()) {
            log.info("No signals");
            return;
        }

        log.info("Processing " + signals.size() + " new signals...");
        List<OrderDto> orders = this.orderHandler.createOrderFromSignal(signals, event.getTime());
        for (OrderDto order : orders) {
            this.orderHandler.persist(order);
        }

    }

    private void on_fill(FilledOrderDto filledOrder) {

    }
}
