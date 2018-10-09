package com.apssouza.mytrade.trading.forex.risk;

import com.apssouza.mytrade.trading.forex.order.OrderDto;
import com.apssouza.mytrade.trading.forex.order.StopOrderDto;
import com.apssouza.mytrade.trading.forex.order.StopOrderType;
import com.apssouza.mytrade.trading.forex.portfolio.Portfolio;
import com.apssouza.mytrade.trading.forex.portfolio.Position;
import com.apssouza.mytrade.trading.misc.helper.config.Properties;
import com.apssouza.mytrade.trading.misc.loop.LoopEvent;

import java.util.*;

public class RiskManagementHandler {

    private final Portfolio portfolio;
    private final PositionSizer positionSizer;
    private final StopOrderCreator stopOrderCreator;

    public RiskManagementHandler(Portfolio portfolio, PositionSizer positionSizer, StopOrderCreator stopOrderCreator) {
        this.portfolio = portfolio;
        this.positionSizer = positionSizer;
        this.stopOrderCreator = stopOrderCreator;
    }

    public List<OrderDto> checkOrders(List<OrderDto> orders) {
        return Collections.emptyList();
    }


    private boolean canOpenPosition() {
        return true;
    }

    private Integer getPositionSize() {
        return 0;
    }

    private void setLeverageStats() {

    }


    public EnumMap<StopOrderType, StopOrderDto> getStopOrders(Position position, LoopEvent event) {
            EnumMap<StopOrderType, StopOrderDto> stop_orders = new EnumMap<>(StopOrderType.class);
        if (
                Properties.hard_stop_loss_enabled ||
                        Properties.entry_stop_loss_enabled ||
                        Properties.trailing_stop_loss_enabled ||
                        Properties.take_profit_stop_enabled
        ) {


            EnumMap<StopOrderType, StopOrderDto> stopOrders = position.getStopOrders();
            if (!stopOrders.isEmpty()) {
                boolean changed_units = stopOrders.get(StopOrderType.HARD_STOP).getQuantity() != position.getQuantity();

                if (stopOrders.isEmpty() || changed_units) {
                    stop_orders.put(StopOrderType.HARD_STOP, this.stopOrderCreator.getHardStopLoss(position));
                    stop_orders.put(StopOrderType.TAKE_PROFIT, this.stopOrderCreator.getProfitStopOrder(position));
                }
                return stop_orders;
            }

            if (Properties.entry_stop_loss_enabled) {
                Optional<StopOrderDto> entryStopOrder = this.stopOrderCreator.getEntryStopOrder(position, event);

                if (entryStopOrder.isPresent()) {
                    stop_orders.put(StopOrderType.ENTRY_STOP, entryStopOrder.get());
                }
            }
            if (Properties.trailing_stop_loss_enabled) {
                Optional<StopOrderDto> trailingStopOrder = this.stopOrderCreator.getTrailingStopOrder(position, event);

                if (trailingStopOrder.isPresent()) {
                    stop_orders.put(StopOrderType.TRAILLING_STOP, trailingStopOrder.get());
                }
            }
        }
        return chooseStopOrders(stop_orders);
    }

    public EnumMap<StopOrderType, StopOrderDto> chooseStopOrders(EnumMap<StopOrderType, StopOrderDto> stop_losses) {
        EnumMap<StopOrderType, StopOrderDto>stop_orders = new EnumMap<>(StopOrderType.class);

        if (Properties.take_profit_stop_enabled) {
            stop_orders.put(StopOrderType.TAKE_PROFIT, stop_losses.get(StopOrderType.TAKE_PROFIT));
        }

        StopOrderDto stopOrderDto = null;
        if (stop_losses.containsKey(StopOrderType.TRAILLING_STOP)) {
            stopOrderDto = stop_losses.get(StopOrderType.TRAILLING_STOP);

        } else if (stop_losses.containsKey(StopOrderType.ENTRY_STOP)) {
            stopOrderDto = stop_losses.get(StopOrderType.ENTRY_STOP);
        }

        if (stopOrderDto == null){
            stop_orders.put(StopOrderType.STOP_LOSS,stop_losses.get(StopOrderType.HARD_STOP));
            return stop_orders;
        }
        stop_orders.put(StopOrderType.STOP_LOSS, stopOrderDto);
        return stop_orders;
    }
}
