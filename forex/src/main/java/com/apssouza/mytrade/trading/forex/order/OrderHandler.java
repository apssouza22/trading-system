package com.apssouza.mytrade.trading.forex.order;

import com.apssouza.mytrade.feed.signal.SignalDto;
import com.apssouza.mytrade.trading.forex.feed.price.PriceFeed;
import com.apssouza.mytrade.trading.forex.portfolio.Portfolio;
import com.apssouza.mytrade.trading.forex.portfolio.Position;
import com.apssouza.mytrade.trading.forex.portfolio.PositionType;
import com.apssouza.mytrade.trading.forex.risk.PositionSizer;
import com.apssouza.mytrade.trading.forex.session.event.SignalCreatedEvent;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.logging.Logger;

public class OrderHandler {
    private final MemoryOrderDao orderDao;
    private final PositionSizer positionSizer;
    private final BigDecimal equity;
    private final PriceFeed priceHandler;
    private final Portfolio portfolio;

    private static Logger log = Logger.getLogger(OrderHandler.class.getName());

    public OrderHandler(
            MemoryOrderDao orderDao,
            PositionSizer positionSizer,
            BigDecimal equity,
            PriceFeed priceHandler,
            Portfolio portfolio
    ) {
        this.orderDao = orderDao;
        this.positionSizer = positionSizer;
        this.equity = equity;
        this.priceHandler = priceHandler;
        this.portfolio = portfolio;
    }

    public OrderDto createOrderFromClosedPosition(Position position, LocalDateTime time) {
        OrderAction action = position.getPositionType().equals(PositionType.LONG) ? OrderAction.SELL : OrderAction.BUY;
        return new OrderDto(
                position.getSymbol(),
                action, position.getQuantity(),
                OrderOrigin.EXITS,
                time,
                position.getIdentifier(),
                OrderStatus.CREATED
        );
    }

    public void persist(OrderDto order) {
        orderDao.persist(order);
    }


    public void updateOrderStatus(Integer id, OrderStatus status) {
        orderDao.updateStatus(id, status);
    }

    public OrderDto createOrderFromSignal(SignalCreatedEvent event) {
        LocalDateTime time = event.getTimestamp();
        SignalDto signal = event.getSignal();
        String action = signal.getAction();

        OrderDto order = new OrderDto(
                signal.getSymbol(),
                OrderAction.valueOf(action.toUpperCase()),
                positionSizer.getQuantity(),
                OrderOrigin.SIGNAL,
                time,
                "",
                OrderStatus.CREATED
        );
        log.info("Created order: " + order.toString());
        return order;
    }


    public Optional<OrderDto> getOrderById(Integer id) {
        return orderDao.getOrderById(id);
    }
}
