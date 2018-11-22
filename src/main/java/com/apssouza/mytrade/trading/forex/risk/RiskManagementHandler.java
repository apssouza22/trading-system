package com.apssouza.mytrade.trading.forex.risk;

import com.apssouza.mytrade.trading.forex.order.OrderDto;
import com.apssouza.mytrade.trading.forex.order.OrderOrigin;
import com.apssouza.mytrade.trading.forex.order.StopOrderDto;
import com.apssouza.mytrade.trading.forex.order.StopOrderType;
import com.apssouza.mytrade.trading.forex.portfolio.Portfolio;
import com.apssouza.mytrade.trading.forex.portfolio.Position;
import com.apssouza.mytrade.trading.forex.risk.stoporder.StopOrderCreator;
import com.apssouza.mytrade.trading.forex.session.event.Event;
import com.apssouza.mytrade.trading.forex.session.event.SignalCreatedEvent;
import com.apssouza.mytrade.trading.misc.helper.config.TradingParams;
import com.apssouza.mytrade.trading.misc.helper.time.MarketTimeHelper;

import java.util.*;
import java.util.function.Consumer;

public class RiskManagementHandler {

    private final Portfolio portfolio;
    private final PositionSizer positionSizer;
    private final StopOrderCreator stopOrderCreator;

    public RiskManagementHandler(Portfolio portfolio, PositionSizer positionSizer, StopOrderCreator stopOrderCreator) {
        this.portfolio = portfolio;
        this.positionSizer = positionSizer;
        this.stopOrderCreator = stopOrderCreator;
    }

    private boolean canOpenPosition() {
        return true;
    }

    private Integer getPositionSize() {
        return 0;
    }

    private void setLeverageStats() {

    }


    public EnumMap<StopOrderType, StopOrderDto> createStopOrders(Position position, Event event) {
        stopOrderCreator.createContext(position.getPositionType());

        EnumMap<StopOrderType, StopOrderDto> stop_orders = new EnumMap<>(StopOrderType.class);
        if (!hasStop()) {
            return new EnumMap(StopOrderType.class);
        }

        EnumMap<StopOrderType, StopOrderDto> stopOrders = position.getStopOrders();
        boolean changed_units = false;
        if (!stopOrders.isEmpty()) {
            changed_units = stopOrders.get(StopOrderType.HARD_STOP).getQuantity() != position.getQuantity();
        }

        if (stopOrders.isEmpty() || changed_units) {
            stop_orders.put(StopOrderType.HARD_STOP, this.stopOrderCreator.getHardStopLoss(position));
            stop_orders.put(StopOrderType.TAKE_PROFIT, this.stopOrderCreator.getProfitStopOrder(position));
        }

        stop_orders.putAll(getMovingStops(position, event));
        return chooseStopOrders(stop_orders);
    }

    private EnumMap<StopOrderType, StopOrderDto>  getMovingStops(Position position, Event event) {
        EnumMap<StopOrderType, StopOrderDto> stop_orders = new EnumMap(StopOrderType.class);
        Consumer<StopOrderDto> consumer = (dto) -> stop_orders.put(dto.getType(), dto);
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

        } else if (stop_losses.containsKey(StopOrderType.ENTRY_STOP)) {
            stopOrderDto = stop_losses.get(StopOrderType.ENTRY_STOP);
        }

        if (stopOrderDto == null) {
            stop_orders.put(StopOrderType.STOP_LOSS, stop_losses.get(StopOrderType.HARD_STOP));
            return stop_orders;
        }
        stop_orders.put(StopOrderType.STOP_LOSS, stopOrderDto);
        return stop_orders;
    }

    public boolean canCreateOrder(SignalCreatedEvent event) {
        Map<String, Position> positions = portfolio.getPositions();
        if (TradingParams.trading_position_edit_enabled || TradingParams.trading_multi_position_enabled){
            return true;
        }
        for (Map.Entry<String, Position> entry : positions.entrySet()){
            if (entry.getValue().getSymbol().equals(event.getSignal().getSymbol().toUpperCase())){
                return false;
            }
        }
        return true;
    }

    public boolean canExecuteOrder(Event event, OrderDto order, List<String> processedOrders, List<String> exitedPositions) {
        if (!MarketTimeHelper.isMarketOpened(event.getTimestamp())){
            return false;
        }
        if (isDuplicatedOrder(order,processedOrders, exitedPositions)){
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
        if (order.getOrigin().equals(OrderOrigin.SIGNAL)) {
            if (processedOrders.contains(order.getSymbol())) {
                return true;
            }

//            Not process order coming from signal if( exists a exit for the currency
            if (exitedPositions.contains(order.getSymbol())) {
                return true;
            }
        }
        return false;
    }
}
