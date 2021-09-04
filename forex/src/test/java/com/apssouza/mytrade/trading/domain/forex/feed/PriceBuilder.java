package com.apssouza.mytrade.trading.domain.forex.feed;

import com.apssouza.mytrade.feed.api.PriceDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;

public class PriceBuilder {

    private String pair = "AUDUSD";
    private BigDecimal price = BigDecimal.valueOf(1.305);


    public PriceBuilder withPrice(String pair, BigDecimal price) {
        this.pair = pair;
        this.price = price;
        return  this;
    }

    public HashMap<String, PriceDto> builderMap() {
        PriceDto priceDto = build();
        HashMap<String, PriceDto> priceMap = new HashMap<>();
        priceMap.put("AUDUSD", priceDto);
        return priceMap;
    }

    public PriceDto build() {
        return new PriceDto(LocalDateTime.now(), price, price, price, price, pair);
    }
}
