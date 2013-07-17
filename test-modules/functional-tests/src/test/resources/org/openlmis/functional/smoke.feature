Feature: Smoke Tests

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

  @smoke
  Scenario: Should view requisition and regimen after authorization
    Given I have the following data including regimen configured:
      | HIV | storeincharge | ADULTS | Admin123 | RegimenCode1 | RegimenName1 | RegimenCode2 | RegimenName2 |
    And I access Initiate RnR page having regimen data
    When I click proceed for view requisition
    And I populate RnR data
    And I populate Regimen data as patientsOnTreatment "100" patientsToInitiateTreatment "200" patientsStoppedTreatment "300" remarks "Regimens data filled"
    And I access home page
    And I access view RnR screen
    Then I should see elements on view requisition page
    When I update requisition status to "SUBMITTED"
    And I type view search criteria
    And I click search
    Then I should see no requisition found message
    When I update approved quantity "10"
    And I update requisition status to "AUTHORIZED"
    And I click search
    Then I should see requisition status as "AUTHORIZED"
    When I click RnR List
    Then I verify total field
    When I access regimen tab for view requisition
    Then I verify values on regimen page as patientsOnTreatment "100" patientsToInitiateTreatment "200" patientsStoppedTreatment "300" remarks "Regimens data filled"

  @smoke
  Scenario: Verifying Forgot Password functionality
    Given I am on forgot password screen
    When I type email "John_Doe@openlmis.com"
    And I type and username "Admin123"
    When I click submit button
    Then I should see email send successfully