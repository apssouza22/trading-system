package com.apssouza.mytrade.feed.price;

import com.apssouza.mytrade.feed.api.PriceDto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PriceHandler {
    private final PriceDao priceDao;

    public PriceHandler(PriceDao priceDao) {
        this.priceDao = priceDao;
    }

    public List<PriceDto> getClosestPrice(LocalDateTime time) {
        return this.priceDao.getClosestPrice(time);
    }

    public Map<String, PriceDto> getPriceSymbolMapped(LocalDateTime time){
        List<PriceDto> prices = this.getClosestPrice(time);
        Map<String, PriceDto> priceDtoMap = new HashMap<>();
        for (PriceDto price : prices) {
            priceDtoMap.put(price.symbol(), price);
        }
        return priceDtoMap;
    }

}
