package com.apssouza.mytrade.trading.domain.forex.statistics;

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
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class HistoryBookHandlerTest extends TestCase {

    private HistoryBookHandler historyBookHandler;

    @Mock
    TransactionsExporter transactionsExporter;

    @Before
    public  void setUp(){
//        Mockito.when(transactionsExporter.exportCsv(Mockito.anyList(), Mockito.anyString()))
        this.historyBookHandler = new HistoryBookHandler(transactionsExporter);
    }

    @Test
    public void getTransactions() {
        List<CycleHistory> transactions = historyBookHandler.getTransactions();
        assertTrue(transactions.isEmpty());
    }

    @Test
    public void startCycle() {
        historyBookHandler.startCycle(LocalDateTime.MIN);
        historyBookHandler.endCycle();
        List<CycleHistory> transactions = historyBookHandler.getTransactions();
        CycleHistory cycleHistory = transactions.get(0);
        assertEquals(LocalDateTime.MIN, cycleHistory.getTime());
    }

    @Test
    public void endCycle() {
        historyBookHandler.endCycle();
        List<CycleHistory> transactions = historyBookHandler.getTransactions();
        assertEquals(1, transactions.size());
    }

    @Test
    public void setState() {
        historyBookHandler.startCycle(LocalDateTime.MIN);
        historyBookHandler.setState(TransactionDto.TransactionState.EXIT,"AUDUSD");
        historyBookHandler.endCycle();
        List<CycleHistory> transactions = historyBookHandler.getTransactions();
        CycleHistory cycleHistory = transactions.get(0);
        assertEquals(TransactionDto.TransactionState.EXIT, cycleHistory.getTransactions().get("AUDUSD").getState());
    }

    @Test
    public void addPosition() {
        historyBookHandler.startCycle(LocalDateTime.MIN);
        PositionBuilder positionBuilder = new PositionBuilder();
        Position position = positionBuilder.build();
        historyBookHandler.addPosition(position);
        historyBookHandler.endCycle();
        List<CycleHistory> transactions = historyBookHandler.getTransactions();
        CycleHistory cycleHistory = transactions.get(0);
        assertEquals(position, cycleHistory.getTransactions().get("AUDUSD").getPosition());
    }

    @Test
    public void addOrderFilled() {
        historyBookHandler.startCycle(LocalDateTime.MIN);
        FilledOrderDto orderDto =  new FilledOrderBuilder().build();
        historyBookHandler.addOrderFilled(orderDto);
        historyBookHandler.endCycle();
        List<CycleHistory> transactions = historyBookHandler.getTransactions();
        CycleHistory cycleHistory = transactions.get(0);
        assertEquals(orderDto, cycleHistory.getTransactions().get("AUDUSD").getFilledOrder());
    }

    @Test
    public void addOrder() {
        historyBookHandler.startCycle(LocalDateTime.MIN);
        OrderDto orderDto =  new OrderBuilder().build();
        historyBookHandler.addOrder(orderDto);
        historyBookHandler.endCycle();
        List<CycleHistory> transactions = historyBookHandler.getTransactions();
        CycleHistory cycleHistory = transactions.get(0);
        assertEquals(orderDto, cycleHistory.getTransactions().get("AUDUSD").getOrder());
    }


    @Test
    public void addOrderToDifferentCpair() {
        historyBookHandler.startCycle(LocalDateTime.MIN);
        OrderBuilder orderBuilder = new OrderBuilder();
        OrderDto orderDto =  orderBuilder.build();
        OrderDto orderDto2 =  orderBuilder.withIdentifier("EURUSD").withSymbol("EURUSD").build();
        historyBookHandler.addOrder(orderDto);
        historyBookHandler.addOrder(orderDto2);
        historyBookHandler.endCycle();
        List<CycleHistory> transactions = historyBookHandler.getTransactions();
        CycleHistory cycleHistory = transactions.get(0);
        assertEquals(orderDto, cycleHistory.getTransactions().get("AUDUSD").getOrder());
        assertEquals(orderDto2, cycleHistory.getTransactions().get("EURUSD").getOrder());
    }

    @Test
    public void export() throws IOException {
        historyBookHandler.export("files/test.csv");
    }
}