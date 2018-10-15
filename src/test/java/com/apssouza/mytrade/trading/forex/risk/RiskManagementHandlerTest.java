package com.apssouza.mytrade.trading.forex.risk;

import com.apssouza.mytrade.trading.builder.*;
import com.apssouza.mytrade.trading.forex.order.*;
import com.apssouza.mytrade.trading.forex.portfolio.Portfolio;
import com.apssouza.mytrade.trading.forex.portfolio.Position;
import com.apssouza.mytrade.trading.forex.risk.stoporder.fixed.StopOrderCreatorFixed;
import com.apssouza.mytrade.trading.misc.helper.config.Properties;
import com.apssouza.mytrade.trading.misc.loop.EventLoop;
import com.oracle.jrockit.jfr.client.EventSettingsBuilder;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class RiskManagementHandlerTest extends TestCase {

    @Test
    public void checkOrders() {

    }


    @Test
    public void createStopOrdersWithStopOrderDisable() {
        Properties.hard_stop_loss_enabled = false;
        Properties.entry_stop_loss_enabled = false;
        Properties.trailing_stop_loss_enabled = false;
        Properties.take_profit_stop_enabled = false;

        RiskManagementBuilder riskManagementBuilder = new RiskManagementBuilder();
        RiskManagementHandler risk = riskManagementBuilder.build();

        PositionBuilder positionBuilder = new PositionBuilder();
        Position position = positionBuilder.build();
        LoopEventBuilder loopEventBuilder = new LoopEventBuilder();
        loopEventBuilder.createPriceMap(BigDecimal.TEN);
        EnumMap<StopOrderType, StopOrderDto> stopOrders = risk.createStopOrders(position, loopEventBuilder.build());
        assertEquals(0, stopOrders.size());

    }

    @Test
    public void createStopOrdersWithHardAndProfitStopOrderEnabled() {
        Properties.hard_stop_loss_enabled = true;
        Properties.entry_stop_loss_enabled = false;
        Properties.trailing_stop_loss_enabled = false;
        Properties.take_profit_stop_enabled = true;

        RiskManagementBuilder riskManagementBuilder = new RiskManagementBuilder();
        RiskManagementHandler risk = riskManagementBuilder.build();

        PositionBuilder positionBuilder = new PositionBuilder();
        Position position = positionBuilder.build();
        LoopEventBuilder loopEventBuilder = new LoopEventBuilder();
        loopEventBuilder.createPriceMap(BigDecimal.TEN);
        EnumMap<StopOrderType, StopOrderDto> stopOrders = risk.createStopOrders(position, loopEventBuilder.build());
        assertEquals(2, stopOrders.size());
        assertTrue(stopOrders.containsKey(StopOrderType.TAKE_PROFIT));
        assertTrue(stopOrders.containsKey(StopOrderType.STOP_LOSS));

    }

    @Test
    public void createStopOrdersWithOnlyHardStopOrderEnabled() {
        Properties.hard_stop_loss_enabled = true;
        Properties.entry_stop_loss_enabled = false;
        Properties.trailing_stop_loss_enabled = false;
        Properties.take_profit_stop_enabled = false;

        RiskManagementBuilder riskManagementBuilder = new RiskManagementBuilder();
        RiskManagementHandler risk = riskManagementBuilder.build();

        PositionBuilder positionBuilder = new PositionBuilder();
        Position position = positionBuilder.build();
        LoopEventBuilder loopEventBuilder = new LoopEventBuilder();
        loopEventBuilder.createPriceMap(BigDecimal.TEN);
        EnumMap<StopOrderType, StopOrderDto> stopOrders = risk.createStopOrders(position, loopEventBuilder.build());
        assertEquals(1, stopOrders.size());
        assertTrue(stopOrders.containsKey(StopOrderType.STOP_LOSS));
    }

    @Test
    public void createStopOrdersWithAllStopOrderEnabled() {
        Properties.hard_stop_loss_enabled = true;
        Properties.entry_stop_loss_enabled = true;
        Properties.trailing_stop_loss_enabled = true;
        Properties.take_profit_stop_enabled = true;

        RiskManagementBuilder riskManagementBuilder = new RiskManagementBuilder();
        RiskManagementHandler risk = riskManagementBuilder.build();

        PositionBuilder positionBuilder = new PositionBuilder();
        Position position = positionBuilder.build();

        LoopEventBuilder loopEventBuilder = new LoopEventBuilder();
        loopEventBuilder.createPriceMap(BigDecimal.TEN);

        EnumMap<StopOrderType, StopOrderDto> stopOrders = risk.createStopOrders(position, loopEventBuilder.build());
        assertEquals(2, stopOrders.size());
        assertTrue(stopOrders.containsKey(StopOrderType.TAKE_PROFIT));
        assertTrue(stopOrders.containsKey(StopOrderType.STOP_LOSS));
    }

    @Test
    public void createStopOrdersWithoutProfitStopOrderEnabled() {
        Properties.hard_stop_loss_enabled = true;
        Properties.entry_stop_loss_enabled = true;
        Properties.trailing_stop_loss_enabled = true;
        Properties.take_profit_stop_enabled = false;

        RiskManagementBuilder riskManagementBuilder = new RiskManagementBuilder();
        RiskManagementHandler risk = riskManagementBuilder.build();

        PositionBuilder positionBuilder = new PositionBuilder();
        Position position = positionBuilder.build();

        LoopEventBuilder loopEventBuilder = new LoopEventBuilder();
        loopEventBuilder.createPriceMap(BigDecimal.TEN);

        EnumMap<StopOrderType, StopOrderDto> stopOrders = risk.createStopOrders(position, loopEventBuilder.build());
        assertEquals(1, stopOrders.size());
        assertTrue(stopOrders.containsKey(StopOrderType.STOP_LOSS));
    }

    @Test
    public void createStopOrdersChoosingEntryStop() {
        Properties.hard_stop_loss_enabled = true;
        Properties.entry_stop_loss_enabled = true;
        Properties.trailing_stop_loss_enabled = false;
        Properties.take_profit_stop_enabled = false;

        RiskManagementBuilder riskManagementBuilder = new RiskManagementBuilder();
        RiskManagementHandler risk = riskManagementBuilder.build();

        StopOrderBuilder stopOrderBuilder = new StopOrderBuilder();
        PositionBuilder positionBuilder = new PositionBuilder();
        positionBuilder.addStopOrder(stopOrderBuilder.build());

        Position position = positionBuilder.build();

        LoopEventBuilder loopEventBuilder = new LoopEventBuilder();
        loopEventBuilder.createPriceMap(BigDecimal.TEN);

        EnumMap<StopOrderType, StopOrderDto> stopOrders = risk.createStopOrders(position, loopEventBuilder.build());
        assertEquals(1, stopOrders.size());
        assertTrue(stopOrders.containsKey(StopOrderType.STOP_LOSS));
        assertEquals(StopOrderType.ENTRY_STOP, stopOrders.get(StopOrderType.STOP_LOSS).getType());
    }

    @Test
    public void createStopOrdersChoosingTrailingStopRatherThenEntryStop() {
        Properties.hard_stop_loss_enabled = true;
        Properties.entry_stop_loss_enabled = true;
        Properties.trailing_stop_loss_enabled = true;
        Properties.take_profit_stop_enabled = false;

        RiskManagementBuilder riskManagementBuilder = new RiskManagementBuilder();
        RiskManagementHandler risk = riskManagementBuilder.build();

        StopOrderBuilder stopOrderBuilder = new StopOrderBuilder();
        PositionBuilder positionBuilder = new PositionBuilder();
        positionBuilder.addStopOrder(stopOrderBuilder.build());

        Position position = positionBuilder.build();

        LoopEventBuilder loopEventBuilder = new LoopEventBuilder();
        loopEventBuilder.createPriceMap(BigDecimal.TEN);

        EnumMap<StopOrderType, StopOrderDto> stopOrders = risk.createStopOrders(position, loopEventBuilder.build());
        assertEquals(1, stopOrders.size());
        assertTrue(stopOrders.containsKey(StopOrderType.STOP_LOSS));
        assertEquals(StopOrderType.TRAILLING_STOP, stopOrders.get(StopOrderType.STOP_LOSS).getType());
    }

}