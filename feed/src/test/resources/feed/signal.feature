Feature: Get trade signals
  Test the ability to retrieve trade signals by date time

  Scenario: Test get signals when exists signals
    Given that exists signal to a give time and system name
    When try to retrieve signal to "system-test" and 2020-05-01 10:10:10
    Then return 1 signal

  Scenario: Test get signals when doesn't exist signal to the given system name
    Given that exists signal to the given time but not for the given system
    When try to retrieve signal to system "system-name" and 2020-05-01 10:10:11
    Then return no signal
