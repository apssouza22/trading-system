package com.apssouza.mytrade.trading.forex.risk.stoporder.fixed;

import com.apssouza.mytrade.trading.forex.portfolio.Position;

import java.math.BigDecimal;

interface CreatorStrategy {

    BigDecimal getEntryStopPrice(Position position, BigDecimal priceClose);

    BigDecimal getHardStopPrice(Position position);

    BigDecimal getTrailingStopPrice(Position position, BigDecimal last_close);

    BigDecimal getProfitStopPrice(Position position);

}
