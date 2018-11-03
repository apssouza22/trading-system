package com.apssouza.mytrade.trading.forex.portfolio;

import com.apssouza.mytrade.feed.signal.SignalDto;
import com.apssouza.mytrade.trading.forex.execution.ExecutionHandler;
import com.apssouza.mytrade.trading.forex.order.*;
import com.apssouza.mytrade.trading.forex.risk.PositionExitHandler;
import com.apssouza.mytrade.trading.forex.risk.RiskManagementHandler;
import com.apssouza.mytrade.trading.forex.session.*;
import com.apssouza.mytrade.trading.forex.session.event.*;
import com.apssouza.mytrade.trading.misc.helper.config.Properties;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.BlockingQueue;
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
    private final BlockingQueue<Event> eventQueue;
    private static Logger log = Logger.getLogger(PortfolioHandler.class.getName());
    private Map<Integer, StopOrderDto> currentStopOrders = new HashMap<>();

    public PortfolioHandler(
            BigDecimal equity,
            OrderHandler orderHandler,
            PositionExitHandler positionExitHandler,
            ExecutionHandler executionHandler,
            Portfolio portfolio,
            ReconciliationHandler reconciliationHandler,
            HistoryBookHandler historyHandler,
            RiskManagementHandler riskManagementHandler,
            BlockingQueue<Event> eventQueue
    ) {

        this.equity = equity;
        this.orderHandler = orderHandler;
        this.positionExitHandler = positionExitHandler;
        this.executionHandler = executionHandler;
        this.portfolio = portfolio;
        this.reconciliationHandler = reconciliationHandler;
        this.historyHandler = historyHandler;
        this.riskManagementHandler = riskManagementHandler;
        this.eventQueue = eventQueue;
    }

    public void updatePortfolioValue(Event event) {
        this.portfolio.updatePortfolioValue(event);
    }

    public void createStopOrder(Event event) {
        if (portfolio.getPositions().isEmpty()) {
            return;
        }
        log.info("Creating stop loss...");
        this.executionHandler.deleteStopOrders();
        MultiPositionHandler.deleteAllMaps();

        Map<Integer, StopOrderDto> stopOrders = new HashMap<>();
        for (Map.Entry<String, Position> entry : this.portfolio.getPositions().entrySet()) {
            Position position = entry.getValue();
            EnumMap<StopOrderType, StopOrderDto> stops = this.riskManagementHandler.createStopOrders(position, event);
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

    public void stopOrderHandle(Event event) throws InterruptedException {
        this.executionHandler.processStopOrders();
        this.cancelOpenStopOrders();
        List<StopOrderDto> filledOrders = this.getFilledStopOrders();
        log.info("Total stop loss order filled " + filledOrders.size());

        for (StopOrderDto stopOrder : filledOrders) {
            eventQueue.put(new StopOrderFilledEvent(
                    EventType.STOP_ORDER_FILLED,
                    event.getTimestamp(),
                    event.getPrice(),
                    stopOrder
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

    public synchronized void processExits(PriceChangedEvent event, List<SignalDto> signals) throws InterruptedException {
        List<Position> exitedPositionss = this.positionExitHandler.process(event, signals);
        this.createOrderFromClosedPosition(exitedPositionss, event);
    }

    private void createOrderFromClosedPosition(List<Position> positions, PriceChangedEvent event) throws InterruptedException {
        for (Position position : positions) {
            if (position.getStatus() == PositionStatus.CLOSED) {
                OrderDto order = this.orderHandler.createOrderFromClosedPosition(position, event.getTimestamp());
                this.eventQueue.put(new OrderCreatedEvent(
                        EventType.ORDER_CREATED,
                        event.getTimestamp(),
                        event.getPrice(),
                        order
                ));
            }
        }
    }
}
