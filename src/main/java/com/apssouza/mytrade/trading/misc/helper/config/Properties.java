package com.apssouza.mytrade.trading.misc.helper.config;

import com.apssouza.mytrade.trading.forex.session.SessionType;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class Properties {
    public static String brokerHost;
    public static String brokerPort;
    public static String brokerClientId;
    public static LocalDateTime tradingStartDay;
    public static LocalDateTime tradingEndDay;
    public static SessionType sessionType;
    public static LocalTime tradingStartTime;
    public static LocalTime tradingEndTime;
    public static boolean trading_multi_position_enabled;
    public static boolean trading_position_edit_enabled;
}
