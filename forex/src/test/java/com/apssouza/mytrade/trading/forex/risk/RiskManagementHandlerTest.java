package com.apssouza.mytrade.trading.forex.risk;

import com.apssouza.mytrade.trading.builder.LoopEventBuilder;
import com.apssouza.mytrade.trading.builder.PositionBuilder;
import com.apssouza.mytrade.trading.builder.StopOrderBuilder;
import com.apssouza.mytrade.trading.forex.order.StopOrderDto;
import com.apssouza.mytrade.trading.forex.order.StopOrderType;
import com.apssouza.mytrade.trading.forex.portfolio.Position;
import com.apssouza.mytrade.trading.forex.risk.stoporder.RiskManagementBuilder;
import com.apssouza.mytrade.trading.misc.helper.TradingParams;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.EnumMap;

@RunWith(MockitoJUnitRunner.class)
public class RiskManagementHandlerTest extends TestCase {

    @Test
    public void checkOrders() {

    }


    @Test
    public void createStopOrdersWithStopOrderDisable() {
        TradingParams.hard_stop_loss_enabled = false;
        TradingParams.entry_stop_loss_enabled = false;
        TradingParams.trailing_stop_loss_enabled = false;
        TradingParams.take_profit_stop_enabled = false;

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
        TradingParams.hard_stop_loss_enabled = true;
        TradingParams.entry_stop_loss_enabled = false;
        TradingParams.trailing_stop_loss_enabled = false;
        TradingParams.take_profit_stop_enabled = true;

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
        TradingParams.hard_stop_loss_enabled = true;
        TradingParams.entry_stop_loss_enabled = false;
        TradingParams.trailing_stop_loss_enabled = false;
        TradingParams.take_profit_stop_enabled = false;

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
        TradingParams.hard_stop_loss_enabled = true;
        TradingParams.entry_stop_loss_enabled = true;
        TradingParams.trailing_stop_loss_enabled = true;
        TradingParams.take_profit_stop_enabled = true;

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
        TradingParams.hard_stop_loss_enabled = true;
        TradingParams.entry_stop_loss_enabled = true;
        TradingParams.trailing_stop_loss_enabled = true;
        TradingParams.take_profit_stop_enabled = false;

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
        TradingParams.hard_stop_loss_enabled = true;
        TradingParams.entry_stop_loss_enabled = true;
        TradingParams.trailing_stop_loss_enabled = false;
        TradingParams.take_profit_stop_enabled = false;

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
        TradingParams.hard_stop_loss_enabled = true;
        TradingParams.entry_stop_loss_enabled = true;
        TradingParams.trailing_stop_loss_enabled = true;
        TradingParams.take_profit_stop_enabled = false;

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