package com.apssouza.mytrade.trading.forex.risk.stoporder.fixed;

import com.apssouza.mytrade.trading.forex.portfolio.Position;

import java.math.BigDecimal;
import java.util.Optional;

interface CreatorStrategy {

    BigDecimal getEntryStopPrice(Position position, BigDecimal priceClose);

    BigDecimal getHardStopPrice(Position position);

    Optional<BigDecimal> getTrailingStopPrice(Position position, BigDecimal last_close);

    BigDecimal getProfitStopPrice(Position position);

}
