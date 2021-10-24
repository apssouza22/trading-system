package com.apssouza.mytrade.trading.domain.forex.risk.stopordercreation;

import com.apssouza.mytrade.trading.domain.forex.portfolio.PositionDto;

import java.math.BigDecimal;
import java.util.Optional;

interface CreatorStrategy {

    BigDecimal getEntryStopPrice(PositionDto position, BigDecimal priceClose);

    BigDecimal getHardStopPrice(PositionDto position);

    Optional<BigDecimal> getTrailingStopPrice(PositionDto position, BigDecimal last_close);

    BigDecimal getProfitStopPrice(PositionDto position);

}
