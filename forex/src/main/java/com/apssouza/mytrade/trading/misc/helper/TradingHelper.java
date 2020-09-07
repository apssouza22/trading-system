package com.apssouza.mytrade.trading.misc.helper;

import com.apssouza.mytrade.trading.forex.order.OrderAction;
import com.apssouza.mytrade.trading.forex.portfolio.Position;
import com.apssouza.mytrade.trading.forex.portfolio.PositionType;
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



    public static OrderAction getExitOrderActionFromPosition(Position position) {
        OrderAction action = OrderAction.BUY;
        if (position.getPositionType() == PositionType.LONG) {
            action = OrderAction.SELL;
        }
        return action;
    }

}
