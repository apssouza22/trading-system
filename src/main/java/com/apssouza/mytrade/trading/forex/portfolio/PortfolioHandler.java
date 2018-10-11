package com.apssouza.mytrade.trading.forex.portfolio;

import com.apssouza.mytrade.feed.price.PriceHandler;
import com.apssouza.mytrade.feed.signal.SignalDto;
import com.apssouza.mytrade.trading.forex.execution.ExecutionHandler;
import com.apssouza.mytrade.trading.forex.order.*;
import com.apssouza.mytrade.trading.forex.risk.PositionExitHandler;
import com.apssouza.mytrade.trading.forex.risk.PositionSizer;
import com.apssouza.mytrade.trading.forex.risk.RiskManagementHandler;
import com.apssouza.mytrade.trading.forex.session.FilledOrderListener;
import com.apssouza.mytrade.trading.forex.session.HistoryBookHandler;
import com.apssouza.mytrade.trading.forex.session.MultiPositionHandler;
import com.apssouza.mytrade.trading.forex.session.OrderCreatedListener;
import com.apssouza.mytrade.trading.forex.statistics.TransactionState;
import com.apssouza.mytrade.trading.misc.helper.config.Properties;
import com.apssouza.mytrade.trading.misc.loop.LoopEvent;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;

public class PortfolioHandler {
    private final BigDecimal equity;

    private final OrderHandler orderHandler;
    private final PositionExitHandler positionExitHandler;
    private final ExecutionHandler executionHandler;
    private final Portfolio portfolio;
    private final ReconciliationHandler reconciliationHandler;
    private final HistoryBookHandler historyHandler;
    private final RiskManagementHandler riskManagementHandler;
    private static Logger log = Logger.getLogger(PortfolioHandler.class.getName());
    private final FilledOrderListener filledOrderListener;
    private final OrderCreatedListener orderCreatedListener;
    private Map<Integer, StopOrderDto> currentStopOrders = new HashMap<>();

    public PortfolioHandler(
            BigDecimal equity,
            OrderHandler orderHandler,
            PositionExitHandler positionExitHandler,
            ExecutionHandler executionHandler,
            Portfolio portfolio,
            ReconciliationHandler reconciliationHandler,
            HistoryBookHandler historyHandler,
            RiskManagementHandler riskManagementHandler
    ) {

        this.equity = equity;
        this.orderHandler = orderHandler;
        this.positionExitHandler = positionExitHandler;
        this.executionHandler = executionHandler;
        this.portfolio = portfolio;
        this.reconciliationHandler = reconciliationHandler;
        this.historyHandler = historyHandler;
        this.riskManagementHandler = riskManagementHandler;
        this.filledOrderListener = new FilledOrderListener(portfolio, historyHandler);
        this.orderCreatedListener = new OrderCreatedListener(executionHandler,historyHandler, filledOrderListener,orderHandler);
    }

    public void updatePortfolioValue(LoopEvent event) {
        this.portfolio.updatePortfolioValue(event);
    }

    public void createStopOrder(LoopEvent event) {
        if (portfolio.getPositions().isEmpty()) {
            log.info("Creating stop loss...");
        }
        this.executionHandler.deleteStopOrders();
        MultiPositionHandler.deleteAllMaps();

        Map<Integer, StopOrderDto> stopOrders = new HashMap<>();
        for (Map.Entry<String, Position> entry : this.portfolio.getPositions().entrySet()) {
            Position position = entry.getValue();
            EnumMap<StopOrderType, StopOrderDto> stops = this.riskManagementHandler.getStopOrders(position, event);
            position = new Position(position, stops);
            StopOrderDto stopLoss = stops.get(StopOrderType.STOP_LOSS);
            log.info("Created stop loss - " + stopLoss);

            StopOrderDto stopOrderLoss = this.executionHandler.placeStopOrder(stopLoss);
            stopOrders.put(stopOrderLoss.getId(), stopOrderLoss);
            MultiPositionHandler.mapStopOrderToPosition(stopOrderLoss, position);

            if (Properties.take_profit_stop_enabled) {
                StopOrderDto stopOrderProfit = this.executionHandler.placeStopOrder(stops.get(StopOrderType.TAKE_PROFIT));
                log.info("Created take profit stop - " + stopOrderProfit);
                stopOrders.put(stopOrderProfit.getId(), stopOrderProfit);
                MultiPositionHandler.mapStopOrderToPosition(stopOrderProfit, position);
            }
        }
        this.currentStopOrders = stopOrders;
    }

    public void processReconciliation() {

    }

    public void onOrder(List<OrderDto> orders) {
        orderCreatedListener.process(orders);
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

    private void onFill(FilledOrderDto filledOrder) {
        filledOrderListener.process(filledOrder);
    }
}
