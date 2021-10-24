package com.apssouza.mytrade.trading.domain.forex.portfolio;

import com.apssouza.mytrade.trading.domain.forex.broker.BrokerService;
import com.apssouza.mytrade.trading.domain.forex.order.OrderDto;

import java.util.Map;


class PortfolioChecker {
    private final PortfolioModel portfolio;
    private final BrokerService executionHandler;

    public PortfolioChecker(PortfolioModel portfolio, BrokerService executionHandler) {
        this.portfolio = portfolio;
        this.executionHandler = executionHandler;
    }

    /**
     * Check if the local portfolio is in sync with the portfolio on the broker
     */
    public void process() throws ReconciliationException {
        Map<String, PositionDto> localPositions = portfolio.getPositions();
        Map<String, FilledOrderDto> remotePositions = executionHandler.getPositions();

        if (localPositions.isEmpty() && remotePositions.isEmpty()) {
            return;
        }

        if (localPositions.size() != remotePositions.size()) {
            throw new ReconciliationException("Portfolio is not in sync", localPositions, remotePositions);
        }

        if (localPositions.size() == remotePositions.size()) {
            checkEveryPosition(localPositions, remotePositions);
        }
    }

    private void checkEveryPosition(Map<String, PositionDto> localPositions,
            Map<String, FilledOrderDto> remotePositions
    ) throws ReconciliationException {
        for (Map.Entry<String, PositionDto> entry : localPositions.entrySet()) {
            String symbol = entry.getValue().symbol();
            if (!remotePositions.containsKey(symbol)) {
                throw new ReconciliationException("Position key mismatch", localPositions, remotePositions);
            }
            OrderDto.OrderAction orderAction = entry.getValue().positionType().getOrderAction();
            if (!remotePositions.get(symbol).action().equals(orderAction)) {
                throw new ReconciliationException("Position action mismatch", localPositions, remotePositions);
            }
        }
    }
}
