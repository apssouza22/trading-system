package com.apssouza.mytrade.trading.domain.forex.portfolio;

import com.apssouza.mytrade.feed.api.PriceDto;
import com.apssouza.mytrade.feed.api.SignalDto;
import com.apssouza.mytrade.trading.domain.forex.common.events.Event;
import com.apssouza.mytrade.trading.domain.forex.common.TradingParams;
import com.apssouza.mytrade.trading.domain.forex.broker.BrokerService;
import com.apssouza.mytrade.trading.domain.forex.common.events.PositionClosedEvent;
import com.apssouza.mytrade.trading.domain.forex.common.events.PriceChangedEvent;
import com.apssouza.mytrade.trading.domain.forex.order.OrderDto;
import com.apssouza.mytrade.trading.domain.forex.order.OrderService;
import com.apssouza.mytrade.trading.domain.forex.risk.RiskManagementService;
import com.apssouza.mytrade.trading.domain.forex.risk.stopordercreation.StopOrderDto;
import com.apssouza.mytrade.trading.domain.forex.risk.stopordercreation.StopOrderFilledEvent;
import com.apssouza.mytrade.trading.domain.forex.common.observerinfra.EventNotifier;
import com.apssouza.mytrade.trading.domain.forex.common.MultiPositionHandler;
import static com.apssouza.mytrade.trading.domain.forex.risk.stopordercreation.StopOrderDto.StopOrderType.STOP_LOSS;
import static com.apssouza.mytrade.trading.domain.forex.risk.stopordercreation.StopOrderDto.StopOrderType.TAKE_PROFIT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class PortfolioService {

    private final OrderService orderService;
    private final BrokerService executionHandler;
    private final PortfolioModel portfolio;
    private final PortfolioChecker portfolioBrokerChecker;
    private final RiskManagementService riskManagementService;
    private final EventNotifier eventNotifier;
    private static Logger log = Logger.getLogger(PortfolioService.class.getName());
    private Map<Integer, StopOrderDto> currentStopOrders = new HashMap<>();

    public PortfolioService(
            OrderService orderService,
            BrokerService executionHandler,
            PortfolioModel portfolio,
            PortfolioChecker portfolioBrokerChecker,
            RiskManagementService riskManagementService,
            EventNotifier eventNotifier
    ) {
        this.orderService = orderService;
        this.executionHandler = executionHandler;
        this.portfolio = portfolio;
        this.portfolioBrokerChecker = portfolioBrokerChecker;
        this.riskManagementService = riskManagementService;
        this.eventNotifier = eventNotifier;
    }

    public void updatePositionsPrices(Map<String, PriceDto> price) {
        this.portfolio.updatePortfolioBalance(price);
    }

    public void createStopOrder(Event event) {
        if (portfolio.getPositionCollection().isEmpty()) {
            return;
        }
        log.info("Creating stop loss...");
        this.executionHandler.deleteStopOrders();
        MultiPositionHandler.deleteAllMaps();

        Map<Integer, StopOrderDto> stopOrders = new HashMap<>();
        for (PositionDto position : this.portfolio.getPositionCollection().getPositions()) {
            var stops = riskManagementService.createStopOrders(position, event);
            position = new PositionDto(position, stops);
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
        if (portfolio.getPositionCollection().isEmpty()) {
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
        if (portfolio.getPositionCollection().isEmpty()) {
            return;
        }
        List<PositionDto> exitedPositions = this.riskManagementService.processPositionExit(event, signals);
        this.createOrderFromClosedPosition(exitedPositions, event);
    }

    private void createOrderFromClosedPosition(List<PositionDto> positions, Event event) {
        for (PositionDto position : positions) {
            if (position.status() != PositionDto.PositionStatus.CLOSED) {
                continue;
            }
            OrderDto order = this.orderService.createOrderFromClosedPosition(position, event.getTimestamp());
            eventNotifier.notify(new PositionClosedEvent(
                    event.getTimestamp(),
                    event.getPrice(),
                    order
            ));
        }
    }

    public List<PositionDto> closeAllPositions(PositionDto.ExitReason reason, Event event) {
        List<PositionDto> exitedPositions = new ArrayList<>();
        for (PositionDto position  : this.portfolio.getPositionCollection().getPositions()) {
            if (!position.isPositionAlive()) {
                continue;
            }
            log.info("Exiting position for(" + position.symbol() + " Reason " + reason);
            portfolio.closePosition(position.identifier(), reason);
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
            closeAllPositions(PositionDto.ExitReason.RECONCILIATION_FAILED, e);
            log.warning(reconciliationException.getMessage());
        }
    }
}
