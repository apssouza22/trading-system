package it;


import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ExistSignal {
    @Given("^that exists signal to a give time and system name$")
    public void thatExistsSignalToAGiveTimeAndSystemName() {
    }

    @When("^try to retrieve signal to \"([^\"]*)\" and (\\d+)-(\\d+)-(\\d+) (\\d+):(\\d+):(\\d+)$")
    public void tryToRetrieveSignalToAnd(String systemName, int year, int month, int day, int hour, int minute, int second){
        // Write code here that turns the phrase above into concrete actions
    }

    @Then("^return (\\d+) signal$")
    public void returnSignal(int total) {
    }
}
