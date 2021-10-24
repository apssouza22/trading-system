package com.apssouza.mytrade.trading.domain.forex.orderbook;

import com.apssouza.mytrade.trading.domain.forex.portfolio.FilledOrderBuilder;
import com.apssouza.mytrade.trading.domain.forex.order.OrderBuilder;
import com.apssouza.mytrade.trading.domain.forex.portfolio.PositionBuilder;
import com.apssouza.mytrade.trading.domain.forex.order.OrderDto;
import com.apssouza.mytrade.trading.domain.forex.portfolio.FilledOrderDto;
import com.apssouza.mytrade.trading.domain.forex.portfolio.PositionDto;
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
public class BookHistoryServiceShould extends TestCase {

    private BookHistoryService bookHistoryService;
    private TransactionsExporter transactionsExporter;


    @Before
    public  void setUp(){
        this.transactionsExporter = mock(TransactionsExporter.class);
        this.bookHistoryService = new BookHistoryService(transactionsExporter);
    }

    @Test
    public void getTransactions() {
        List<CycleHistory> transactions = bookHistoryService.getTransactions();
        assertTrue(transactions.isEmpty());
    }

    @Test
    public void startCycle() {
        bookHistoryService.startCycle(LocalDateTime.MIN);
        bookHistoryService.endCycle();
        List<CycleHistory> transactions = bookHistoryService.getTransactions();
        CycleHistory cycleHistory = transactions.get(0);
        assertEquals(LocalDateTime.MIN, cycleHistory.getTime());
    }

    @Test
    public void endCycle() {
        bookHistoryService.endCycle();
        List<CycleHistory> transactions = bookHistoryService.getTransactions();
        assertEquals(1, transactions.size());
    }

    @Test
    public void setState() {
        bookHistoryService.startCycle(LocalDateTime.MIN);
        bookHistoryService.setState(TransactionDto.TransactionState.EXIT,"AUDUSD");
        bookHistoryService.endCycle();
        List<CycleHistory> transactions = bookHistoryService.getTransactions();
        CycleHistory cycleHistory = transactions.get(0);
        assertEquals(TransactionDto.TransactionState.EXIT, cycleHistory.getTransactions().get("AUDUSD").getState());
    }

    @Test
    public void addPosition() {
        bookHistoryService.startCycle(LocalDateTime.MIN);
        PositionBuilder positionBuilder = new PositionBuilder();
        PositionDto position = positionBuilder.build();
        bookHistoryService.addPosition(position);
        bookHistoryService.endCycle();
        List<CycleHistory> transactions = bookHistoryService.getTransactions();
        CycleHistory cycleHistory = transactions.get(0);
        assertEquals(position, cycleHistory.getTransactions().get("AUDUSD").getPosition());
    }

    @Test
    public void addOrderFilled() {
        bookHistoryService.startCycle(LocalDateTime.MIN);
        FilledOrderDto orderDto =  new FilledOrderBuilder().build();
        bookHistoryService.addOrderFilled(orderDto);
        bookHistoryService.endCycle();
        List<CycleHistory> transactions = bookHistoryService.getTransactions();
        CycleHistory cycleHistory = transactions.get(0);
        assertEquals(orderDto, cycleHistory.getTransactions().get("AUDUSD").getFilledOrder());
    }

    @Test
    public void addOrder() {
        bookHistoryService.startCycle(LocalDateTime.MIN);
        OrderDto orderDto =  new OrderBuilder().build();
        bookHistoryService.addOrder(orderDto);
        bookHistoryService.endCycle();
        List<CycleHistory> transactions = bookHistoryService.getTransactions();
        CycleHistory cycleHistory = transactions.get(0);
        assertEquals(orderDto, cycleHistory.getTransactions().get("AUDUSD").getOrder());
    }


    @Test
    public void addOrderToDifferentCpair() {
        bookHistoryService.startCycle(LocalDateTime.MIN);
        OrderBuilder orderBuilder = new OrderBuilder();
        OrderDto orderDto =  orderBuilder.build();
        OrderDto orderDto2 =  orderBuilder.withIdentifier("EURUSD").withSymbol("EURUSD").build();
        bookHistoryService.addOrder(orderDto);
        bookHistoryService.addOrder(orderDto2);
        bookHistoryService.endCycle();
        List<CycleHistory> transactions = bookHistoryService.getTransactions();
        CycleHistory cycleHistory = transactions.get(0);
        assertEquals(orderDto, cycleHistory.getTransactions().get("AUDUSD").getOrder());
        assertEquals(orderDto2, cycleHistory.getTransactions().get("EURUSD").getOrder());
    }

    @Test
    public void export() throws IOException {
        bookHistoryService.export("files/test.csv");
        verify(transactionsExporter,atMostOnce()).exportCsv(any(), any());
    }
}