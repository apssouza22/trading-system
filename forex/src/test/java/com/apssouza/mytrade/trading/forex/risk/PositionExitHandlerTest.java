package com.apssouza.mytrade.trading.forex.risk;

import com.apssouza.mytrade.feed.SignalDto;
import com.apssouza.mytrade.trading.builder.LoopEventBuilder;
import com.apssouza.mytrade.trading.builder.PositionExitBuilder;
import com.apssouza.mytrade.trading.builder.SignalBuilder;
import com.apssouza.mytrade.trading.forex.portfolio.*;
import com.apssouza.mytrade.trading.forex.session.event.PriceChangedEvent;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class PositionExitHandlerTest extends TestCase {

    private PositionExitBuilder exitBuilder;
    private SignalBuilder signalBuilder;
    private LoopEventBuilder loopEventBuilder;


    @Before
    public void setUp() throws Exception {
        this.exitBuilder = new PositionExitBuilder();
        this.signalBuilder = new SignalBuilder();
        this.loopEventBuilder = new LoopEventBuilder();
    }

    @Test
    public void processWithOnePositionExitedWithCounterSignalSell() {

        loopEventBuilder.createPriceMap(BigDecimal.valueOf(1.003));
        PriceChangedEvent loopEvent = loopEventBuilder.build();

        exitBuilder.addPosition(PositionType.LONG);
        PositionExitHandler handler = exitBuilder.build();

        signalBuilder.addSignal(LocalDateTime.MIN, "Sell");
        List<SignalDto> signals = signalBuilder.buildList();

        List<Position> exit_list = handler.process(loopEvent, signals);
        assertEquals(ExitReason.COUNTER_SIGNAL, exit_list.get(0).getExitReason());
    }

    @Test
    public void processWithOnePositionExitedWithCounterSignalBuy() {
        loopEventBuilder.createPriceMap(BigDecimal.valueOf(1.003));
        PriceChangedEvent loopEvent = loopEventBuilder.build();

        exitBuilder.addPosition(PositionType.SHORT);
        PositionExitHandler handler = exitBuilder.build();

        signalBuilder.addSignal(LocalDateTime.MIN, "Buy");
        List<SignalDto> signals = signalBuilder.buildList();

        List<Position> exit_list = handler.process(loopEvent, signals);
        assertEquals(ExitReason.COUNTER_SIGNAL, exit_list.get(0).getExitReason());

    }

    @Test
    public void processWithNoPositionExitedWithCounterSignalBuy() {
        loopEventBuilder.createPriceMap(BigDecimal.valueOf(1.003));
        PriceChangedEvent loopEvent = loopEventBuilder.build();

        exitBuilder.addPosition(PositionType.LONG);
        PositionExitHandler handler = exitBuilder.build();

        signalBuilder.addSignal(LocalDateTime.MIN, "Buy");
        List<SignalDto> signals = signalBuilder.buildList();

        List<Position> exit_list = handler.process(loopEvent, signals);
        assertEquals(0, exit_list.size());

    }


    @Test
    public void processWithNoPositionExitedWithCounterSignalSell() {
        loopEventBuilder.createPriceMap(BigDecimal.valueOf(1.003));
        PriceChangedEvent loopEvent = loopEventBuilder.build();

        exitBuilder.addPosition(PositionType.SHORT);
        PositionExitHandler handler = exitBuilder.build();

        signalBuilder.addSignal(LocalDateTime.MIN, "Sell");
        List<SignalDto> signals = signalBuilder.buildList();

        List<Position> exit_list = handler.process(loopEvent, signals);
        assertEquals(0, exit_list.size());

    }

    @Test
    public void processWithNoSignal() {
        loopEventBuilder.createPriceMap(BigDecimal.valueOf(1.003));
        loopEventBuilder.setTime(LocalDateTime.of(2018,1,1,1,1));
        PriceChangedEvent loopEvent = loopEventBuilder.build();

        exitBuilder.addPosition(PositionType.SHORT);
        PositionExitHandler handler = exitBuilder.build();

        List<SignalDto> signals = signalBuilder.buildList();

        List<Position> exit_list = handler.process(loopEvent, signals);
        assertEquals(0, exit_list.size());
    }

}