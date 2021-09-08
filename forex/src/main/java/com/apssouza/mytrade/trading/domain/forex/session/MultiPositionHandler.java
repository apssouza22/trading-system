package com.apssouza.mytrade.trading.domain.forex.session;

import com.apssouza.mytrade.trading.domain.forex.order.OrderDto;
import com.apssouza.mytrade.trading.domain.forex.risk.stoporder.StopOrderDto;
import com.apssouza.mytrade.trading.domain.forex.portfolio.FilledOrderDto;
import com.apssouza.mytrade.trading.domain.forex.portfolio.Position;
import com.apssouza.mytrade.common.misc.helper.time.DateTimeHelper;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MultiPositionHandler {
    private static Map<Integer, Position> positionToStopOrderMap = new ConcurrentHashMap<>();

    public static String getIdentifierFromOrder(OrderDto order) {
        if (order.identifier() == null || order.identifier().isEmpty())
            return order.symbol() + '_' + order.id() + '_' + order.time().toEpochSecond(DateTimeHelper.ZONEOFFSET_UTC);

        return order.identifier();
    }

    public static void deleteAllMaps() {
        positionToStopOrderMap = new ConcurrentHashMap<>();
    }


    public static Position getPositionByStopOrder(StopOrderDto stopOrder) {
        if (MultiPositionHandler.positionToStopOrderMap.containsKey(stopOrder.id())) {
            Position ps = MultiPositionHandler.positionToStopOrderMap.get(stopOrder.id());
            MultiPositionHandler.positionToStopOrderMap.remove(stopOrder.id());
            return ps;
        }
        throw new RuntimeException("Not found position for the given stop order. id =  " + stopOrder.id() + "pair = " + stopOrder.symbol());
    }

    public static void mapStopOrderToPosition(StopOrderDto stoporder, Position position) {
        MultiPositionHandler.positionToStopOrderMap.putIfAbsent(stoporder.id(), position);
    }

    public static Map<String, FilledOrderDto> getAggregatedPortfolio(List<Position> portfolio) {
        Map<String, Integer> currencyPositions = new HashMap<>();

        for (Position ps : portfolio) {
            calculateQuantitiesBySymbol(currencyPositions, ps);
        }
        Map<String, FilledOrderDto> positionList = new HashMap<>();
        for (Map.Entry<String, Integer> entry : currencyPositions.entrySet()) {
            if (currencyPositions.get(entry.getKey()) == 0) {
                continue;
            }
            OrderDto.OrderAction position_type = currencyPositions.get(entry.getKey()) > 0 ? OrderDto.OrderAction.BUY : OrderDto.OrderAction.SELL;

            positionList.put(entry.getKey(), new FilledOrderDto(
                    LocalDateTime.now(),
                    entry.getKey(),
                    position_type,
                    Math.abs(currencyPositions.get(entry.getKey())),
                    null,
                    "",
                    0
            ));
        }

        return positionList;
    }

    private static void calculateQuantitiesBySymbol(Map<String, Integer> currencyPositions, Position ps) {
        if (currencyPositions.containsKey(ps.getSymbol())) {
            processExistingSymbol(currencyPositions, ps);
            return;
        }
        if (ps.getPositionType().equals(Position.PositionType.LONG)) {
            currencyPositions.put(ps.getSymbol(), ps.getQuantity());
        }

        if (ps.getPositionType().equals(Position.PositionType.SHORT)) {
            currencyPositions.put(ps.getSymbol(), -ps.getQuantity());
        }

    }

    private static void processExistingSymbol(Map<String, Integer> currencyPositions, Position ps) {
        Integer position_units = currencyPositions.get(ps.getSymbol());
        if (ps.getPositionType().equals(Position.PositionType.LONG)) {
            currencyPositions.put(ps.getSymbol(), position_units + ps.getQuantity());
        }
        if (ps.getPositionType().equals(Position.PositionType.SHORT)) {
            currencyPositions.put(ps.getSymbol(), position_units - ps.getQuantity());
        }
    }

    public static List<OrderDto> createPositionIdentifier(List<OrderDto> orders) {
        List<OrderDto> list = new LinkedList<>();
        for (OrderDto order : orders) {
            list.add(new OrderDto(
                    getIdentifierFromOrder(order),
                    order
            ));
        }
        return list;
    }
}
