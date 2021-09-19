package moduletest;


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

public class DoesntExistSignal {
    private FeedModule feed;
    private List<SignalDto> signals;

    @Given("that exists signal to the given time but not for the given system")
    public void that_exists_signal_to_the_given_time_but_not_for_the_given_system() {
        var date = of(2018, 9, 10);
        this.feed = new FeedBuilder()
                .withStartTime(LocalDateTime.of(date.minusDays(20), LocalTime.MIN))
                .withEndTime(LocalDateTime.of(date.plusDays(6), LocalTime.MIN))
                .withSignalName("system-test")
                .build();

    }

    @When("^try to retrieve signal to system \"([^\"]*)\" and (\\d+)-(\\d+)-(\\d+) (\\d+):(\\d+):(\\d+)$")
    public void tryToRetrieveSignalToAnd(String systemName, int year, int month, int day, int hour, int minute, int second){
        this.signals = feed.getSignal(systemName, LocalDateTime.of(year, month, day, hour, minute, second));
    }

    @Then("return no signal")
    public void return_no_signal() {
        Assert.assertEquals(0, signals.size());
    }
}
