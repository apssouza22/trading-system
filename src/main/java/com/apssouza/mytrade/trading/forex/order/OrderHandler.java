package com.apssouza.mytrade.trading.forex.order;

import com.apssouza.mytrade.feed.price.PriceHandler;
import com.apssouza.mytrade.feed.signal.SignalDto;
import com.apssouza.mytrade.trading.forex.portfolio.Portfolio;
import com.apssouza.mytrade.trading.forex.portfolio.Position;
import com.apssouza.mytrade.trading.forex.portfolio.PositionType;
import com.apssouza.mytrade.trading.forex.risk.PositionSizer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;

public class OrderHandler {
    private final MemoryOrderDao orderDao;
    private final PositionSizer positionSizer;
    private final BigDecimal equity;
    private final PriceHandler priceHandler;
    private final Portfolio portfolio;

    private static Logger log = Logger.getLogger(OrderHandler.class.getName());

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
        OrderAction action = position.getPositionType().equals(PositionType.LONG) ? OrderAction.SELL : OrderAction.BUY;
        return new OrderDto(
                position.getSymbol(),
                action, position.getQuantity(),
                OrderOrigin.STOP_ORDER,
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

    public List<OrderDto> createOrderFromSignal(List<SignalDto> signals, LocalDateTime time) {
        if (signals.isEmpty()) {
            return Collections.emptyList();
        }
        List<OrderDto> orders = new ArrayList<>();

        for (SignalDto signal : signals) {
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
            log.info("Created order: " + orders.toString());
            orders.add(order);
        }
        return orders;
    }


}
