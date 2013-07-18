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
  Scenario: Should configure program product ISA
    Given I have the following data for ISA:
      | Admin123 | Admin123 | VACCINES |
    And I access program product ISA page
    When I type ratio "3.9" dosesPerYear "3" wastage "10" bufferPercentage "25" adjustmentValue "0" minimumValue "10" maximumValue "1000"
    Then I verify calculated ISA value having population "1000" ratio "3.9" dosesPerYear "3" wastage "10" bufferPercentage "25" adjustmentValue "0" minimumValue "10" maximumValue "1000"
    And I click cancel
    And I access home page

  @smoke
  Scenario: Should initiate distribution
    Given I have the following data for distribution:
      | userSIC       | password | deliveryZoneCodeFirst | deliveryZoneCodeSecond | deliveryZoneNameFirst | deliveryZoneNameSecond | facilityCodeFirst | facilityCodeSecond | programFirst | programSecond | schedule |
      | storeincharge | Admin123 | DZ1                   | DZ2                    | Delivery Zone First   | Delivery Zone Second   | F10               | F11                | VACCINES     | TB            | M        |
    And I access plan my distribution page
    When I select delivery zone "Delivery Zone First"
    And I select program "VACCINES"
    And I select period "Period14"
    And I initiate distribution
    Then I should see data download successfully
    And I should see delivery zone "Delivery Zone First" program "VACCINES" period "Period14" in table

  @smoke
  Scenario: Verifying Forgot Password functionality
    Given I am on forgot password screen
    When I type email "John_Doe@openlmis.com"
    And I type and username "Admin123"
    When I click submit button
    Then I should see email send successfully
    And I am logged in as Admin