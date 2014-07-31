Feature: Smoke Requisition Tests

  @smokeRequisition
  Scenario: User should be able to initiate and submit emergency RnR
    Given I have the following data for regimen:
      | HIV | storeInCharge | ADULTS | RegimenCode1 | RegimenName1 | RegimenCode2 | RegimenName2 |
    Given I have "storeInCharge" user with "CREATE_REQUISITION,VIEW_REQUISITION" rights and data to initiate requisition
    And I am logged in as "storeInCharge"
    And I access initiate emergency requisition page
    Then I got error message "No current period defined. Please contact the Admin."
    When I have period "currentPeriod" associated with schedule "M"
    And I access home page
    And I access initiate emergency requisition page
    Then I should verify "currentPeriod" with status "Not yet started" in row "1"
    When I access proceed
    And I access initiate emergency requisition page
    Then I should verify "currentPeriod" with status "INITIATED" in row "2"
    And I should verify "currentPeriod" with status "Not yet started" in row "1"
    When I access proceed
    And I enter beginning balance "100"
    And I enter quantity dispensed "100"
    And I enter quantity received "100"
    And I click submit
    And I click ok
    When I click print
    Then I close new window
    Then I validate beginning balance "100"
    And I validate quantity dispensed "100"
    And I validate quantity received "100"
    And I access home page
    And I access initiate requisition page
    And I access initiate emergency requisition page
    Then I should verify "currentPeriod" with status "INITIATED" in row "3"
    Then I should verify "currentPeriod" with status "SUBMITTED" in row "2"
    And I should verify "currentPeriod" with status "Not yet started" in row "1"

  @smokeRequisition
  Scenario: User should be able to save and submit regimen data
    Given I have the following data for regimen:
      | HIV | storeInCharge | ADULTS | RegimenCode1 | RegimenName1 | RegimenCode2 | RegimenName2 |
    And I have "storeInCharge" user with "CREATE_REQUISITION,VIEW_REQUISITION" rights and data to initiate requisition
    And I have regimen template configured
    And I am logged in as "storeInCharge"
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
    When I click resize button
    And I click full view print button
    Then I close new window
    Then I click resize button

  @smokeRequisition
  Scenario: User should view requisition and regimen after authorization
    Given I have the following data for regimen:
      | HIV | storeInCharge | ADULTS | RegimenCode1 | RegimenName1 | RegimenCode2 | RegimenName2 |
    And I have "storeInCharge" user with "CREATE_REQUISITION,VIEW_REQUISITION" rights and data to initiate requisition
    And I have regimen template configured
    And I am logged in as "storeInCharge"
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
    And I am logged in as "Admin123"

  @smokeRequisition
  Scenario: Verify New Regimen Created
    Given I have data available for programs configured
    And I am logged in as "Admin123"
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
    And I am logged in as "Admin123"
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
    Given I am logged in as "Admin123"
    When I access initiate requisition page through URL
    Then I should see unauthorized access message

  @smokeRequisition
  Scenario: Requisition user should not access admin Page
    Given I have "storeInCharge" user with "CREATE_REQUISITION,VIEW_REQUISITION" rights
    And I am logged in as "storeInCharge"
    When I access create facility page through URL
    Then I should see unauthorized access message

  @smokeRequisition
  Scenario: Admin can create, disable & restore user
    Given I am logged in as "Admin123"
    When I create a user:
      | Email                   | FirstName | LastName | UserName |
      | Dummy_User@openlmis.com | Dummy     | User     | Dummy    |
    Then I should see user not verified
    When I disable user "Dummy" and "User"
    Then I should see disable user "Dummy User" message
    When I enable user "Dummy"
    Then I should see enable user "Dummy User" message
    When I verify user email "Dummy_User@openlmis.com"
    Then I should see user "Dummy" verified

  @smokeRequisition
  Scenario: User should be able to configure order file format
    Given I configure order file:
      | File Prefix | Header In File |
      | O           | FALSE          |
    And I am logged in as "Admin123"
    And I access configure order page
    Then I should see order file prefix "O"
    And I should see include column header as "false"
    And I should see all column headers disabled
    And I should see include checkbox "checked" for all column headers
    When I save order file format
    Then I should see "Order file configuration saved successfully!"

  @smokeRequisition
  Scenario: User should be able to configure shipment file format using default format
    When I am logged in as "Admin123"
    And I access configure shipment page
    And I should see include column headers unchecked
    And I should see include checkbox for all data fields
    And I should see default value of positions
    When I save shipment file format
    Then I should see successful message "Shipment file configuration saved successfully!"

  @smokeRequisition
  Scenario: User should be able to configure budget file format using default format
    When I am logged in as "Admin123"
    And I access configure budget page
    And I should see include column headers option unchecked
    And I verify default checkbox for all data fields
    And I verify default value of positions
    When I save budget file format
    Then I should see budget successful saved message as "Budget file configuration saved successfully!"

  @smokeRequisition
  Scenario: User should download order file and verify
    Given I have the following data for regimen:
      | HIV | storeInCharge | ADULTS | RegimenCode1 | RegimenName1 | RegimenCode2 | RegimenName2 |
    And I configure order file:
      | File Prefix | Header In File |
      | O           | TRUE           |

    And I configure openlmis order file columns:
      | Data Field Label         | Include In Order File | Column Label | Position | Format     |
      | header.order.number      | TRUE                  | ONUm         | 7        |            |
      | header.order.date        | TRUE                  | Order Date   | 6        | yyyy/MM/dd |
      | label.period             | TRUE                  | Period       | 5        | dd/MM/yyyy |
      | header.quantity.approved | TRUE                  | AQTY         | 4        |            |
      | header.product.code      | TRUE                  |              | 3        |            |
      | header.product.name      | TRUE                  |              | 2        |            |
      | create.facility.code     | TRUE                  | FCCode       | 1        |            |

    And I configure non openlmis order file columns:
      | Data Field Label | Include In Order File | Column Label | Position |
      | NOT APPLICABLE   | TRUE                  |              | 7        |
      | NOT APPLICABLE   | TRUE                  | Dummy        | 8        |

    And I have "storeInCharge" user with "CREATE_REQUISITION,VIEW_REQUISITION,APPROVE_REQUISITION" rights and data to initiate requisition
    And I have "lmu" role having "ADMIN" based "CONVERT_TO_ORDER,VIEW_ORDER" rights
    And I have users:
      | Email                 | FirstName | LastName | UserName | Role | FacilityCode |
      | Jake_Doe@openlmis.com | Jake      | Doe      | lmu      | lmu  | F10          |
    And I have fulfillment data for user "lmu" role "lmu" and facility "F10"
    And I have regimen template configured
    And I am logged in as "storeInCharge"
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
    Then I verify order file line "1" having "FCCode,,,AQTY,Period,Order Date,ONUm,,Dummy"
    And I verify order file line "2" having "F10"
    And I verify order file line "2" having "antibiotic Capsule 300/200/600 mg"
    And I verify order file line "2" having "P10"
    And I verify order file line "2" having "10"
    And I verify order file line "2" having "16/01/2012"
    And I verify order date format "yyyy/mm/dd" in line "2"
    And I verify order id in line "2"

  @smokeRequisition
  Scenario: Blank
    Given I have "storeInCharge" user with "MANAGE_POD" rights
    And I am logged in as "storeInCharge"

  @smokeRequisition
  Scenario: Selected requisitions across pages should not convert to order
    Given I have "51" requisitions for convert to order
    And I am logged in as "storeInCharge"
    When I access convert to order page
    And I select "1" requisition on page "1"
    And I select "1" requisition on page "2"
    And I convert selected requisitions to order
    Then "1" requisition converted to order

  @smokeRequisition
  Scenario: User should able to see list of orders to update POD for "Ready to pack" order
    Given I have "storeInCharge" user with "MANAGE_POD" rights
    And I have a/an "Regular" order in "READY_TO_PACK" status
    And I am logged in as "storeInCharge"
    When I access Manage POD page
    Then I should see list of orders to manage POD for Rnr
    When I click on update Pod link for Row "1"
    Then I should see all products to update pod

  @smokeRequisition
  Scenario: User should able to see list of orders to update POD for packed orders
    Given I have "storeInCharge" user with "MANAGE_POD" rights
    And I have a/an "Regular" order in "RELEASED" status
    When I receive shipment for the order
    And I am logged in as "storeInCharge"
    And I access Manage POD page
    And I click on update Pod link for Row "1"
    Then I should see all products listed in shipment file to update pod

  @smokeRequisition
  Scenario: User should able to submit POD for emergency RnR
    Given I have "storeInCharge" user with "MANAGE_POD" rights
    And I have a/an "Emergency" order in "RELEASED" status
    And I am logged in as "storeInCharge"
    And I receive shipment for the order
    And I access Manage POD page
    And I click on update Pod link for Row "1"
    And I enter "10" as quantity received, "78" as quantity returned and "notes" as notes in row "1"
    And I enter "openLMIS" as deliveredBy,"Facility Incharge" as receivedBy and "27/02/2014" as receivedDate
    And I submit POD
    Then I verify quantity received, quantity returned,notes,deliveredBy,receivedBy,receivedDate disabled
    And I verify in database quantity received as "10", quantity returned as "78" and notes as "notes"
    And I verify in database deliveredBy as "openLMIS",receivedBy as "Facility Incharge" and receivedDate as "2014-02-27 00:00:00"
    Then I access view orders page
    And I verify order status as "Received" in row "1"
    Then I access Manage POD page
    And I verify order not present on manage pod page