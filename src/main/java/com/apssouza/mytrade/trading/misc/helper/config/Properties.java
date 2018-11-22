package com.apssouza.mytrade.trading.misc.helper.config;

import com.apssouza.mytrade.trading.forex.session.SessionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
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
    public static boolean trading_multi_position_enabled = false;
    public static boolean trading_position_edit_enabled = false;
    public static boolean hard_stop_loss_enabled = true;
    public static boolean entry_stop_loss_enabled = true;
    public static boolean trailing_stop_loss_enabled = true;
    public static boolean take_profit_stop_enabled = true;
    public static Map<String, Integer> currency_pair_significant_digits_in_price;
    public static double hard_stop_loss_distance = 0.1;
    public static double trailing_stop_loss_distance = 1;
    public static double take_profit_distance_fixed = 0.1;
    public static double entry_stop_loss_distance_fixed = 1;
    public static String systemName = "signal_test";
    public static String transaction_path = "/Users/alex/projects/coalface/files/java.csv";

    static {
        currency_pair_significant_digits_in_price = new HashMap<String, Integer>() {{
            put("AUDUSD", 4);
        }};
    }
}
