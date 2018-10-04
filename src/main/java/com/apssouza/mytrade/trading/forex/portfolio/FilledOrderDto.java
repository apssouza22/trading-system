package com.apssouza.mytrade.trading.forex.portfolio;

import com.apssouza.mytrade.trading.forex.order.OrderAction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FilledOrderDto {
    private final LocalDateTime time;
    private final String symbol;
    private final OrderAction action;
    private final int quantity;
    private final BigDecimal priceWithSpread;
    private final String identifier;
    private final Integer id;

    public FilledOrderDto(
            LocalDateTime time,
            String symbol,
            OrderAction action,
            int quantity,
            BigDecimal priceWithSpread,
            String identifier,
            Integer id
    ) {


        this.time = time;
        this.symbol = symbol;
        this.action = action;
        this.quantity = quantity;
        this.priceWithSpread = priceWithSpread;
        this.identifier = identifier;
        this.id = id;
    }

    public FilledOrderDto(int quantity, FilledOrderDto filledOrderDto) {
        this(
                filledOrderDto.getTime(),
                filledOrderDto.getSymbol(),
                filledOrderDto.getAction(),
                quantity,
                filledOrderDto.getPriceWithSpread(),
                filledOrderDto.getIdentifier(),
                filledOrderDto.getId()
        );
    }

    public LocalDateTime getTime() {
        return time;
    }

    public String getSymbol() {
        return symbol;
    }

    public OrderAction getAction() {
        return action;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getPriceWithSpread() {
        return priceWithSpread;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Integer getId() {
        return id;
    }
}
