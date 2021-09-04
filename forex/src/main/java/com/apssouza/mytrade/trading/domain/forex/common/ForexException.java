package com.apssouza.mytrade.trading.domain.forex.common;

public class ForexException extends RuntimeException {

    public ForexException(Exception ex) {
        super(ex);
    }
}
