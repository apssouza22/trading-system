package moduletest;

import com.apssouza.mytrade.feed.api.PriceDto;
import com.apssouza.mytrade.feed.api.SignalDto;
import com.apssouza.mytrade.trading.domain.forex.feed.TradingFeed;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import static java.util.Collections.singletonList;

public class FeedMockBuilder {
    TradingFeed feed = mock(TradingFeed.class);
    HashMap<String, PriceDto> priceDtoHashMap = new HashMap<>();

    public FeedMockBuilder withPrice(LocalDateTime time) {
        priceDtoHashMap.put("AUDUSD", new PriceDto(
                time,
                BigDecimal.ONE,
                BigDecimal.ONE,
                BigDecimal.ONE,
                BigDecimal.ONE,
                "AUDUSD"
        ));
        return this;
    }
    public FeedMockBuilder withSignal(LocalDateTime time, String action) {
        when(feed.getSignal(any(), eq(time.plusSeconds(2)))).thenReturn(singletonList(new SignalDto(
                time.plusSeconds(2),
                action,
                "AUDUSD",
                "signal_test"
        )));
        return this;
    }

    public TradingFeed build() {
        when(feed.getPriceSymbolMapped(any())).thenReturn(priceDtoHashMap);
        return feed;
    }
}
