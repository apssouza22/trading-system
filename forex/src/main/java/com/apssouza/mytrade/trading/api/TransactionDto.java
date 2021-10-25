package com.apssouza.mytrade.trading.api;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public record TransactionDto(com.apssouza.mytrade.trading.domain.forex.orderbook.TransactionDto event
) {
    public Map<String, String> getTransactionTable() {
        var table = new HashMap<String, String>();
        if (event.getOrder() == null) {
            table.put("time", event.getTime().format(DateTimeFormatter.ISO_DATE_TIME));
            table.put("created order - symbol", event.getOrder().symbol());
            table.put("created order - action", event.getOrder().action().name());
            table.put("created order - origin", event.getOrder().origin().name());
            table.put("created order - qtd", String.valueOf(event.getOrder().quantity()));
        }
        if (event.getFilledOrder() != null) {
            table.put("Order filled - symbol", event.getFilledOrder().symbol());
            table.put("Order filled - action", event.getFilledOrder().action().name());
            table.put("Order filled - price with spread", event.getFilledOrder().priceWithSpread().toPlainString());
        }
        return table;
    }
}
