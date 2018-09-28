package com.apssouza.mytrade.trading.forex.order;

public class OrderDto {
    public final String positionIdentifier;

    public OrderDto(String identifierFromOrder) {
        this.positionIdentifier = identifierFromOrder;
    }
}
