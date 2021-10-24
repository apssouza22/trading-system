package com.apssouza.mytrade.trading.domain.forex.order;

import com.apssouza.mytrade.feed.api.SignalDto;
import com.apssouza.mytrade.trading.domain.forex.portfolio.Position;
import com.apssouza.mytrade.trading.domain.forex.risk.RiskManagementService;
import com.apssouza.mytrade.trading.domain.forex.feed.signalfeed.SignalCreatedEvent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

public class OrderService {
    private final OrderDao orderDao;
    private final RiskManagementService riskManagementService;

    private static Logger log = Logger.getLogger(OrderService.class.getName());

    public OrderService(
            OrderDao orderDao,
            RiskManagementService riskManagementService
    ) {
        this.orderDao = orderDao;
        this.riskManagementService = riskManagementService;
    }

    public OrderDto createOrderFromClosedPosition(Position position, LocalDateTime time) {
        OrderDto.OrderAction action = position.getPositionType().equals(Position.PositionType.LONG) ? OrderDto.OrderAction.SELL : OrderDto.OrderAction.BUY;
        return new OrderDto(
                position.getSymbol(),
                action, position.getQuantity(),
                OrderDto.OrderOrigin.EXITS,
                time,
                position.getIdentifier(),
                OrderDto.OrderStatus.CREATED
        );
    }

    public OrderDto persist(OrderDto order) {
        return orderDao.persist(order);
    }


    public boolean updateOrderStatus(Integer id, OrderDto.OrderStatus status) {
        return orderDao.updateStatus(id, status);
    }

    public OrderDto createOrderFromSignal(SignalCreatedEvent event) {
        LocalDateTime time = event.getTimestamp();
        SignalDto signal = event.getSignal();
        String action = signal.action();

        OrderDto order = new OrderDto(
                signal.symbol(),
                OrderDto.OrderAction.valueOf(action.toUpperCase()),
                riskManagementService.getPositionSize(),
                OrderDto.OrderOrigin.SIGNAL,
                time,
                "",
                OrderDto.OrderStatus.CREATED
        );
        return order;
    }

    public List<OrderDto> getOrderByStatus(OrderDto.OrderStatus status) {
        return orderDao.getOrderByStatus(status);
    }

}
