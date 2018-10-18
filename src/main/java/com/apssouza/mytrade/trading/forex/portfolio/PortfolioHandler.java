package com.apssouza.mytrade.trading.forex.portfolio;

import com.apssouza.mytrade.feed.signal.SignalDto;
import com.apssouza.mytrade.trading.forex.execution.ExecutionHandler;
import com.apssouza.mytrade.trading.forex.order.*;
import com.apssouza.mytrade.trading.forex.risk.PositionExitHandler;
import com.apssouza.mytrade.trading.forex.risk.RiskManagementHandler;
import com.apssouza.mytrade.trading.forex.session.*;
import com.apssouza.mytrade.trading.forex.session.event.*;
import com.apssouza.mytrade.trading.forex.session.listener.FilledOrderListener;
import com.apssouza.mytrade.trading.forex.session.listener.OrderCreatedListener;
import com.apssouza.mytrade.trading.forex.session.listener.StopOrderFilledListener;
import com.apssouza.mytrade.trading.misc.helper.config.Properties;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    private final FilledOrderListener filledOrderListener;
    private final OrderCreatedListener orderCreatedListener;
    private final StopOrderFilledListener stopOrderFilledListener;
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
        this.filledOrderListener = new FilledOrderListener(portfolio, historyHandler);
        this.orderCreatedListener = new OrderCreatedListener(executionHandler, historyHandler, filledOrderListener, orderHandler);
        this.stopOrderFilledListener = new StopOrderFilledListener(portfolio, historyHandler);
    }

    public void updatePortfolioValue(Event event) {
        this.portfolio.updatePortfolioValue(event);
    }

    public void createStopOrder(Event event) {
        if (portfolio.getPositions().isEmpty()) {
            log.info("Creating stop loss...");
        }
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

    public void processReconciliation() {

    }

    public void onOrderCreated(OrderCreatedEvent event) {
        this.orderHandler.persist(event.getOrder());
    }

    public void onOrderFound(OrderFoundEvent event){
        List<OrderDto> orders = MultiPositionHandler.createPositionIdentifier(event.getOrders());
        orderCreatedListener.process(orders);
    }

    public void stopOrderHandle(Event event) {
        this.cancelOpenStopOrders();
        List<StopOrderDto> filledOrders = this.getFilledStopOrders();
        log.info("Total stop loss order filled " + filledOrders.size());
        this.onStopOrderFilled(filledOrders, event.getTimestamp());
    }

    private void onStopOrderFilled(List<StopOrderDto> filledOrders, LocalDateTime time) {
        for (StopOrderDto stopOrder : filledOrders) {
            stopOrderFilledListener.process(stopOrder, time);
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
                OrderDto order = this.orderHandler.createOrderFromClosedPosition(position, event.getTimestamp());
                this.orderHandler.persist(order);
            }
        }
    }

    public void onSignal(SignalCreatedEvent event) {
        log.info("Processing  new signal...");
        if (riskManagementHandler.canCreateOrder(event)){
            OrderDto order = this.orderHandler.createOrderFromSignal(event);
            try {
                this.eventQueue.put(new OrderCreatedEvent(
                        EventType.ORDER_CREATED,
                        event.getTimestamp(),
                        event.getPrice(),
                        order
                ));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }

    private void onFill(FilledOrderDto filledOrder) {
        filledOrderListener.process(filledOrder);
    }
}
