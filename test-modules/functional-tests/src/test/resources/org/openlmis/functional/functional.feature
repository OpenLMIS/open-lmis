Feature: Gradle-Cucumber integration

  @smoke
  Scenario: results are shown
    Given Data setup done
    And User is on forgot password screen
    When Enter email="John_Doe@openlmis.com" and username="Admin123"
    And Click submit button
    Then Verify email send successfully

