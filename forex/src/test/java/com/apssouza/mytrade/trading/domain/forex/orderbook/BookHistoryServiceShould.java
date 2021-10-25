package com.apssouza.mytrade.trading.domain.forex.orderbook;

import com.apssouza.mytrade.trading.domain.forex.portfolio.FilledOrderBuilder;
import com.apssouza.mytrade.trading.domain.forex.order.OrderBuilder;
import com.apssouza.mytrade.trading.domain.forex.portfolio.PositionBuilder;
import com.apssouza.mytrade.trading.domain.forex.order.OrderDto;
import com.apssouza.mytrade.trading.domain.forex.portfolio.FilledOrderDto;
import com.apssouza.mytrade.trading.domain.forex.portfolio.PositionDto;

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
    public void return_transactions_from_the_history() {
        List<CycleHistoryDto> transactions = bookHistoryService.getTransactions();
        assertTrue(transactions.isEmpty());
    }

    @Test
    public void start_history_cycle() {
        bookHistoryService.startCycle(LocalDateTime.MIN);
        bookHistoryService.endCycle();
        List<CycleHistoryDto> transactions = bookHistoryService.getTransactions();
        CycleHistoryDto cycleHistory = transactions.get(0);
        assertEquals(LocalDateTime.MIN, cycleHistory.getTime());
    }

    @Test
    public void end_history_cycle() {
        bookHistoryService.endCycle();
        List<CycleHistoryDto> transactions = bookHistoryService.getTransactions();
        assertEquals(1, transactions.size());
    }

    @Test
    public void set_state_of_transaction() {
        bookHistoryService.startCycle(LocalDateTime.MIN);
        bookHistoryService.setState(TransactionDto.TransactionState.EXIT,"AUDUSD");
        bookHistoryService.endCycle();
        List<CycleHistoryDto> transactions = bookHistoryService.getTransactions();
        CycleHistoryDto cycleHistory = transactions.get(0);
        assertEquals(TransactionDto.TransactionState.EXIT, cycleHistory.getTransactions().get("AUDUSD").getState());
    }

    @Test
    public void add_new_position_to_history() {
        bookHistoryService.startCycle(LocalDateTime.MIN);
        PositionBuilder positionBuilder = new PositionBuilder();
        PositionDto position = positionBuilder.build();
        bookHistoryService.addPosition(position);
        bookHistoryService.endCycle();
        List<CycleHistoryDto> transactions = bookHistoryService.getTransactions();
        CycleHistoryDto cycleHistory = transactions.get(0);
        assertEquals(position, cycleHistory.getTransactions().get("AUDUSD").getPosition());
    }

    @Test
    public void add_new_order_filled_to_history() {
        bookHistoryService.startCycle(LocalDateTime.MIN);
        FilledOrderDto orderDto =  new FilledOrderBuilder().build();
        bookHistoryService.addOrderFilled(orderDto);
        bookHistoryService.endCycle();
        List<CycleHistoryDto> transactions = bookHistoryService.getTransactions();
        CycleHistoryDto cycleHistory = transactions.get(0);
        assertEquals(orderDto, cycleHistory.getTransactions().get("AUDUSD").getFilledOrder());
    }

    @Test
    public void add_new_order_to_history() {
        bookHistoryService.startCycle(LocalDateTime.MIN);
        OrderDto orderDto =  new OrderBuilder().build();
        bookHistoryService.addOrder(orderDto);
        bookHistoryService.endCycle();
        List<CycleHistoryDto> transactions = bookHistoryService.getTransactions();
        CycleHistoryDto cycleHistory = transactions.get(0);
        assertEquals(orderDto, cycleHistory.getTransactions().get("AUDUSD").getOrder());
    }


    @Test
    public void add_new_order_to_different_currency_pair() {
        bookHistoryService.startCycle(LocalDateTime.MIN);
        OrderBuilder orderBuilder = new OrderBuilder();
        OrderDto orderDto =  orderBuilder.build();
        OrderDto orderDto2 =  orderBuilder.withIdentifier("EURUSD").withSymbol("EURUSD").build();
        bookHistoryService.addOrder(orderDto);
        bookHistoryService.addOrder(orderDto2);
        bookHistoryService.endCycle();
        List<CycleHistoryDto> transactions = bookHistoryService.getTransactions();
        CycleHistoryDto cycleHistory = transactions.get(0);
        assertEquals(orderDto, cycleHistory.getTransactions().get("AUDUSD").getOrder());
        assertEquals(orderDto2, cycleHistory.getTransactions().get("EURUSD").getOrder());
    }

    @Test
    public void export_history_to_csv() throws IOException {
        bookHistoryService.export("files/test.csv");
        verify(transactionsExporter,atMostOnce()).exportCsv(any(), any());
    }
}