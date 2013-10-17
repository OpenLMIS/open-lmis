Feature: Smoke Tests

  @smokeRequisition
  
  Scenario: User should be able to save and submit regimen data
    Given I have the following data for regimen:
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

  @smokeRequisition
  

  Scenario: User should view requisition and regimen after authorization
    Given I have the following data for regimen:
      | HIV | storeincharge | ADULTS | RegimenCode1 | RegimenName1 | RegimenCode2 | RegimenName2 |
    And I have "storeincharge" user with "CREATE_REQUISITION,VIEW_REQUISITION" rights and data to initiate requisition
    And I have regimen template configured
    And I am logged in as "storeincharge"
    And I access initiate requisition page
    When I click proceed
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


  @smokeRequisition
  

  Scenario: Verifying Forgot Password functionality
    Given I am on forgot password screen
    When I type email "John_Doe@openlmis.com"
    And I type and username "Admin123"
    When I click submit button
    Then I should see email send successfully
    And I am logged in as Admin

  @smokeRequisition
  

  Scenario: Verify New Regimen Created
    Given I have data available for programs configured
    And I am logged in as Admin
    When I access regimen configuration page
    Then I should see configured program list
    When I configure program "ESSENTIAL MEDICINES" for regimen template
    And I add new regimen:
      | Category | Code  | Name  | Active |
      | Adults   | Code1 | Name1 | true   |
    And I save regimen
    Then I should see regimen created message

  @smokeRequisition
  

  Scenario: Verify New Regimen Reporting Field Configuration
    Given I have data available for programs configured
    And I am logged in as Admin
    When I access regimen configuration page
    Then I should see configured program list
    When I configure program "ESSENTIAL MEDICINES" for regimen template
    And I add new regimen:
      | Category | Code  | Name  | Active |
      | Adults   | Code1 | Name1 | true   |
    And I access regimen reporting fields tab
    Then I should see regimen reporting fields
    When I add new regimen reporting field:
      | NoOfPatientsOnTreatment | Remarks        |
      | false                   | Testing column |
    And I save regimen
    Then I should see regimen created message
    When I edit program "ESSENTIAL MEDICINES" for regimen template
    Then I should see created regimen and reporting fields:
      | Code  | Name  | Remarks        |
      | Code1 | Name1 | Testing column |
    When I activate Number Of Patients On Treatment
    And I save regimen
    Then I should see regimen created message

  @smokeRequisition
  

  Scenario: Admin user should not access requisition page
    Given I am logged in as Admin
    When I access initiate requisition page through URL
    Then I should see unauthorized access message

  @smokeRequisition
  

  Scenario: Requisition user should not access admin Page
    Given I have "storeincharge" user with "CREATE_REQUISITION,VIEW_REQUISITION" rights
    And I am logged in as "storeincharge"
    When I access create facility page through URL
    Then I should see unauthorized access message

  @smokeRequisition
  

  Scenario: Admin can create, disable & restore user
    Given I am logged in as Admin
    When I create a user:
      | Email                   | Firstname | Lastname | UserName |
      | Dummy_User@openlmis.com | Dummy     | User     | Dummy    |
    Then I should see user not verified
    When I disable user "Dummy User"
    Then I should see disable user "Dummy User" message
    When I enable user "Dummy User"
    Then I should see enable user "Dummy User" message
    When I verify user email "Dummy_User@openlmis.com"
    Then I should see user "Dummy User" verified

  @smokeRequisition
  

  Scenario: User should be able to configure order file format
    Given I configure order file:
      | File Prefix | Header In File |
      | O           | FALSE          |
    And I am logged in as Admin
    And I access configure order page
    Then I should see order file prefix "O"
    And I should see include column header as "false"
    And I should see all column headers disabled
    And I should see include checkbox "checked" for all column headers
    When I save order file format
    Then I should see "Order file configuration saved successfully!"

  @smokeRequisition
  

  Scenario: User should be able to configure shipment file format using default format
    When I am logged in as Admin
    And I access configure shipment page
    And I should see include column headers as "false"
    And I should see include checkbox for all data fields
    And I should see default value of positions
    When I save shipment file format
    Then I should see successfull message "Shipment file configuration saved successfully!"

  @smokeRequisition
  Scenario: User should download order file and verify
    Given I have the following data for regimen:
      | HIV | storeincharge | ADULTS | RegimenCode1 | RegimenName1 | RegimenCode2 | RegimenName2 |
    And I configure order file:
      | File Prefix | Header In File |
      | O           | TRUE           |

    And I configure openlmis order file columns:
      | Data Field Label         | Include In Order File | Column Label | Position | Format     |
      | header.order.number      | TRUE                  | ONUm         | 6        |            |
      | header.order.date        | TRUE                  | Order Date   | 5        | yyyy/MM/dd |
      | label.period             | TRUE                  | Period       | 4        | dd/MM/yyyy |
      | header.quantity.approved | TRUE                  | AQTY         | 3        |            |
      | header.product.code      | TRUE                  |              | 2        |            |
      | create.facility.code     | TRUE                  | FCCode       | 1        |            |

    And I configure non openlmis order file columns:
      | Data Field Label | Include In Order File | Column Label | Position |
      | NOT APPLICABLE   | TRUE                  |              | 7        |
      | NOT APPLICABLE   | TRUE                  | Dummy        | 8        |

    And I have "storeincharge" user with "CREATE_REQUISITION,VIEW_REQUISITION,APPROVE_REQUISITION" rights and data to initiate requisition
    And I have "lmu" role having "ADMIN" based "CONVERT_TO_ORDER,VIEW_ORDER" rights
    And I have users:
      | UserId | Email                 | Firstname | Lastname | UserName | Role | FacilityCode |
      | 111    | Jake_Doe@openlmis.com | Jake      | Doe      | lmu      | lmu  | F10          |
    And I have fulfillment data for user "lmu" role "lmu" and facility "F10"
    And I have regimen template configured
    And I am logged in as "storeincharge"
    And I access initiate requisition page
    When I click proceed
    And I populate RnR data
    And I populate Regimen data as patientsOnTreatment "100" patientsToInitiateTreatment "200" patientsStoppedTreatment "300" remarks "Regimens data filled"
    And I update requisition status to "SUBMITTED"
    And I update requisition status to "AUTHORIZED"
    And I have approved quantity "10"
    And I update requisition status to "APPROVED"
    And I logout
    And I am logged in as "lmu"
    And I access convert to order page
    And I convert to order
    And I access view orders page
    And I download order file
    And I get order data in file prefix "O"
    Then I verify order file line "1" having "FCCode,,AQTY,Period,Order Date,ONUm,,Dummy"
    And I verify order file line "2" having "F10,P10,10,16/01/2012,"
    And I verify order date format "yyyy/mm/dd" in line "2"
    And I verify order id in line "2"

  @smokeRequisition
  

  Scenario: User should be able to initiate and submit emergency RnR
    Given I have the following data for regimen:
      | HIV | storeincharge | ADULTS | RegimenCode1 | RegimenName1 | RegimenCode2 | RegimenName2 |
    Given I have "storeincharge" user with "CREATE_REQUISITION,VIEW_REQUISITION" rights and data to initiate requisition
    And I am logged in as "storeincharge"
    And I access initiate emergency requisition page
    Then I got error message "No current period defined. Please contact the Admin."
    When I have period "currentPeriod" associated with schedule "M"
    And I access home page
    And I access initiate emergency requisition page
    Then I should verify "currentPeriod" with status "Not yet started" in row "1"
    When I access proceed
    And I access home page
    And I access initiate requisition page
    And I access initiate emergency requisition page
    Then I should verify "currentPeriod" with status "INITIATED" in row "2"
    And I should verify "currentPeriod" with status "Not yet started" in row "1"
    When I access proceed
    And I enter beginning balance "100"
    And I enter quantity dispensed "100"
    And I enter quantity received "100"
    And I click submit
    And I click ok
    And I access home page
    And I access initiate requisition page
    And I access initiate emergency requisition page
    Then I should verify "currentPeriod" with status "INITIATED" in row "3"
    Then I should verify "currentPeriod" with status "SUBMITTED" in row "2"
    And I should verify "currentPeriod" with status "Not yet started" in row "1"

  @smokeRequisition
  
  Scenario: Selected requisitions across pages should not convert to order
    Given I have "51" requisitions for convert to order
    And I am logged in as "storeincharge"
    When I access convert to order page
    And I select "1" requisition on page "1"
    And I select "1" requisition on page "2"
    And I access convert to order
    Then "1" requisition converted to order