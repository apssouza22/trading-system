package com.apssouza.mytrade.trading.domain.forex.portfolio;

import com.apssouza.mytrade.feed.api.SignalDto;
import com.apssouza.mytrade.trading.domain.forex.common.Event;
import com.apssouza.mytrade.trading.domain.forex.common.TradingParams;
import com.apssouza.mytrade.trading.domain.forex.broker.OrderExecution;
import com.apssouza.mytrade.trading.domain.forex.feed.pricefeed.PriceChangedEvent;
import com.apssouza.mytrade.trading.domain.forex.order.OrderDto;
import com.apssouza.mytrade.trading.domain.forex.order.OrderHandler;
import com.apssouza.mytrade.trading.domain.forex.risk.RiskManagementHandler;
import com.apssouza.mytrade.trading.domain.forex.risk.stoporder.StopOrderDto;
import com.apssouza.mytrade.trading.domain.forex.risk.stoporder.StopOrderFilledEvent;
import com.apssouza.mytrade.trading.domain.forex.session.EventNotifier;
import com.apssouza.mytrade.trading.domain.forex.session.MultiPositionHandler;
import static com.apssouza.mytrade.trading.domain.forex.risk.stoporder.StopOrderDto.StopOrderType.STOP_LOSS;
import static com.apssouza.mytrade.trading.domain.forex.risk.stoporder.StopOrderDto.StopOrderType.TAKE_PROFIT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class PortfolioHandler {

    private final OrderHandler orderHandler;
    private final OrderExecution executionHandler;
    private final PortfolioModel portfolio;
    private final PortfoliosChecker portfolioBrokerChecker;
    private final RiskManagementHandler riskManagementHandler;
    private final EventNotifier eventNotifier;
    private static Logger log = Logger.getLogger(PortfolioHandler.class.getName());
    private Map<Integer, StopOrderDto> currentStopOrders = new HashMap<>();

    public PortfolioHandler(
            OrderHandler orderHandler,
            OrderExecution executionHandler,
            PortfolioModel portfolio,
            PortfoliosChecker portfolioBrokerChecker,
            RiskManagementHandler riskManagementHandler,
            EventNotifier eventNotifier
    ) {
        this.orderHandler = orderHandler;
        this.executionHandler = executionHandler;
        this.portfolio = portfolio;
        this.portfolioBrokerChecker = portfolioBrokerChecker;
        this.riskManagementHandler = riskManagementHandler;
        this.eventNotifier = eventNotifier;
    }

    public void updatePositionsPrices(Event event) {
        this.portfolio.updatePortfolioBalance(event);
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
            var stops = riskManagementHandler.createStopOrders(position, event);
            position = new Position(position, stops);
            var stopLoss = stops.get(STOP_LOSS);
            log.info("Created stop loss - " + stopLoss);

            StopOrderDto stopOrderLoss = this.executionHandler.placeStopOrder(stopLoss);
            stopOrders.put(stopOrderLoss.id(), stopOrderLoss);
            MultiPositionHandler.mapStopOrderToPosition(stopOrderLoss, position);

            if (TradingParams.take_profit_stop_enabled) {
                var stopOrderProfit = this.executionHandler.placeStopOrder(stops.get(TAKE_PROFIT));
                log.info("Created take profit stop - " + stopOrderProfit);
                stopOrders.put(stopOrderProfit.id(), stopOrderProfit);
                MultiPositionHandler.mapStopOrderToPosition(stopOrderProfit, position);
            }
        }
        this.currentStopOrders = stopOrders;
    }

    public void handleStopOrder(Event event) {
        if (portfolio.getPositions().isEmpty()) {
            return;
        }
        this.executionHandler.processStopOrders();
        this.cancelOpenStopOrders();
        List<StopOrderDto> filledOrders = this.getFilledStopOrders();
        log.info("Total stop loss order filled " + filledOrders.size());

        for (StopOrderDto stopOrder : filledOrders) {
            eventNotifier.notify(new StopOrderFilledEvent(
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
        if (stopOrders.isEmpty()) {
            return filledStopLoss;
        }

        for (Map.Entry<Integer, StopOrderDto> entry : this.currentStopOrders.entrySet()) {
            StopOrderDto stopOrder = stopOrders.get(entry.getKey());
            if (stopOrder.status() == StopOrderDto.StopOrderStatus.FILLED) {
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

    public synchronized void processExits(PriceChangedEvent event, List<SignalDto> signals) {
        if (portfolio.getPositions().isEmpty()) {
            return;
        }
        List<Position> exitedPositions = this.riskManagementHandler.processPositionExit(event, signals);
        this.createOrderFromClosedPosition(exitedPositions, event);
    }

    private void createOrderFromClosedPosition(List<Position> positions, Event event) {
        for (Position position : positions) {
            if (position.getStatus() != Position.PositionStatus.CLOSED) {
                continue;
            }
            OrderDto order = this.orderHandler.createOrderFromClosedPosition(position, event.getTimestamp());
            eventNotifier.notify(new PositionClosedEvent(
                    event.getTimestamp(),
                    event.getPrice(),
                    order
            ));
        }

    }

    public List<Position> closeAllPositions(Position.ExitReason reason, Event event) {
        List<Position> exitedPositions = new ArrayList<>();
        for (Map.Entry<String, Position> entry : this.portfolio.getPositions().entrySet()) {
            Position position = entry.getValue();
            if (position.getStatus() == Position.PositionStatus.CLOSED) {
                continue;
            }
            log.info("Exiting position for(" + position.getSymbol() + " Reason " + reason);
            position = position.closePosition(reason);
            this.portfolio.addNewPosition(position);
            exitedPositions.add(position);
        }
        this.createOrderFromClosedPosition(exitedPositions, event);
        return exitedPositions;
    }

    public PortfolioModel getPortfolio() {
        return portfolio;
    }

    /**
     * Check if the local portfolio is in sync with the portfolio on the broker
     */
    public void processReconciliation(Event e) {
        try {
            portfolioBrokerChecker.process();
        } catch (ReconciliationException reconciliationException) {
            closeAllPositions(Position.ExitReason.RECONCILIATION_FAILED, e);
            log.warning(reconciliationException.getMessage());
        }
    }
}
