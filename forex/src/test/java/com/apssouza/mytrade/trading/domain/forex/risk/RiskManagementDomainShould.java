package com.apssouza.mytrade.trading.domain.forex.risk;

import com.apssouza.mytrade.trading.domain.forex.common.TradingParams;
import com.apssouza.mytrade.trading.domain.forex.order.StopOrderBuilder;
import com.apssouza.mytrade.trading.domain.forex.portfolio.PositionDto;
import com.apssouza.mytrade.trading.domain.forex.portfolio.PositionBuilder;
import com.apssouza.mytrade.trading.domain.forex.risk.stopordercreation.RiskManagementBuilder;
import com.apssouza.mytrade.trading.domain.forex.risk.stopordercreation.StopOrderDto.StopOrderType;
import com.apssouza.mytrade.trading.domain.forex.session.LoopEventBuilder;
import static com.apssouza.mytrade.trading.domain.forex.risk.stopordercreation.StopOrderDto.StopOrderType.ENTRY_STOP;
import static com.apssouza.mytrade.trading.domain.forex.risk.stopordercreation.StopOrderDto.StopOrderType.STOP_LOSS;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;

import junit.framework.TestCase;

@RunWith(MockitoJUnitRunner.class)
public class RiskManagementDomainShould extends TestCase {

    @Test
    public void create_stop_order_only_if_stop_order_enabled() {
        TradingParams.hard_stop_loss_enabled = false;
        TradingParams.entry_stop_loss_enabled = false;
        TradingParams.trailing_stop_loss_enabled = false;
        TradingParams.take_profit_stop_enabled = false;

        RiskManagementService risk = new RiskManagementBuilder().build();

        PositionBuilder positionBuilder = new PositionBuilder();
        PositionDto position = positionBuilder.build();
        LoopEventBuilder loopEventBuilder = new LoopEventBuilder();
        loopEventBuilder.withPriceMap(BigDecimal.TEN);
        var stopOrders = risk.createStopOrders(position, loopEventBuilder.build());
        assertEquals(0, stopOrders.size());

    }

    @Test
    public void create_only_hard_stop_order() {
        TradingParams.hard_stop_loss_enabled = true;
        TradingParams.entry_stop_loss_enabled = false;
        TradingParams.trailing_stop_loss_enabled = false;
        TradingParams.take_profit_stop_enabled = false;

        RiskManagementService risk = new RiskManagementBuilder().build();

        PositionBuilder positionBuilder = new PositionBuilder();
        PositionDto position = positionBuilder.build();
        LoopEventBuilder loopEventBuilder = new LoopEventBuilder();
        loopEventBuilder.withPriceMap(BigDecimal.TEN);
        var stopOrders = risk.createStopOrders(position, loopEventBuilder.build());
        assertEquals(1, stopOrders.size());
        assertTrue(stopOrders.containsKey(STOP_LOSS));
    }

    @Test
    public void create_multiple_stop_orders() {
        TradingParams.hard_stop_loss_enabled = true;
        TradingParams.entry_stop_loss_enabled = true;
        TradingParams.trailing_stop_loss_enabled = true;
        TradingParams.take_profit_stop_enabled = true;

        RiskManagementService risk = new RiskManagementBuilder().build();

        PositionBuilder positionBuilder = new PositionBuilder();
        PositionDto position = positionBuilder.build();

        LoopEventBuilder loopEventBuilder = new LoopEventBuilder();
        loopEventBuilder.withPriceMap(BigDecimal.TEN);

        var stopOrders = risk.createStopOrders(position, loopEventBuilder.build());
        assertEquals(2, stopOrders.size());
        assertTrue(stopOrders.containsKey(StopOrderType.TAKE_PROFIT));
        assertTrue(stopOrders.containsKey(STOP_LOSS));
    }

    @Test
    public void create_trailing_stop_order_instead_entry_stop() {
        TradingParams.hard_stop_loss_enabled = true;
        TradingParams.entry_stop_loss_enabled = true;
        TradingParams.trailing_stop_loss_enabled = true;
        TradingParams.take_profit_stop_enabled = false;

        RiskManagementService risk = new RiskManagementBuilder().build();

        StopOrderBuilder stopOrderBuilder = new StopOrderBuilder();
        PositionBuilder positionBuilder = new PositionBuilder();
        positionBuilder.addStopOrder(stopOrderBuilder.build());

        PositionDto position = positionBuilder.build();

        LoopEventBuilder loopEventBuilder = new LoopEventBuilder();
        loopEventBuilder.withPriceMap(BigDecimal.TEN);

        var stopOrders = risk.createStopOrders(position, loopEventBuilder.build());
        assertEquals(1, stopOrders.size());
        assertTrue(stopOrders.containsKey(STOP_LOSS));
        assertEquals(ENTRY_STOP, stopOrders.get(STOP_LOSS).type());
    }

}