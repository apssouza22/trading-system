package com.apssouza.mytrade.trading.misc.loop;

import com.apssouza.mytrade.feed.price.PriceDto;
import com.apssouza.mytrade.feed.price.PriceHandler;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAmount;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class RealEventLoopTest extends TestCase {

    private RealEventLoop obj;

    @Mock
    private PriceHandler priceHandler;
    @Mock
    private PriceDto priceDto;
    @Mock
    private CurrentTimeCreator currentTimeCreator;

    private LocalDateTime start;

    @Before
    public void setUp() throws Exception {
        Map<String, PriceDto> prices = new HashMap<>();
        prices.put("AUDUSD",priceDto);

        this.start = LocalDateTime.of(2018, 1,1,1,1);

        Mockito.when(priceHandler.getPriceSymbolMapped(
                Mockito.any(LocalDateTime.class))
        ).thenReturn(prices);

        Mockito.when(currentTimeCreator.getNow())
                .thenReturn(start)
                .thenReturn(start.plusMinutes(1))
                .thenReturn(start.plusMinutes(2));

        this.obj = new RealEventLoop(
                start,
                start.plusMinutes(2),
                Duration.ofMinutes(1),
                currentTimeCreator,
                priceHandler
        );
    }


    @Test
    public void sleep() {

    }

    @Test
    public void next() {
        assertEquals(this.start, obj.next().getTimestamp());
        assertEquals(this.start.plusMinutes(1), obj.next().getTimestamp());
        assertEquals(this.start.plusMinutes(2), obj.next().getTimestamp());
    }
}