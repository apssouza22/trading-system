package com.apssouza.mytrade.trading.misc.loop;

import com.apssouza.mytrade.feed.price.PriceDto;
import com.apssouza.mytrade.feed.price.PriceHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
@RunWith(MockitoJUnitRunner.class)
public class RangeEventLoopTest {

    private RangeEventLoop obj;

    @Mock
    private PriceHandler priceHandler;
    @Mock
    private PriceDto priceDto;
    private LocalDateTime start;

    @Before
    public void setUp() throws Exception {
        Map<String, PriceDto> prices = new HashMap<>();
        prices.put("AUDUSD",priceDto);

        this.start = LocalDateTime.of(2018, 1,1,1,1);
        Mockito.when(priceHandler.getPriceSymbolMapped(
                Mockito.any(LocalDateTime.class))
        ).thenReturn(prices);
        this.obj = new RangeEventLoop(Arrays.asList(
                start,
                start.plusMinutes(1),
                start.plusMinutes(2)
        ), priceHandler);
    }

    @Test
    public void hasNext() {
        assertTrue( obj.hasNext());
        obj.next();
        assertTrue(obj.hasNext());
        obj.next();
        assertTrue(obj.hasNext());
        obj.next();
        assertFalse(obj.hasNext());
    }

    @Test
    public void next() {
        assertEquals(this.start, obj.next().getTime());
        assertEquals(this.start.plusMinutes(1), obj.next().getTime());
        assertEquals(this.start.plusMinutes(2), obj.next().getTime());
    }
}