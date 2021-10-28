package com.apssouza.mytrade.trading.domain.forex.portfolio;

import com.apssouza.mytrade.trading.domain.forex.brokerintegration.BrokerIntegrationService;
import com.apssouza.mytrade.trading.domain.forex.common.MultiPositionHandler;
import com.apssouza.mytrade.trading.domain.forex.common.TradingParams;
import com.apssouza.mytrade.trading.domain.forex.common.events.Event;
import com.apssouza.mytrade.trading.domain.forex.common.observerinfra.EventNotifier;
import com.apssouza.mytrade.trading.domain.forex.risk.RiskManagementService;
import com.apssouza.mytrade.trading.domain.forex.risk.stopordercreation.StopOrderDto;
import com.apssouza.mytrade.trading.domain.forex.risk.stopordercreation.StopOrderFilledEvent;
import static com.apssouza.mytrade.trading.domain.forex.risk.stopordercreation.StopOrderDto.StopOrderType.STOP_LOSS;
import static com.apssouza.mytrade.trading.domain.forex.risk.stopordercreation.StopOrderDto.StopOrderType.TAKE_PROFIT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

class StopOrderHandler {

    private static Logger log = Logger.getLogger(PortfolioService.class.getName());
    private final EventNotifier eventNotifier;
    private final BrokerIntegrationService executionHandler;
    private final RiskManagementService riskManagementService;

    private Map<Integer, StopOrderDto> currentStopOrders = new HashMap<>();

    public StopOrderHandler(final EventNotifier eventNotifier, final BrokerIntegrationService executionHandler, final RiskManagementService riskManagementService) {
        this.eventNotifier = eventNotifier;
        this.executionHandler = executionHandler;
        this.riskManagementService = riskManagementService;
    }

    public void createStopOrder(Event event, final List<PositionDto> positions) {
        if (positions.isEmpty()) {
            return;
        }
        log.info("Creating stop loss...");
        this.executionHandler.deleteStopOrders();
        MultiPositionHandler.deleteAllMaps();

        Map<Integer, StopOrderDto> stopOrders = new HashMap<>();
        for (PositionDto position : positions) {
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

    public void handleStopOrder(Event event, final List<PositionDto> positions) {
        if (positions.isEmpty()) {
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
}
