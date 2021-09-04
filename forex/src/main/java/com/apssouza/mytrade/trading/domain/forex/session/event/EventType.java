package com.apssouza.mytrade.trading.domain.forex.session.event;

public enum EventType {

    SIGNAL_CREATED,
    ORDER_CREATED,
    ORDER_FILLED,
    PRICE_CHANGED,
    ORDER_FOUND,
    STOP_ORDER_FILLED,
    SESSION_FINISHED, ENDED_TRADING_DAY, PORTFOLIO_CHANGED
}
