package com.apssouza.mytrade.trading.domain.forex.risk;

import com.apssouza.mytrade.common.misc.helper.time.MarketTimeHelper;
import com.apssouza.mytrade.feed.api.SignalDto;
import com.apssouza.mytrade.trading.domain.forex.common.Event;
import com.apssouza.mytrade.trading.domain.forex.common.TradingParams;
import com.apssouza.mytrade.trading.domain.forex.feed.pricefeed.PriceChangedEvent;
import com.apssouza.mytrade.trading.domain.forex.feed.signalfeed.SignalCreatedEvent;
import com.apssouza.mytrade.trading.domain.forex.order.OrderDto;
import com.apssouza.mytrade.trading.domain.forex.portfolio.PortfolioModel;
import com.apssouza.mytrade.trading.domain.forex.portfolio.Position;
import com.apssouza.mytrade.trading.domain.forex.risk.stoporder.StopOrderCreator;
import com.apssouza.mytrade.trading.domain.forex.risk.stoporder.StopOrderDto;
import com.apssouza.mytrade.trading.domain.forex.risk.stoporder.StopOrderDto.StopOrderType;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class RiskManagementHandler {

    private final PortfolioModel portfolio;
    private PositionExitHandler exitHandler;
    private final StopOrderCreator stopOrderCreator;

    public RiskManagementHandler(PortfolioModel portfolio, PositionExitHandler exitHandler, StopOrderCreator stopOrderCreator) {
        this.portfolio = portfolio;
        this.exitHandler = exitHandler;
        this.stopOrderCreator = stopOrderCreator;
    }


    public EnumMap<StopOrderType, StopOrderDto> createStopOrders(Position position, Event event) {
        stopOrderCreator.createContext(position.getPositionType());
        var orders = new EnumMap<StopOrderType, StopOrderDto>(StopOrderType.class);
        if (!hasStop()) {
            return new EnumMap(StopOrderType.class);
        }

        var stopOrders = position.getStopOrders();
        boolean changed_units = false;
        if (!stopOrders.isEmpty()) {
            changed_units = stopOrders.get(StopOrderType.HARD_STOP).getQuantity() != position.getQuantity();
        }

        if (stopOrders.isEmpty() || changed_units) {
            orders.put(StopOrderType.HARD_STOP, this.stopOrderCreator.getHardStopLoss(position));
            orders.put(StopOrderType.TAKE_PROFIT, this.stopOrderCreator.getProfitStopOrder(position));
        }

        orders.putAll(getMovingStops(position, event));
        return chooseStopOrders(orders);
    }

    public List<Position> processPositionExit(PriceChangedEvent event, List<SignalDto> signals) {
        return exitHandler.process(event, signals);
    }

    private EnumMap<StopOrderType, StopOrderDto> getMovingStops(Position position, Event event) {
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
        if (TradingParams.trading_position_edit_enabled || TradingParams.trading_multi_position_enabled) {
            return true;
        }
        for (Map.Entry<String, Position> entry : positions.entrySet()) {
            if (entry.getValue().getSymbol().equals(event.getSignal().symbol().toUpperCase())) {
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
        if (order.getOrigin().equals(OrderDto.OrderOrigin.SIGNAL)) {
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

    public int getPositionSize() {
        return 10;
    }
}
