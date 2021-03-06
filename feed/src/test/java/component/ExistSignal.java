package component;


import com.apssouza.mytrade.feed.api.FeedBuilder;
import com.apssouza.mytrade.feed.api.FeedModule;
import com.apssouza.mytrade.feed.api.SignalDto;

import org.junit.Assert;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static java.time.LocalDate.of;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ExistSignal {

    private FeedModule feed;
    private List<SignalDto> signals;

    @Given("^that exists signal to a give time and system name$")
    public void thatExistsSignalToAGiveTimeAndSystemName() {
        var date = of(2018, 9, 10);
        this.feed = new FeedBuilder()
                .withStartTime(LocalDateTime.of(date.minusDays(20), LocalTime.MIN))
                .withEndTime(LocalDateTime.of(date.plusDays(6), LocalTime.MIN))
                .withSignalName("system-test")
                .build();
    }

    @When("^try to retrieve signal to \"([^\"]*)\" and (\\d+)-(\\d+)-(\\d+) (\\d+):(\\d+):(\\d+)$")
    public void tryToRetrieveSignalToAnd( String systemName, int year, int month, int day, int hour, int minute, int second) {
        LocalDateTime dateTime = LocalDateTime.of(year, month, day, hour, minute, second);
        this.signals = feed.getSignal(systemName, dateTime);
    }

    @Then("^return (\\d+) signal$")
    public void returnSignal(int total) {
        Assert.assertEquals(total, signals.size());
    }

    @Then("returned signal is {string}")
    public void returnedSignalIs(String action) {
        Assert.assertEquals(action, signals.get(0).action());
    }
}
