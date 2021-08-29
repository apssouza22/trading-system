package com.apssouza.mytrade.trading.forex.portfolio;

import com.apssouza.mytrade.trading.forex.execution.OrderExecution;
import com.apssouza.mytrade.trading.forex.order.OrderAction;
import com.apssouza.mytrade.trading.forex.session.event.Event;

import java.util.Map;

class ReconciliationHandler {
    private final PortfolioModel portfolio;
    private final OrderExecution executionHandler;

    public ReconciliationHandler(PortfolioModel portfolio, OrderExecution executionHandler) {
        this.portfolio = portfolio;
        this.executionHandler = executionHandler;
    }

    public void process(Event event) throws ReconciliationException {
        Map<String, Position> localPositions = portfolio.getPositions();
        Map<String, FilledOrderDto> remotePositions = executionHandler.getPositions();

        if (localPositions.isEmpty() && remotePositions.isEmpty()){
            return;
        }

        if (localPositions.size() != remotePositions.size()){
            throw new ReconciliationException(localPositions, remotePositions, event);
        }

        if (localPositions.size() == remotePositions.size()){
            checkEveryPosition(event, localPositions, remotePositions);
        }
    }

    private void checkEveryPosition(
            Event event, Map<String, Position> localPositions,
            Map<String, FilledOrderDto> remotePositions
    ) throws ReconciliationException {
        for (Map.Entry<String, Position> entry : localPositions.entrySet()) {
            String symbol = entry.getValue().getSymbol();
            if (!remotePositions.containsKey(symbol)){
                throw new ReconciliationException(localPositions, remotePositions, event);
            }
            OrderAction orderAction = entry.getValue().getPositionType().getOrderAction();
            if (!remotePositions.get(symbol).getAction().equals(orderAction)){
                throw new ReconciliationException(localPositions, remotePositions, event);
            }
        }
    }
}
