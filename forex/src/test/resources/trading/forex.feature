Feature: Get trade signals
  Test the ability to retrieve trade signals by date time

  Scenario: Test get history when exists transactions
    Given 2 buy signals and 1 sell signal
    When retrieving session history
    Then return a history with 3 transactions

  Scenario: Test get history when not exists transactions
    Given that no signal are given
    When retrieving session history.
    Then return a history with no transactions
