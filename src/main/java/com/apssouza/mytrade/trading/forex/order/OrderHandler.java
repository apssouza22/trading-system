package com.apssouza.mytrade.trading.forex.order;

import com.apssouza.mytrade.feed.price.PriceHandler;
import com.apssouza.mytrade.trading.forex.portfolio.Portfolio;
import com.apssouza.mytrade.trading.forex.portfolio.Position;
import com.apssouza.mytrade.trading.forex.portfolio.PositionType;
import com.apssouza.mytrade.trading.forex.risk.PositionSizer;
import com.apssouza.mytrade.trading.forex.statistics.TransactionState;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderHandler {
    private final MemoryOrderDao orderDao;
    private final PositionSizer positionSizer;
    private final BigDecimal equity;
    private final PriceHandler priceHandler;
    private final Portfolio portfolio;

    public OrderHandler(
            MemoryOrderDao orderDao,
            PositionSizer positionSizer,
            BigDecimal equity,
            PriceHandler priceHandler,
            Portfolio portfolio
    ) {

        this.orderDao = orderDao;
        this.positionSizer = positionSizer;
        this.equity = equity;
        this.priceHandler = priceHandler;
        this.portfolio = portfolio;
    }

    public OrderDto createOrderFromClosedPosition(Position position, LocalDateTime time) {
       OrderAction action=  position.getPositionType().equals(PositionType.LONG)? OrderAction.SELL: OrderAction.BUY;
        return new OrderDto(
                position.getSymbol(),
                action, position.getQuantity(),
                TransactionState.EXIT,
                time,
                position.getIdentifier(),
                OrderStatus.CREATED
        );
    }

    public void persist(OrderDto order) {
        orderDao.persist(order);
    }


    public void updateOrdeStatus(Integer id, OrderStatus status){
        orderDao.updateStatus(id, status);
    }
}
