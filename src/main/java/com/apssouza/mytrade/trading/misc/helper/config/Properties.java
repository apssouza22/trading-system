package com.apssouza.mytrade.trading.misc.helper.config;

import com.apssouza.mytrade.trading.forex.session.SessionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

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
    public static boolean hard_stop_loss_enabled;
    public static boolean entry_stop_loss_enabled;
    public static boolean trailing_stop_loss_enabled;
    public static boolean take_profit_stop_enabled;
    public static Map<String, Integer> currency_pair_significant_digits_in_price;
    public static double hard_stop_loss_distance;
    public static double trailing_stop_loss_distance;
    public static double take_profit_distance_fixed;
    public static double entry_stop_loss_distance_fixed;
}
