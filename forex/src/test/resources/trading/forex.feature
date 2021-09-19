Feature: Get session history
  Test the ability to retrieve the trade session history

  Scenario: Test get history when exists transactions
    Given 2 buy signals and 1 sell signal
    When retrieving session history
    Then return a history with 3 transactions
    Then history should contain 2 buys and 1 sell orders

