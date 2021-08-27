package com.apssouza.mytrade.trading.forex.common;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class NumberHelper {

    public static BigDecimal roundSymbolPrice(String symbol, BigDecimal price){
        MathContext mc = new MathContext(
                TradingParams.currency_pair_significant_digits_in_price.get(symbol),
                RoundingMode.HALF_UP
        );
        return price.round(mc);
    }
}
