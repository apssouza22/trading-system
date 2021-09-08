package com.apssouza.mytrade.trading.api;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public record TransactionDto(com.apssouza.mytrade.trading.domain.forex.session.TransactionDto transaction
) {
    public Map<String, String> getTransactionTable() {
        var table = new HashMap<String, String>();
        table.put("time", transaction.getTime().format(DateTimeFormatter.ISO_DATE_TIME));
        table.put("created order - symbol", transaction.getOrder().symbol());
        table.put("created order - action", transaction.getOrder().action().name());
        table.put("created order - origin", transaction.getOrder().origin().name());
        table.put("created order - qtd", String.valueOf(transaction.getOrder().quantity()));
        table.put("Order filled - symbol", transaction.getFilledOrder().symbol());
        table.put("Order filled - action", transaction.getFilledOrder().action().name());
        table.put("Order filled - price with spread", transaction.getFilledOrder().priceWithSpread().toPlainString());
        return table;
    }
}
