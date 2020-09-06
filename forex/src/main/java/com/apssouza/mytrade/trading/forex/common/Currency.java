package com.apssouza.mytrade.trading.forex.common;


public enum Currency {

    XAU(java.util.Currency.getInstance("XAU")),
    XAG(java.util.Currency.getInstance("XAG")),

    EUR(java.util.Currency.getInstance("EUR")),
    GBP(java.util.Currency.getInstance("GBP")),
    AUD(java.util.Currency.getInstance("AUD")),
    NZD(java.util.Currency.getInstance("NZD")),

    USD(java.util.Currency.getInstance("USD")),

    BRL(java.util.Currency.getInstance("BRL")),
    CAD(java.util.Currency.getInstance("CAD")),
    // use CNY for CNH
    CNH(java.util.Currency.getInstance("CNY")),
    CNY(java.util.Currency.getInstance("CNY")),
    CZK(java.util.Currency.getInstance("CZK")),
    DKK(java.util.Currency.getInstance("DKK")),
    HKD(java.util.Currency.getInstance("HKD")),
    HUF(java.util.Currency.getInstance("HUF")),
    MXN(java.util.Currency.getInstance("MXN")),
    NOK(java.util.Currency.getInstance("NOK")),
    PLN(java.util.Currency.getInstance("PLN")),
    RON(java.util.Currency.getInstance("RON")),
    RUB(java.util.Currency.getInstance("RUB")),
    SEK(java.util.Currency.getInstance("SEK")),
    SGD(java.util.Currency.getInstance("SGD")),
    TRY(java.util.Currency.getInstance("TRY")),
    ZAR(java.util.Currency.getInstance("ZAR")),

    CHF(java.util.Currency.getInstance("CHF")),
    JPY(java.util.Currency.getInstance("JPY"));

    private java.util.Currency currency;

    Currency(java.util.Currency currency) {
        this.currency = currency;
    }

    public java.util.Currency getCurrency() {
        return currency;
    }

}
