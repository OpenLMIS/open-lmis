Feature: Smoke Tests

  @smoke
  Scenario: Should save and submit regimen data
    Given I have the following data:
      | HIV | storeincharge | ADULTS | RegimenCode1 | RegimenName1 | RegimenCode2 | RegimenName2 |
    And I have "storeincharge" user with "CREATE_REQUISITION,VIEW_REQUISITION" rights and data to initiate requisition
    And I have regimen template configured
    And I am logged in as "storeincharge"
    And I access initiate requisition page
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
      | userSIC        | deliveryZoneCodeFirst | deliveryZoneCodeSecond | deliveryZoneNameFirst | deliveryZoneNameSecond | facilityCodeFirst | facilityCodeSecond | programFirst | programSecond | schedule |
      | storeincharge  | DZ1                   | DZ2                    | Delivery Zone First   | Delivery Zone Second   | F10               | F11                | VACCINES     | TB            | M        |
    And I assign delivery zone "DZ1" to user "storeincharge" having role "store in-charge"
    And I am logged in as "storeincharge"
    And I access plan my distribution page
    When I select delivery zone "Delivery Zone First"
    And I select program "VACCINES"
    And I select period "Period14"
    And I initiate distribution
    Then I should see data download successfully
    And I should see delivery zone "Delivery Zone First" program "VACCINES" period "Period14" in table

  @smoke
  Scenario: Should fetch program period on manage distribution screen
    Given I have the following data for distribution:
      | userSIC        | deliveryZoneCodeFirst | deliveryZoneCodeSecond | deliveryZoneNameFirst | deliveryZoneNameSecond | facilityCodeFirst | facilityCodeSecond | programFirst | programSecond | schedule |
      | storeincharge  | DZ1                   | DZ2                    | Delivery Zone First   | Delivery Zone Second   | F10               | F11                | VACCINES     | TB            | M        |
    And I am logged in as "storeincharge"
    And I access plan my distribution page
    Then I verify fields
    And I should see deliveryZone "--None Assigned--"
    When I assign delivery zone "DZ1" to user "storeincharge" having role "store in-charge"
    And I access plan my distribution page
    And I select delivery zone "Delivery Zone First"
    Then I should see program "VACCINES"
    And I select program "VACCINES"
    Then I should see period "Period14"
    And I click view load amount

  @smoke
  Scenario: Should override ISA
    Given I have the following data for override ISA:
      | user     | password | program   | product | productName | category | whoratio | dosesperyear | wastageFactor | bufferpercentage | minimumvalue | maximumvalue | adjustmentvalue |
      | Admin123 | Admin123 | VACCINES  | P1      | antibiotic1 | C1       | 1        | 2            | 3             | 4                | null         | null         | 5               |
    And I am logged in as Admin
    And I access create facility page
    When I create facility
    And I override ISA "24"
    Then I should see calculated ISA "7"
    When I click ISA done
    And I save facility
    Then I should see save successfully
    When I search facility
    Then I should see overridden ISA "24"

  @smoke
  Scenario: Verifying Forgot Password functionality
    Given I am on forgot password screen
    When I type email "John_Doe@openlmis.com"
    And I type and username "Admin123"
    When I click submit button
    Then I should see email send successfully
    And I am logged in as Admin

  @smoke
  Scenario: Verify New Regimen Created
    Given I have data available for programs configured
    And I am logged in as Admin
    When I access regimen configuration page
    Then I should see configured program list
    When I configure program "ESSENTIAL MEDICINES" for regimen template
    And I add new regimen:
    |Category|Code  |Name |Active|
    |Adults  |Code1 |Name1|true  |
    And I save regimen
    Then I should see regimen created message

  @smoke
  Scenario: Verify New Regimen Reporting Field Configuration
    Given I have data available for programs configured
    And I am logged in as Admin
    When I access regimen configuration page
    Then I should see configured program list
    When I configure program "ESSENTIAL MEDICINES" for regimen template
    And I add new regimen:
      |Category|Code  |Name |Active|
      |Adults  |Code1 |Name1|true  |
    And I access regimen reporting fields tab
    Then I should see regimen reporting fields
    When I add new regimen reporting field:
      |NoOfPatientsOnTreatment |Remarks         |
      |false                   |Testing column  |
    And I save regimen
    Then I should see regimen created message
    When I edit program "ESSENTIAL MEDICINES" for regimen template
    Then I should see created regimen and reporting fields:
      |Code  |Name |Remarks         |
      |Code1 |Name1|Testing column  |
    When I activate Number Of Patients On Treatment
    And I save regimen
    Then I should see regimen created message

  @smoke
  Scenario: Admin user should not access requisition page
    Given I am logged in as Admin
    When I access initiate requisition page through URL
    Then I should see unauthorized access message

  @smoke
  Scenario: Requisition user should not access admin Page
    Given I have "storeincharge" user with "CREATE_REQUISITION,VIEW_REQUISITION" rights
    And I am logged in as "storeincharge"
    When I access create facility page through URL
    Then I should see unauthorized access message

  @smoke
  Scenario: Distribution user should view ISA, Override ISA and NoRecords for different delivery zone, program & period combination
    Given I have data available for distribution load amount
    When I am logged in as "fieldcoordinator"
    And I access plan my distribution page
    And I select delivery zone "Delivery Zone First"
    And I select program "VACCINES"
    And I select period "Period14"
    And I click load amount
    Then I should see ISA values as per delivery zone facilities
    When I access plan my distribution page
    And I select delivery zone "Delivery Zone Second"
    And I select program "TB"
    And I select period "Period14"
    And I click load amount
    Then I should see message "No records found"

  @smoke
  Scenario: Distribution user should view aggregate ISA for delivery zone
    Given I have data available for multiple facilities in a delivery zone for distribution load amount
    When I am logged in as "fieldcoordinator"
    And I access plan my distribution page
    And I select delivery zone "Delivery Zone First"
    And I select program "VACCINES"
    And I select period "Period14"
    And I click load amount
    Then I should see aggregate ISA values as per mutiple facilities in one delivery zone
