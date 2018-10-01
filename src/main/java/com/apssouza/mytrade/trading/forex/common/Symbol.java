package com.apssouza.mytrade.trading.forex.common;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.EnumSet;
import java.util.Set;

public enum Symbol {

    // Major 3
    USDJPY(Currency.USD, Currency.JPY, 0.01, 2),
    EURJPY(Currency.EUR, Currency.JPY, 0.01, 2),
    EURUSD(Currency.EUR, Currency.USD, 0.0001, 4),

    // ABC...
    AUDCAD(Currency.AUD, Currency.CAD, 0.0001, 4),
    AUDCHF(Currency.AUD, Currency.CHF, 0.0001, 4),
    AUDJPY(Currency.AUD, Currency.JPY, 0.01, 2),
    AUDNZD(Currency.AUD, Currency.NZD, 0.0001, 4),
    AUDSGD(Currency.AUD, Currency.SGD, 0.0001, 4),
    AUDUSD(Currency.AUD, Currency.USD, 0.0001, 4),

    CADCHF(Currency.CAD, Currency.CHF, 0.0001, 4),
    CADHKD(Currency.CAD, Currency.HKD, 0.0001, 4),
    CADJPY(Currency.CAD, Currency.JPY, 0.01, 2),

    CHFJPY(Currency.CHF, Currency.JPY, 0.01, 2),
    CHFPLN(Currency.CHF, Currency.PLN, 0.0001, 4),
    CHFSGD(Currency.CHF, Currency.SGD, 0.0001, 4),

    EURAUD(Currency.EUR, Currency.AUD, 0.0001, 4),
    EURBRL(Currency.EUR, Currency.BRL, 0.0001, 4),
    EURCAD(Currency.EUR, Currency.CAD, 0.0001, 4),
    EURCHF(Currency.EUR, Currency.CHF, 0.0001, 4),
    EURDKK(Currency.EUR, Currency.DKK, 0.0001, 4),
    EURGBP(Currency.EUR, Currency.GBP, 0.0001, 4),
    EURHKD(Currency.EUR, Currency.HKD, 0.0001, 4),
    EURHUF(Currency.EUR, Currency.HUF, 0.01, 2),
    EURMXN(Currency.EUR, Currency.MXN, 0.0001, 4),
    EURNOK(Currency.EUR, Currency.NOK, 0.0001, 4),
    EURNZD(Currency.EUR, Currency.NZD, 0.0001, 4),
    EURPLN(Currency.EUR, Currency.PLN, 0.0001, 4),
    EURRUB(Currency.EUR, Currency.RUB, 0.0001, 4),
    EURSEK(Currency.EUR, Currency.SEK, 0.0001, 4),
    EURSGD(Currency.EUR, Currency.SGD, 0.0001, 4),
    EURTRY(Currency.EUR, Currency.TRY, 0.0001, 4),
    EURZAR(Currency.EUR, Currency.ZAR, 0.0001, 4),

    GBPAUD(Currency.GBP, Currency.AUD, 0.0001, 4),
    GBPCAD(Currency.GBP, Currency.CAD, 0.0001, 4),
    GBPCHF(Currency.GBP, Currency.CHF, 0.0001, 4),
    GBPJPY(Currency.GBP, Currency.JPY, 0.01, 2),
    GBPNZD(Currency.GBP, Currency.NZD, 0.0001, 4),
    GBPUSD(Currency.GBP, Currency.USD, 0.0001, 4),

    HKDJPY(Currency.HKD, Currency.JPY, 0.0001, 4),
    HUFJPY(Currency.HUF, Currency.JPY, 0.0001, 4),
    MXNJPY(Currency.MXN, Currency.JPY, 0.0001, 4),
    NOKJPY(Currency.NOK, Currency.JPY, 0.0001, 4),
    NZDCAD(Currency.NZD, Currency.CAD, 0.0001, 4),
    NZDCHF(Currency.NZD, Currency.CHF, 0.0001, 4),
    NZDJPY(Currency.NZD, Currency.JPY, 0.01, 2),
    NZDSGD(Currency.NZD, Currency.SGD, 0.0001, 4),
    NZDUSD(Currency.NZD, Currency.USD, 0.0001, 4),
    SGDJPY(Currency.SGD, Currency.JPY, 0.01, 2),
    SEKJPY(Currency.SEK, Currency.JPY, 0.0001, 4),
    TRYJPY(Currency.TRY, Currency.JPY, 0.0001, 4),

    USDBRL(Currency.USD, Currency.BRL, 0.0001, 4),
    USDCAD(Currency.USD, Currency.CAD, 0.0001, 4),
    USDCHF(Currency.USD, Currency.CHF, 0.0001, 4),
    USDCNH(Currency.USD, Currency.CNH, 0.0001, 4),
    USDCZK(Currency.USD, Currency.CZK, 0.01, 2),
    USDDKK(Currency.USD, Currency.DKK, 0.0001, 4),
    USDHKD(Currency.USD, Currency.HKD, 0.0001, 4),
    USDHUF(Currency.USD, Currency.HUF, 0.01, 2),
    USDMXN(Currency.USD, Currency.MXN, 0.0001, 4),
    USDNOK(Currency.USD, Currency.NOK, 0.0001, 4),
    USDPLN(Currency.USD, Currency.PLN, 0.0001, 4),
    USDRON(Currency.USD, Currency.RON, 0.0001, 4),
    USDRUB(Currency.USD, Currency.RUB, 0.0001, 4),
    USDSEK(Currency.USD, Currency.SEK, 0.0001, 4),
    USDSGD(Currency.USD, Currency.SGD, 0.0001, 4),
    USDTRY(Currency.USD, Currency.TRY, 0.0001, 4),
    USDZAR(Currency.USD, Currency.ZAR, 0.0001, 4),

    XAGUSD(Currency.XAG, Currency.USD, 0.01, 2),
    XAUUSD(Currency.XAU, Currency.USD, 0.01, 2),
    ZARJPY(Currency.ZAR, Currency.JPY, 0.0001, 4);


    public static final Set<Symbol> G3 = EnumSet.of(Symbol.USDJPY, Symbol.EURUSD, Symbol.EURJPY);

    public static final Set<Symbol> G7 =
            EnumSet.of(
                    Symbol.USDJPY, Symbol.EURJPY, Symbol.EURUSD,
                    Symbol.GBPUSD, Symbol.AUDUSD, Symbol.USDCAD, Symbol.USDCHF,
                    Symbol.EURGBP, Symbol.EURAUD, Symbol.EURCAD, Symbol.EURCHF,
                    Symbol.GBPJPY, Symbol.AUDJPY, Symbol.CADJPY, Symbol.CHFJPY,
                    Symbol.GBPCHF
            );

    private static final String CurrencyPAIR_SEPARATOR = "/";

    private Currency Currency1;
    private Currency Currency2;
    private double pipValue;
    private int pipScale;

    private String strName;

    Symbol(Currency Currency1, Currency Currency2, double pipValue, int pipScale) {
        this.Currency1 = Currency1;
        this.Currency2 = Currency2;
        this.pipValue = pipValue;
        this.pipScale = pipScale;

        this.strName = Currency1.name() + Currency2.name();
    }

    /**
     * Currency1, Currency2
     *
     * @param Currency1
     * @param Currency2
     * @return
     */
    public static Symbol getSymbolFromCurrency(Currency Currency1, Currency Currency2) {
        return Symbol.valueOf(Currency1.name() + Currency2.name());
    }

    /**
     * @param fixSymbol
     * @return
     */
    public static Symbol valueOfFixSymbol(String fixSymbol) {
        return valueOf(removeCurrencypairSeparator(fixSymbol));
    }

    /**
     * @param symbols
     * @return
     */
    public static Set<Symbol> valueOfStringArray(String[] symbols) {
        Set<Symbol> symbolSet = EnumSet.noneOf(Symbol.class);
        for (String str : symbols) {
            if (str.equals("G3")) {
                symbolSet.addAll(G3);
            }
            if (str.equals("G7")) {
                symbolSet.addAll(G7);
            }
            if (!str.isEmpty()) {
                symbolSet.add(Symbol.valueOf(str));
            }
        }
        return symbolSet;
    }

    @Override
    public String toString() {
        if (strName == null) {
            name();
        }
        return strName;
    }

    /**
     * Returns currency separator
     *
     * @return currency separator
     */
    public static String getCurrencypairsSeparator() {
        return CurrencyPAIR_SEPARATOR;
    }

    public static String removeCurrencypairSeparator(String str) {
        return str.replaceAll(CurrencyPAIR_SEPARATOR, "");
    }

    /**
     * @return Currency with separator
     */
    public String getNameWithSeparator() {
        return Currency1.name() + CurrencyPAIR_SEPARATOR + Currency2.name();
    }

    /**
     * Is there USD
     *
     * @return
     */
    public boolean isContainUsd() {
        return Currency1 == Currency.USD || Currency2 == Currency.USD;
    }


    /**
     * Pips
     *
     * @param price
     * @return
     */
    public double roundPips(double price) {
        BigDecimal bigDecimal = new BigDecimal(price);
        return bigDecimal.setScale(pipScale, RoundingMode.HALF_UP).doubleValue();
    }


    /**
     * Round Pips and return Int Value
     *
     * @param price
     * @return
     */
    public int roundPipsIntValue(double price) {
        double intPrice = price * Math.pow(10, pipScale);
        int intRoundPrice = (int) Math.round(intPrice);
        return intRoundPrice;
    }


    /**
     * pips to real value
     *
     * @param pips
     * @return
     */
    public double convertPipsToReal(double pips) {
        return pips / Math.pow(10, pipScale);
    }

    /**
     * Real value to pip
     *
     * @param realRate
     * @return
     */
    public double convertRealToPips(double realRate) {
        return realRate * Math.pow(10, pipScale);
    }


    public String getStrName() {
        return strName;
    }

    public Currency getCurrency1() {
        return Currency1;
    }

    public Currency getCurrency2() {
        return Currency2;
    }

    public double getPipValue() {
        return pipValue;
    }

    public int getPipScale() {
        return pipScale;
    }


}
