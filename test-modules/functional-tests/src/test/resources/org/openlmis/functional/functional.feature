Feature: Gradle-Cucumber integration
  @smoke
  Scenario: results are shown
    Given I am on forgot password screen
    When I type email "John_Doe@openlmis.com"
    And I type and username "Admin123"
    When I click submit button
    Then I should see email send successfully

