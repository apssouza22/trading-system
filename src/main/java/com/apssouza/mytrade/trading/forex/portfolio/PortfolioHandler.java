package com.apssouza.mytrade.trading.forex.portfolio;

import com.apssouza.mytrade.feed.price.PriceHandler;
import com.apssouza.mytrade.feed.signal.SignalDto;
import com.apssouza.mytrade.trading.forex.execution.ExecutionHandler;
import com.apssouza.mytrade.trading.forex.order.*;
import com.apssouza.mytrade.trading.forex.risk.PositionExitHandler;
import com.apssouza.mytrade.trading.forex.risk.PositionSizer;
import com.apssouza.mytrade.trading.forex.risk.RiskManagementHandler;
import com.apssouza.mytrade.trading.forex.session.HistoryBookHandler;
import com.apssouza.mytrade.trading.forex.session.MultiPositionHandler;
import com.apssouza.mytrade.trading.forex.statistics.TransactionState;
import com.apssouza.mytrade.trading.misc.helper.config.Properties;
import com.apssouza.mytrade.trading.misc.loop.LoopEvent;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;

public class PortfolioHandler {
    private final BigDecimal equity;
    private final PriceHandler priceHandler;
    private final OrderHandler orderHandler;
    private final PositionSizer positionSizer;
    private final PositionExitHandler positionExitHandler;
    private final ExecutionHandler executionHandler;
    private final Portfolio portfolio;
    private final ReconciliationHandler reconciliationHandler;
    private final HistoryBookHandler historyHandler;
    private final RiskManagementHandler riskManagementHandler;
    private static Logger log = Logger.getLogger(PortfolioHandler.class.getName());
    private Map<Integer, StopOrderDto> currentStopOrders = new HashMap<>();

    public PortfolioHandler(
            BigDecimal equity,
            PriceHandler priceHandler,
            OrderHandler orderHandler,
            PositionSizer positionSizer,
            PositionExitHandler positionExitHandler,
            ExecutionHandler executionHandler,
            Portfolio portfolio,
            ReconciliationHandler reconciliationHandler,
            HistoryBookHandler historyHandler,
            RiskManagementHandler riskManagementHandler
    ) {

        this.equity = equity;
        this.priceHandler = priceHandler;
        this.orderHandler = orderHandler;
        this.positionSizer = positionSizer;
        this.positionExitHandler = positionExitHandler;
        this.executionHandler = executionHandler;
        this.portfolio = portfolio;
        this.reconciliationHandler = reconciliationHandler;
        this.historyHandler = historyHandler;
        this.riskManagementHandler = riskManagementHandler;
    }

    public void updatePortfolioValue(LoopEvent event) {
        this.portfolio.updatePortfolioValue(event);
    }

    public void createStopOrder(LoopEvent event) {
        if( portfolio.getPositions().isEmpty()){
            log.info("Creating stop loss...");
        }
        this.executionHandler.deleteStopOrders();
        MultiPositionHandler.deleteAllMaps();

        Map<Integer, StopOrderDto> stopOrders = new HashMap<>();
        for( Map.Entry<String, Position> entry: this.portfolio.getPositions().entrySet()) {
            Position position = entry.getValue();
            EnumMap<StopOrderType, StopOrderDto> stops = this.riskManagementHandler.getStopOrders(position, event);
            position = new Position(position, stops);
            StopOrderDto stopLoss = stops.get(StopOrderType.STOP_LOSS);
            log.info("Created stop loss - " + stopLoss);

            StopOrderDto stopOrderLoss = this.executionHandler.placeStopOrder(stopLoss);
            stopOrders.put(stopOrderLoss.getId(), stopOrderLoss);
            MultiPositionHandler.mapStopOrderToPosition(stopOrderLoss, position);

            if (Properties.take_profit_stop_enabled) {
                StopOrderDto stopOrderProfit = this.executionHandler.placeStopOrder(stops.get(StopOrderType.TAKE_PROFIT));
                log.info("Created take profit stop - " + stopOrderProfit);
                stopOrders.put(stopOrderProfit.getId(), stopOrderProfit);
                MultiPositionHandler.mapStopOrderToPosition(stopOrderProfit, position);
            }
        }
        this.currentStopOrders = stopOrders;
    }

    public void processReconciliation() {

    }

    public void onOrder(List<OrderDto> orders) {
        if(orders.isEmpty()) {
            log.info("No orders");
            return;
        }

        log.info(orders.size() + " new orders");
        List<String> processedOrders = new ArrayList<>();
        List<String> exitedPositions = new ArrayList<>();
        for (OrderDto order : orders) {
            if(order.getOrigin().equals(OrderOrigin.STOP_ORDER)) {
                exitedPositions.add(order.getSymbol());
            }
        }

        for (OrderDto order : orders) {
            if(!this.canExecuteOrder(order, processedOrders, exitedPositions)) {
                continue;
            }
            this.historyHandler.addOrder(order);
            FilledOrderDto filledOrder = executionHandler.executeOrder(order);
            if(filledOrder != null) {
                this.onFill(filledOrder);
                this.historyHandler.addOrderFilled(filledOrder);
                orderHandler.updateOrderStatus(order.getId(), OrderStatus.EXECUTED);
                processedOrders.add(order.getSymbol());
            } else {
                orderHandler.updateOrderStatus(order.getId(), OrderStatus.FAILED);
            }
        }
    }

    private boolean canExecuteOrder(OrderDto order, List<String> processedOrders, List<String> exitedPositions) {
        /**
         # Avoiding process more than one order for a currency pair in a cycle
         # possibility of more than one order by cycle:
         #     - many signals
         #     - order generated by exits and by the signals
         **/
        if(order.getOrigin().equals(OrderOrigin.SIGNAL)) {
            if(processedOrders.contains(order.getSymbol())) {
                return false;
            }

//            Not process order coming from signal if( exists a exit for the currency
            if(exitedPositions.contains(order.getSymbol())) {
                return false;
            }
        }
        return true;
    }

    public void stopOrderHandle(LoopEvent event) {
        this.cancelOpenStopOrders();
        List<StopOrderDto> filledOrders = this.getFilledStopOrders();
        log.info("Total stop loss order filled " + filledOrders.size());
        this.closePositionWithStopOrderFilled(filledOrders, event.getTime());
    }

    private void closePositionWithStopOrderFilled(List<StopOrderDto> filledOrders, LocalDateTime time) {
        for (StopOrderDto stopOrder : filledOrders) {
            Position ps = MultiPositionHandler.getPositionByStopOrder(stopOrder);
            ps.closePosition(ExitReason.STOP_ORDER_FILLED);
            this.portfolio.closePosition(ps.getIdentifier());
            this.historyHandler.setState(TransactionState.EXIT, ps.getIdentifier());
            this.historyHandler.addPosition(ps);

            this.historyHandler.addOrderFilled(new FilledOrderDto(
                    time,
                    stopOrder.getSymbol(),
                    stopOrder.getAction(),
                    stopOrder.getQuantity(),
                    stopOrder.getFilledPrice(),
                    ps.getIdentifier(),
                    stopOrder.getId()
            ));
        }
    }

    private List<StopOrderDto> getFilledStopOrders() {
        List<StopOrderDto> filledStopLoss = new ArrayList<>();
        Map<Integer, StopOrderDto> stopOrders = this.executionHandler.getStopLossOrders();
        Map<Integer, StopOrderDto> limitOrders = this.executionHandler.getLimitOrders();
        stopOrders.putAll(limitOrders);
        if(stopOrders.isEmpty())
            return filledStopLoss;

        for (Map.Entry<Integer, StopOrderDto> entry : this.currentStopOrders.entrySet()) {
            StopOrderDto stopOrder = stopOrders.get(entry.getKey());
            if(stopOrder.getStatus() == StopOrderStatus.FILLED) {
                filledStopLoss.add(stopOrder);
            }
        }
        return filledStopLoss;

    }

    private void cancelOpenStopOrders() {
        if(!this.currentStopOrders.isEmpty()) {
            int count = this.executionHandler.cancelOpenStopOrders();
            log.info("Cancelled " + count + " stop loss");

            count = this.executionHandler.cancelOpenLimitOrders();
            log.info("Cancelled " + count + " limit orders");
        }
    }

    public void processExits(LoopEvent event, List<SignalDto> signals) {
        List<Position> exitedPositionss = this.positionExitHandler.process(event, signals);
        this.createOrderFromClosedPosition(exitedPositionss, event);
    }

    private void createOrderFromClosedPosition(List<Position> positions, LoopEvent event) {
        for (Position position : positions) {
            if(position.getStatus() == PositionStatus.CLOSED) {
                OrderDto order = this.orderHandler.createOrderFromClosedPosition(position, event.getTime());
                this.orderHandler.persist(order);
            }
        }
    }

    public void onSignal(LoopEvent event, List<SignalDto> signals) {
        if(signals.isEmpty()) {
            log.info("No signals");
            return;
        }

        log.info("Processing " + signals.size() + " new signals...");
        List<OrderDto> orders = this.orderHandler.createOrderFromSignal(signals, event.getTime());
        for (OrderDto order : orders) {
            this.orderHandler.persist(order);
        }

    }

    private void onFill(FilledOrderDto filledOrder) {
        filledOrder.getAction();
        
//        # If there is no position, create one
        if (!this.portfolio.getPositions().containsKey(filledOrder.getIdentifier())) {
            PositionType position_type = filledOrder.getAction().equals(OrderAction.BUY) ? PositionType.LONG : PositionType.SHORT;

            Position ps1 = new Position(
                    position_type,
                    filledOrder.getSymbol(),
                    filledOrder.getQuantity(),
                    filledOrder.getPriceWithSpread(),
                    filledOrder.getTime(),
                    filledOrder.getIdentifier(),
                    filledOrder,
                    null,
                    PositionStatus.FILLED
            );
            this.portfolio.addNewPosition(ps1);
            this.historyHandler.setState(TransactionState.ENTRY, ps1.getIdentifier());
            this.historyHandler.addPosition(ps1);
        }else {
            Position ps = this.portfolio.getPosition(filledOrder.getIdentifier());
            if (!Properties.trading_position_edit_enabled) {
                if (filledOrder.getQuantity() != ps.getQuantity()) {
                    throw new RuntimeException("Not allow units to be added/removed");
                }
            }
            if (filledOrder.getAction().equals(OrderAction.SELL) && ps.getPositionType().equals(PositionType.LONG)) {
                if (filledOrder.getQuantity() == ps.getQuantity()) {
                    this.portfolio.closePosition(filledOrder.getIdentifier());
                    this.historyHandler.setState(TransactionState.EXIT, filledOrder.getIdentifier());
                } else {
                    this.portfolio.removePositionQtd(filledOrder.getIdentifier(), filledOrder.getQuantity());
                    this.historyHandler.setState(TransactionState.REMOVE_QTD, filledOrder.getIdentifier());
                }
            } else if (filledOrder.getAction().equals(OrderAction.BUY) && ps.getPositionType().equals(PositionType.SHORT)) {
                if (filledOrder.getQuantity() == ps.getQuantity()) {
                    this.portfolio.closePosition(filledOrder.getIdentifier());
                    this.historyHandler.setState(TransactionState.EXIT, filledOrder.getIdentifier());
                } else {
                    this.portfolio.removePositionQtd(filledOrder.getIdentifier(), filledOrder.getQuantity());
                    this.historyHandler.setState(TransactionState.REMOVE_QTD, filledOrder.getIdentifier());
                }
            } else if (filledOrder.getAction().equals(OrderAction.BUY) && ps.getPositionType().equals(PositionType.LONG)){
                this.portfolio.addPositionQtd(filledOrder.getIdentifier(), filledOrder.getQuantity(), filledOrder.getPriceWithSpread());
                this.historyHandler.setState(TransactionState.ADD_QTD, filledOrder.getIdentifier());

            }else if (filledOrder.getAction().equals(OrderAction.SELL) &&  ps.getPositionType().equals(PositionType.SHORT)){
                this.portfolio.addPositionQtd(filledOrder.getIdentifier(), filledOrder.getQuantity(), filledOrder.getPriceWithSpread());
                this.historyHandler.setState(TransactionState.ADD_QTD, filledOrder.getIdentifier());
            }
            this.historyHandler.addPosition(ps);
        }
    }
}
