package com.apssouza.mytrade.trading.domain.forex.risk.stopordercreation;

import com.apssouza.mytrade.trading.domain.forex.portfolio.Position;

import java.math.BigDecimal;
import java.util.Optional;

interface CreatorStrategy {

    BigDecimal getEntryStopPrice(Position position, BigDecimal priceClose);

    BigDecimal getHardStopPrice(Position position);

    Optional<BigDecimal> getTrailingStopPrice(Position position, BigDecimal last_close);

    BigDecimal getProfitStopPrice(Position position);

}
