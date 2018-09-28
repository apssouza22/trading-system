package com.apssouza.mytrade.trading.misc.helper;

import com.apssouza.mytrade.trading.misc.helper.config.Properties;
import com.apssouza.mytrade.trading.misc.helper.time.DateTimeHelper;

import java.time.LocalDateTime;

public class TradingHelper {

    public static boolean isTradingTime(LocalDateTime currentTime) {
        if (DateTimeHelper.compare(Properties.tradingStartDay, "<", currentTime ))
            return false;
        if (DateTimeHelper.compare(Properties.tradingEndDay, ">", currentTime ))
            return false;
        return true;
    }

}
