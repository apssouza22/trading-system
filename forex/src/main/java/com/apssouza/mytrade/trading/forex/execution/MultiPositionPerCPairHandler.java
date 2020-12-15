package com.apssouza.mytrade.trading.forex.execution;

import com.apssouza.mytrade.trading.forex.order.OrderAction;
import com.apssouza.mytrade.trading.forex.portfolio.FilledOrderDto;

import java.util.Map;

class MultiPositionPerCPairHandler {
    private final Map<String, FilledOrderDto> positions;

    public MultiPositionPerCPairHandler(Map<String, FilledOrderDto> positions) {
        this.positions = positions;
    }

    public void handle(OrderAction action, String currency_pair, Integer quantity) {
        FilledOrderDto filledOrderDto = this.positions.get(currency_pair);
        if (action.equals(OrderAction.SELL) && filledOrderDto.getAction().equals(OrderAction.BUY)) {
            handlerOppositeDirection(currency_pair, quantity, filledOrderDto);
            return;
        }
        if (action.equals(OrderAction.BUY) && filledOrderDto.getAction().equals(OrderAction.SELL)) {
            handlerOppositeDirection(currency_pair, quantity, filledOrderDto);
            return;
        }
        if (action.equals(OrderAction.BUY) && filledOrderDto.getAction().equals(OrderAction.BUY)) {
            handleSameDirection(quantity, filledOrderDto);
            return;
        }
        if (action.equals(OrderAction.SELL) && filledOrderDto.getAction().equals(OrderAction.SELL)) {
            handleSameDirection(quantity, filledOrderDto);
            return;
        }
    }

    private void handleSameDirection(Integer quantity, FilledOrderDto filledOrderDto) {
        filledOrderDto = new FilledOrderDto(filledOrderDto.getQuantity() + quantity, filledOrderDto);
        this.positions.put(filledOrderDto.getSymbol(), filledOrderDto);
    }

    private void handlerOppositeDirection(String currency_pair, Integer quantity, FilledOrderDto filledOrderDto) {
        if (quantity == filledOrderDto.getQuantity()) {
            this.positions.remove(currency_pair);
            return;
        }
        filledOrderDto = new FilledOrderDto(Math.abs(filledOrderDto.getQuantity() - quantity), filledOrderDto);
        this.positions.put(currency_pair, filledOrderDto);

    }
}
