Feature: Gradle-Cucumber integration
  @smoke
  Scenario: Verifying Forgot Password functionality
    Given I am on forgot password screen
    When I type email "John_Doe@openlmis.com"
    And I type and username "Admin123"
    When I click submit button
    Then I should see email send successfully

  @smoke
  Scenario: Should save and submit regimen data
    Given I have the following data:
      | HIV | storeincharge | ADULTS | Admin123 | RegimenCode1 | RegimenName1 | RegimenCode2 | RegimenName2 |
    And I access Initiate RnR page
    When I click proceed
    And I populate RnR data
    And I click proceed
    And I access regimen tab
    Then I should see regimen fields
    When I type patients on treatment "100"
    And I type patients initiated treatment "100"
    And I type patients stopped treatment "100"
    And I type remarks "Regimens data filled"
    And I click save
    Then I should see saved successfully
    When I click submit
    And I click ok
    Then I should see submit successfully

