package com.apssouza.mytrade.trading.domain.forex.portfolio;

import com.apssouza.mytrade.feed.api.PriceDto;
import com.apssouza.mytrade.feed.api.SignalDto;
import com.apssouza.mytrade.trading.domain.forex.common.events.Event;
import com.apssouza.mytrade.trading.domain.forex.brokerintegration.BrokerIntegrationService;
import com.apssouza.mytrade.trading.domain.forex.common.events.PositionClosedEvent;
import com.apssouza.mytrade.trading.domain.forex.common.events.PriceChangedEvent;
import com.apssouza.mytrade.trading.domain.forex.order.OrderDto;
import com.apssouza.mytrade.trading.domain.forex.order.OrderService;
import com.apssouza.mytrade.trading.domain.forex.risk.RiskManagementService;
import com.apssouza.mytrade.trading.domain.forex.risk.stopordercreation.StopOrderDto;
import com.apssouza.mytrade.trading.domain.forex.common.observerinfra.EventNotifier;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

class PortfolioServiceImpl implements PortfolioService {

    private final OrderService orderService;
    private final BrokerIntegrationService executionHandler;
    private final PortfolioChecker portfolioBrokerChecker;
    private final RiskManagementService riskManagementService;
    private final EventNotifier eventNotifier;
    private static Logger log = Logger.getLogger(PortfolioService.class.getName());
    private final StopOrderHandler stopOrderHandler;
    private PositionCollection positions = new PositionCollection();

    public PortfolioServiceImpl(
            OrderService orderService,
            BrokerIntegrationService executionHandler,
            PortfolioChecker portfolioBrokerChecker,
            RiskManagementService riskManagementService,
            EventNotifier eventNotifier
    ) {
        this.orderService = orderService;
        this.executionHandler = executionHandler;
        this.portfolioBrokerChecker = portfolioBrokerChecker;
        this.riskManagementService = riskManagementService;
        this.eventNotifier = eventNotifier;
        this.stopOrderHandler =  new StopOrderHandler(eventNotifier,executionHandler, riskManagementService);
    }

    public void createStopOrder(Event event){
        stopOrderHandler.createStopOrder(event, getPositions());
    }
    public void handleStopOrder(Event event) {
        stopOrderHandler.handleStopOrder(event, getPositions());
    }

    public void updatePositionsPrices(Map<String, PriceDto> price) {
        positions.updateItems(position -> {
            PriceDto priceDto = price.get(position.symbol());
            return new PositionDto(position, position.quantity(), priceDto.close(), position.avgPrice());
        });
    }


    public synchronized void checkExits(PriceChangedEvent event, List<SignalDto> signals) {
        if (isEmpty()) {
            return;
        }
        List<PositionDto> exitedPositions = this.riskManagementService.getExitPositions(getPositions(), signals);
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
        for (PositionDto position  : getPositions()) {
            if (!position.isPositionAlive()) {
                continue;
            }
            log.info("Exiting position for(" + position.symbol() + " Reason " + reason);
            closePosition(position.identifier(), reason);
            exitedPositions.add(position);
        }
        this.createOrderFromClosedPosition(exitedPositions, event);
        return exitedPositions;
    }

    /**
     * Check if the local portfolio is in sync with the portfolio on the broker
     */
    public void processReconciliation(Event e) {
        try {
            portfolioBrokerChecker.process(positions.getPositions());
        } catch (ReconciliationException reconciliationException) {
            closeAllPositions(PositionDto.ExitReason.RECONCILIATION_FAILED, e);
            log.warning(reconciliationException.getMessage());
        }
    }

    public int size() {
        return positions.size();
    }


    public PositionDto addPositionQtd(String identifier, int qtd, BigDecimal price) throws PortfolioException {
        if (!this.positions.contains(identifier)) {
            throw new PortfolioException("Position not found");
        }
        var ps = this.positions.get(identifier);
        var avgPrice = ps.getNewAveragePrice(qtd, price);
        var newPosition = new PositionDto(ps, qtd, price, avgPrice);
        this.positions.update(newPosition);
        return newPosition;
    }

    public boolean removePositionQtd(String identfier, int qtd) throws PortfolioException {
        if (!this.positions.contains(identfier)) {
            throw new RuntimeException("Position not found");
        }
        PositionDto ps = this.positions.get(identfier);
        var position = addPositionQtd(identfier, -qtd, ps.avgPrice());
        if (position.quantity() == 0) {
            closePosition(position.identifier(), PositionDto.ExitReason.STOP_ORDER_FILLED);
        }
        return true;

    }

    public boolean contains(String identifier) {
        return positions.contains(identifier);
    }

    public PositionDto getPosition(final String identifier) {
        return positions.get(identifier);
    }


    public boolean closePosition(String identifier, PositionDto.ExitReason reason) {
        if (!this.positions.contains(identifier)) {
            throw new RuntimeException("Position not found");
        }
        this.positions.remove(identifier);
        log.info(String.format("Position closed - %s %s  ", identifier, reason));
        return true;
    }


    public PositionDto addNewPosition(PositionDto.PositionType positionType, FilledOrderDto filledOrder) {
        var ps = new PositionDto(
                positionType,
                filledOrder.symbol(),
                filledOrder.quantity(),
                filledOrder.priceWithSpread(),
                filledOrder.time(),
                filledOrder.identifier(),
                filledOrder,
                null,
                PositionDto.PositionStatus.FILLED,
                filledOrder.priceWithSpread(),
                filledOrder.priceWithSpread(),
                new EnumMap<>(StopOrderDto.StopOrderType.class)
        );
        this.positions.add(ps);
        return ps;
    }

    public boolean isEmpty() {
        return positions.isEmpty();
    }

    public List<PositionDto> getPositions() {
        return positions.getPositions();
    }

    public void printPortfolio() {
        positions.getPositions().forEach(position -> log.info(position.toString()));
    }

}
