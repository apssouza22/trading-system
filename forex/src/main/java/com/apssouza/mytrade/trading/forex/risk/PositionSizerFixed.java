package com.apssouza.mytrade.trading.forex.risk;

public class PositionSizerFixed implements PositionSizer {

    @Override
    public Integer getQuantity() {
        return 10000;
    }
}
