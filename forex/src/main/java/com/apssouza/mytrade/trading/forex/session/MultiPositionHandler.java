package com.apssouza.mytrade.trading.forex.session;

import com.apssouza.mytrade.trading.forex.order.OrderDto;
import com.apssouza.mytrade.trading.forex.order.StopOrderDto;
import com.apssouza.mytrade.trading.forex.portfolio.FilledOrderDto;
import com.apssouza.mytrade.trading.forex.portfolio.Position;
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
        if (order.getIdentifier() == null || order.getIdentifier().isEmpty())
            return order.getSymbol() + '_' + order.getId() + '_' + order.getTime().toEpochSecond(DateTimeHelper.ZONEOFFSET_UTC);

        return order.getIdentifier();
    }

    public static void deleteAllMaps() {
        positionToStopOrderMap = new ConcurrentHashMap<>();
    }


    public static Position getPositionByStopOrder(StopOrderDto stopOrder) {
        if (MultiPositionHandler.positionToStopOrderMap.containsKey(stopOrder.getId())) {
            Position ps = MultiPositionHandler.positionToStopOrderMap.get(stopOrder.getId());
            MultiPositionHandler.positionToStopOrderMap.remove(stopOrder.getId());
            return ps;
        }
        throw new RuntimeException("Not found position for the given stop order. id =  " + stopOrder.getId() + "pair = " + stopOrder.getSymbol());
    }

    public static void mapStopOrderToPosition(StopOrderDto stoporder, Position position) {
        MultiPositionHandler.positionToStopOrderMap.putIfAbsent(stoporder.getId(), position);
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
                    null
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
