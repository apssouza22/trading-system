package component;


import com.apssouza.mytrade.trading.api.CycleHistoryDto;
import com.apssouza.mytrade.trading.api.ExecutionType;
import com.apssouza.mytrade.trading.api.ForexBuilder;
import com.apssouza.mytrade.trading.api.ForexEngine;
import com.apssouza.mytrade.trading.api.SessionType;
import com.apssouza.mytrade.trading.domain.forex.order.OrderDto;

import org.junit.Assert;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class HistoryWithTransactions {

    private ForexEngine engine;
    private List<CycleHistoryDto> sessionHistory;

    @Given("^2 buy signals and 1 sell signal$")
    public void startRunningTheEngine() {
        var date = LocalDateTime.of(2021, 9, 10, 10, 0, 0);

        var systemName = "signal_test";
        LocalDateTime start = date.minusDays(1);
        LocalDateTime end = date;

        var feedBuilder = new FeedMockBuilder();
        feedBuilder.withPrice(start);
        feedBuilder.withSignal(start.plusSeconds(1),"BUY");
        feedBuilder.withSignal(start.plusSeconds(2),"SELL");
        feedBuilder.withSignal(start.plusSeconds(3),"BUY");

        engine = new ForexBuilder()
                .withSystemName(systemName)
                .withStartTime(start)
                .withEndTime(end)
                .withEquity(BigDecimal.valueOf(100000L))
                .withSessionType(SessionType.BACK_TEST)
                .withExecutionType(ExecutionType.SIMULATED)
                .withFeed(feedBuilder.build())
                .build();

        engine.start();
    }

    @When("^retrieving session history$")
    public void retrieveSessionHistory() {
        this.sessionHistory = engine.getHistory();
    }

    @Then("^return a history with (\\d+) transactions$")
    public void countTransactions(int total) {
        Assert.assertEquals(total, sessionHistory.size());
    }

    @Then("^history should contain 2 buys and 1 sell orders$")
    public void countOrders() {
        Assert.assertEquals(OrderDto.OrderAction.BUY, sessionHistory.get(0).transactions().get(0).transaction().getOrder().action());
        Assert.assertEquals(OrderDto.OrderAction.SELL, sessionHistory.get(1).transactions().get(0).transaction().getOrder().action());
        Assert.assertEquals(OrderDto.OrderAction.BUY, sessionHistory.get(2).transactions().get(0).transaction().getOrder().action());
    }

}
