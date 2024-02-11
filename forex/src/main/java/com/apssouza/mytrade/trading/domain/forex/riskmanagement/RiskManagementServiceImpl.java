package com.apssouza.mytrade.trading.domain.forex.riskmanagement;

import com.apssouza.mytrade.common.time.MarketTimeHelper;
import com.apssouza.mytrade.feed.api.SignalDto;
import com.apssouza.mytrade.trading.domain.forex.common.TradingParams;
import com.apssouza.mytrade.trading.domain.forex.common.events.Event;
import com.apssouza.mytrade.trading.domain.forex.order.OrderDto;
import com.apssouza.mytrade.trading.domain.forex.portfolio.FilledOrderDto;
import com.apssouza.mytrade.trading.domain.forex.portfolio.PositionDto;
import com.apssouza.mytrade.trading.domain.forex.riskmanagement.stopordercreation.StopOrderCreator;
import com.apssouza.mytrade.trading.domain.forex.riskmanagement.stopordercreation.StopOrderDto;
import com.apssouza.mytrade.trading.domain.forex.riskmanagement.stopordercreation.StopOrderDto.StopOrderType;

import java.util.EnumMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

class RiskManagementServiceImpl implements RiskManagementService{

    private final PositionExitChecker exitHandler;
    private final StopOrderCreator stopOrderCreator;

    public RiskManagementServiceImpl(PositionExitChecker exitHandler, StopOrderCreator stopOrderCreator) {
        this.exitHandler = exitHandler;
        this.stopOrderCreator = stopOrderCreator;
    }


    public EnumMap<StopOrderType, StopOrderDto> createStopOrders(PositionDto position, Event event) {
        stopOrderCreator.createContext(position.positionType());
        var orders = new EnumMap<StopOrderType, StopOrderDto>(StopOrderType.class);
        if (!hasStop()) {
            return new EnumMap(StopOrderType.class);
        }

        var stopOrders = position.stopOrders();
        boolean changed_units = false;
        if (!stopOrders.isEmpty()) {
            changed_units = stopOrders.get(StopOrderType.HARD_STOP).quantity() != position.quantity();
        }

        if (stopOrders.isEmpty() || changed_units) {
            orders.put(StopOrderType.HARD_STOP, this.stopOrderCreator.getHardStopLoss(position));
            orders.put(StopOrderType.TAKE_PROFIT, this.stopOrderCreator.getProfitStopOrder(position));
        }

        orders.putAll(getMovingStops(position, event));
        return chooseStopOrders(orders);
    }

    public List<PositionDto> getExitPositions(List<PositionDto> positions, List<SignalDto> signals) {
        return exitHandler.check(positions, signals);
    }

    private EnumMap<StopOrderType, StopOrderDto> getMovingStops(PositionDto position, Event event) {
        EnumMap<StopOrderType, StopOrderDto> stop_orders = new EnumMap(StopOrderType.class);
        Consumer<StopOrderDto> consumer = (dto) -> stop_orders.put(dto.type(), dto);
        if (TradingParams.entry_stop_loss_enabled) {
            Optional<StopOrderDto> entryStopOrder = this.stopOrderCreator.getEntryStopOrder(position, event);
            entryStopOrder.ifPresent(consumer);
        }
        if (TradingParams.trailing_stop_loss_enabled) {
            Optional<StopOrderDto> trailingStopOrder = this.stopOrderCreator.getTrailingStopOrder(position, event);
            trailingStopOrder.ifPresent(consumer);
        }
        return stop_orders;
    }

    private boolean hasStop() {
        return TradingParams.hard_stop_loss_enabled ||
                TradingParams.entry_stop_loss_enabled ||
                TradingParams.trailing_stop_loss_enabled ||
                TradingParams.take_profit_stop_enabled;
    }

    private EnumMap<StopOrderType, StopOrderDto> chooseStopOrders(EnumMap<StopOrderType, StopOrderDto> stop_losses) {
        EnumMap<StopOrderType, StopOrderDto> stop_orders = new EnumMap<>(StopOrderType.class);

        if (TradingParams.take_profit_stop_enabled) {
            stop_orders.put(StopOrderType.TAKE_PROFIT, stop_losses.get(StopOrderType.TAKE_PROFIT));
        }

        StopOrderDto stopOrderDto = null;
        if (stop_losses.containsKey(StopOrderType.TRAILLING_STOP)) {
            stopOrderDto = stop_losses.get(StopOrderType.TRAILLING_STOP);

        }
        if (stop_losses.containsKey(StopOrderType.ENTRY_STOP)) {
            stopOrderDto = stop_losses.get(StopOrderType.ENTRY_STOP);
        }

        if (stopOrderDto == null) {
            stop_orders.put(StopOrderType.STOP_LOSS, stop_losses.get(StopOrderType.HARD_STOP));
            return stop_orders;
        }
        stop_orders.put(StopOrderType.STOP_LOSS, stopOrderDto);
        return stop_orders;
    }

    public boolean canCreateOrder(OrderDto order, List<PositionDto> openPositions) {
        if (TradingParams.trading_position_edit_enabled || TradingParams.trading_multi_position_enabled) {
            return true;
        }
        for (PositionDto position : openPositions) {
            FilledOrderDto filledOrder = position.filledOrder();
            if (filledOrder.symbol().equals(order.symbol()) && filledOrder.action().equals(order.action())) {
                return false;
            }
        }
        return true;
    }

    public boolean canExecuteOrder(Event event, OrderDto order, List<String> processedOrders, List<String> exitedPositions) {
        if (!MarketTimeHelper.isMarketOpened(event.getTimestamp())) {
            return false;
        }
        if (isDuplicatedOrder(order, processedOrders, exitedPositions)) {
            return false;
        }

        return true;
    }

    private boolean isDuplicatedOrder(OrderDto order, List<String> processedOrders, List<String> exitedPositions) {
        /**
         # Avoiding process more than one order for a currency pair in a cycle
         # possibility of more than one order by cycle:
         #     - many signals
         #     - order generated by exits and by the signals
         **/
        if (order.origin().equals(OrderDto.OrderOrigin.SIGNAL)) {
            if (processedOrders.contains(order.symbol())) {
                return true;
            }

            // Not process order coming from signal if( exists a exit for the currency
            if (exitedPositions.contains(order.symbol())) {
                return true;
            }
        }
        return false;
    }

    public int getPositionSize() {
        return 10;
    }
}
