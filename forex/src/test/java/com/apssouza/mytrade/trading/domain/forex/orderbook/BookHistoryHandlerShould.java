package com.apssouza.mytrade.trading.domain.forex.orderbook;

import com.apssouza.mytrade.trading.domain.forex.portfolio.FilledOrderBuilder;
import com.apssouza.mytrade.trading.domain.forex.order.OrderBuilder;
import com.apssouza.mytrade.trading.domain.forex.portfolio.PositionBuilder;
import com.apssouza.mytrade.trading.domain.forex.order.OrderDto;
import com.apssouza.mytrade.trading.domain.forex.portfolio.FilledOrderDto;
import com.apssouza.mytrade.trading.domain.forex.portfolio.Position;
import com.apssouza.mytrade.trading.domain.forex.session.CycleHistory;
import com.apssouza.mytrade.trading.domain.forex.session.TransactionDto;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class BookHistoryHandlerShould extends TestCase {

    private BookHistoryHandler bookHistoryHandler;
    private TransactionsExporter transactionsExporter;


    @Before
    public  void setUp(){
        this.transactionsExporter = mock(TransactionsExporter.class);
        this.bookHistoryHandler = new BookHistoryHandler(transactionsExporter);
    }

    @Test
    public void getTransactions() {
        List<CycleHistory> transactions = bookHistoryHandler.getTransactions();
        assertTrue(transactions.isEmpty());
    }

    @Test
    public void startCycle() {
        bookHistoryHandler.startCycle(LocalDateTime.MIN);
        bookHistoryHandler.endCycle();
        List<CycleHistory> transactions = bookHistoryHandler.getTransactions();
        CycleHistory cycleHistory = transactions.get(0);
        assertEquals(LocalDateTime.MIN, cycleHistory.getTime());
    }

    @Test
    public void endCycle() {
        bookHistoryHandler.endCycle();
        List<CycleHistory> transactions = bookHistoryHandler.getTransactions();
        assertEquals(1, transactions.size());
    }

    @Test
    public void setState() {
        bookHistoryHandler.startCycle(LocalDateTime.MIN);
        bookHistoryHandler.setState(TransactionDto.TransactionState.EXIT,"AUDUSD");
        bookHistoryHandler.endCycle();
        List<CycleHistory> transactions = bookHistoryHandler.getTransactions();
        CycleHistory cycleHistory = transactions.get(0);
        assertEquals(TransactionDto.TransactionState.EXIT, cycleHistory.getTransactions().get("AUDUSD").getState());
    }

    @Test
    public void addPosition() {
        bookHistoryHandler.startCycle(LocalDateTime.MIN);
        PositionBuilder positionBuilder = new PositionBuilder();
        Position position = positionBuilder.build();
        bookHistoryHandler.addPosition(position);
        bookHistoryHandler.endCycle();
        List<CycleHistory> transactions = bookHistoryHandler.getTransactions();
        CycleHistory cycleHistory = transactions.get(0);
        assertEquals(position, cycleHistory.getTransactions().get("AUDUSD").getPosition());
    }

    @Test
    public void addOrderFilled() {
        bookHistoryHandler.startCycle(LocalDateTime.MIN);
        FilledOrderDto orderDto =  new FilledOrderBuilder().build();
        bookHistoryHandler.addOrderFilled(orderDto);
        bookHistoryHandler.endCycle();
        List<CycleHistory> transactions = bookHistoryHandler.getTransactions();
        CycleHistory cycleHistory = transactions.get(0);
        assertEquals(orderDto, cycleHistory.getTransactions().get("AUDUSD").getFilledOrder());
    }

    @Test
    public void addOrder() {
        bookHistoryHandler.startCycle(LocalDateTime.MIN);
        OrderDto orderDto =  new OrderBuilder().build();
        bookHistoryHandler.addOrder(orderDto);
        bookHistoryHandler.endCycle();
        List<CycleHistory> transactions = bookHistoryHandler.getTransactions();
        CycleHistory cycleHistory = transactions.get(0);
        assertEquals(orderDto, cycleHistory.getTransactions().get("AUDUSD").getOrder());
    }


    @Test
    public void addOrderToDifferentCpair() {
        bookHistoryHandler.startCycle(LocalDateTime.MIN);
        OrderBuilder orderBuilder = new OrderBuilder();
        OrderDto orderDto =  orderBuilder.build();
        OrderDto orderDto2 =  orderBuilder.withIdentifier("EURUSD").withSymbol("EURUSD").build();
        bookHistoryHandler.addOrder(orderDto);
        bookHistoryHandler.addOrder(orderDto2);
        bookHistoryHandler.endCycle();
        List<CycleHistory> transactions = bookHistoryHandler.getTransactions();
        CycleHistory cycleHistory = transactions.get(0);
        assertEquals(orderDto, cycleHistory.getTransactions().get("AUDUSD").getOrder());
        assertEquals(orderDto2, cycleHistory.getTransactions().get("EURUSD").getOrder());
    }

    @Test
    public void export() throws IOException {
        bookHistoryHandler.export("files/test.csv");
        verify(transactionsExporter,atMostOnce()).exportCsv(any(), any());
    }
}