package it;


import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class DoesntExistSignal {
    @Given("that exists signal to the given time but not for the given system")
    public void that_exists_signal_to_the_given_time_but_not_for_the_given_system() {
        // Write code here that turns the phrase above into concrete actions

    }

    @When("^try to retrieve signal to system \"([^\"]*)\" and (\\d+)-(\\d+)-(\\d+) (\\d+):(\\d+):(\\d+)$")
    public void tryToRetrieveSignalToAnd(String systemName, int year, int month, int day, int hour, int minute, int second){
        // Write code here that turns the phrase above into concrete actions
    }

    @Then("return no signal")
    public void return_no_signal() {

    }
}
