package com.apssouza.mytrade.trading.forex.common;

import com.apssouza.mytrade.trading.forex.order.OrderDto;
import com.apssouza.mytrade.trading.forex.portfolio.Position;
import com.apssouza.mytrade.common.misc.helper.time.DayHelper;

import java.time.LocalDateTime;

public class TradingHelper {

    public static boolean isTradingTime(LocalDateTime currentTime) {
        if (DayHelper.isWeekend(currentTime.toLocalDate()))
            return false;

        if (TradingParams.tradingStartTime.compareTo(currentTime.toLocalTime()) > 0)
            return false;
        if (TradingParams.tradingEndTime.compareTo(currentTime.toLocalTime()) < 0)
            return false;
        return true;
    }

    public static boolean hasEndedTradingTime(LocalDateTime currentTime) {
        if (TradingParams.tradingEndTime.compareTo(currentTime.toLocalTime()) >= 0)
            return false;
        return true;
    }



    public static OrderDto.OrderAction getExitOrderActionFromPosition(Position position) {
        OrderDto.OrderAction action = OrderDto.OrderAction.BUY;
        if (position.getPositionType() == Position.PositionType.LONG) {
            action = OrderDto.OrderAction.SELL;
        }
        return action;
    }

}
