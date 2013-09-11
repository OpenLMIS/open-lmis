Feature: Smoke Tests

  @smoke
  @ie2
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

  @smoke
  @ie2

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

  @smoke
  @ie2

  Scenario: User should able to configure program product ISA
    Given I have data available for program product ISA
    And I am logged in as Admin
    When I access program product ISA page for "VACCINES"
    And I type ratio "3.9" dosesPerYear "3" wastage "10" bufferPercentage "25" adjustmentValue "0" minimumValue "10" maximumValue "1000"
    Then I verify calculated ISA value having population "1000" ratio "3.9" dosesPerYear "3" wastage "10" bufferPercentage "25" adjustmentValue "0" minimumValue "10" maximumValue "1000"
    And I click cancel
    And I access home page

  @smoke
  @ie2

  Scenario: User should able to initiate distribution
    Given I have the following data for distribution:
      | userSIC       | deliveryZoneCodeFirst | deliveryZoneCodeSecond | deliveryZoneNameFirst | deliveryZoneNameSecond | facilityCodeFirst | facilityCodeSecond | programFirst | programSecond | schedule |
      | storeincharge | DZ1                   | DZ2                    | Delivery Zone First   | Delivery Zone Second   | F10               | F11                | VACCINES     | TB            | M        |
    And I have data available for "Multiple" facilities attached to delivery zones
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
  @ie2

  Scenario: User should able to fetch program period on manage distribution screen
    Given I have the following data for distribution:
      | userSIC       | deliveryZoneCodeFirst | deliveryZoneCodeSecond | deliveryZoneNameFirst | deliveryZoneNameSecond | facilityCodeFirst | facilityCodeSecond | programFirst | programSecond | schedule |
      | storeincharge | DZ1                   | DZ2                    | Delivery Zone First   | Delivery Zone Second   | F10               | F11                | VACCINES     | TB            | M        |
    And I have data available for "Multiple" facilities attached to delivery zones
    And I am logged in as "storeincharge"
    And I access plan my distribution page
    Then I verify fields
    And I should see deliveryZone "--None Assigned--"
    When I assign delivery zone "DZ1" to user "storeincharge" having role "store in-charge"
    And I access plan my distribution page
    And I select delivery zone "Delivery Zone First"
    Then I should see program "VACCINES,TB"
    And I select program "VACCINES"
    Then I should see period "Period14"
    And I click view load amount

  @smoke
  @ie2

  Scenario: User should able to override ISA
    Given I have the following data for override ISA:
      | user     | program  | product | productName | category | whoratio | dosesperyear | wastageFactor | bufferpercentage | minimumvalue | maximumvalue | adjustmentvalue |
      | Admin123 | VACCINES | P1      | antibiotic1 | C1       | 1        | 2            | 3             | 4                | null         | null         | 5               |
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
  @ie2

  Scenario: Verifying Forgot Password functionality
    Given I am on forgot password screen
    When I type email "John_Doe@openlmis.com"
    And I type and username "Admin123"
    When I click submit button
    Then I should see email send successfully
    And I am logged in as Admin

  @smoke
  @ie2

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

  @smoke
  @ie2

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

  @smoke
  @ie2

  Scenario: Admin user should not access requisition page
    Given I am logged in as Admin
    When I access initiate requisition page through URL
    Then I should see unauthorized access message

  @smoke
  @ie2

  Scenario: Requisition user should not access admin Page
    Given I have "storeincharge" user with "CREATE_REQUISITION,VIEW_REQUISITION" rights
    And I am logged in as "storeincharge"
    When I access create facility page through URL
    Then I should see unauthorized access message

  @smoke
  @ie2

  Scenario: Distribution user should view ISA, Override ISA and NoRecords for different delivery zone, program & period combination
    Given I have data available for distribution load amount
    And I have data available for "Multiple" facilities attached to delivery zones
    And I have following ISA values:
      | Program  | Product | whoratio | dosesperyear | wastageFactor | bufferpercentage | minimumvalue | maximumvalue | adjustmentvalue |
      | VACCINES | P10     | 10       | 10           | 10            | 10               | null         | null         | 0               |
    And I have following override ISA values:
      | Facility Code | Program  | Product | ISA  |
      | F11           | VACCINES | P11     | 1000 |
    And I have role assigned to delivery zones
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
  @ie2

  Scenario: Distribution user should view aggregate ISA for delivery zone
    Given I have data available for distribution load amount
    And I have data available for "Single" facility attached to delivery zones
    And I have following override ISA values:
      | Facility Code | Program  | Product | ISA  |
      | F10           | VACCINES | P10     | 1000 |
      | F10           | VACCINES | P11     | 2000 |
      | F11           | VACCINES | P10     | 3000 |
      | F11           | VACCINES | P11     | 4000 |
    And I have role assigned to delivery zones
    When I am logged in as "fieldcoordinator"
    And I access plan my distribution page
    And I select delivery zone "Delivery Zone First"
    And I select program "VACCINES"
    And I select period "Period14"
    And I click load amount
    Then I should see aggregate ISA values as per multiple facilities in one delivery zone

  @smoke
  @ie2

  Scenario: User should see facility list/ selection page
    Given I have the following data for distribution:
      | userSIC       | deliveryZoneCodeFirst | deliveryZoneCodeSecond | deliveryZoneNameFirst | deliveryZoneNameSecond | facilityCodeFirst | facilityCodeSecond | programFirst | programSecond | schedule |
      | storeincharge | DZ1                   | DZ2                    | Delivery Zone First   | Delivery Zone Second   | F10               | F11                | VACCINES     | TB            | M        |
    And I have data available for "Multiple" facilities attached to delivery zones
    And I assign delivery zone "DZ1" to user "storeincharge" having role "store in-charge"
    And I am logged in as "storeincharge"
    And I access plan my distribution page
    When I select delivery zone "Delivery Zone First"
    And I select program "VACCINES"
    And I select period "Period14"
    And I initiate distribution
    And I record data
    And I verify Distributions data is not synchronised
    Then I should see Delivery Zone "Delivery Zone First", Program "VACCINES" and Period "Period14" in the header
    And I should see No facility selected
    And I should see "active" facilities that support the program "VACCINES" and delivery zone "Delivery Zone First"
    When I choose facility "F10"
    Then I should see "Health center" in the header
    And  I should see "Village Dispensary" in the header

  @smoke
  @ie2

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

  @smoke
  @ie2

  Scenario: User should be able to add/edit/delete refrigerator
    Given I have the following data for distribution:
      | userSIC       | deliveryZoneCodeFirst | deliveryZoneCodeSecond | deliveryZoneNameFirst | deliveryZoneNameSecond | facilityCodeFirst | facilityCodeSecond | programFirst | programSecond | schedule |
      | storeincharge | DZ1                   | DZ2                    | Delivery Zone First   | Delivery Zone Second   | F10               | F11                | VACCINES     | TB            | M        |
    And I have data available for "Multiple" facilities attached to delivery zones
    And I assign delivery zone "DZ1" to user "storeincharge" having role "store in-charge"
    And I am logged in as "storeincharge"
    And I access plan my distribution page
    When I select delivery zone "Delivery Zone First"
    And I select program "VACCINES"
    And I select period "Period14"
    And I initiate distribution
    And I record data
    When I choose facility "F10"
    Then I should see Refrigerators screen
    When I add new refrigerator
    Then I should see New Refrigerator screen
    When I enter Brand "LG"
    And I enter Modal "800 LITRES"
    And I enter Serial Number "GR-J287PGHV"
    And I access done
    And I verify Distributions data is not synchronised
    And I verify Refrigerator data is not synchronised
    Then I should see refrigerator "LG;800 LITRES;GR-J287PGHV" added successfully
    And I should see "overall" refrigerator icon as "RED"
    When I edit refrigerator
    Then I should see "individual" refrigerator icon as "RED"
    And I enter refrigerator temperature "3"
    And I should see "overall" refrigerator icon as "AMBER"
    Then I should see "individual" refrigerator icon as "AMBER"
    And I verify "Yes" it was working correctly when I left
    And I enter low alarm events "1"
    And I enter high alarm events "0"
    And I verify "No" that there is a problem with refrigerator since last visit
    Then I should see "individual" refrigerator icon as "GREEN"
    And I should see "overall" refrigerator icon as "GREEN"
    And I enter Notes "miscellaneous"
    And I add refrigerator
    And I verify Refrigerator data is not synchronised
    Then I should not see Refrigerator details section
    And I should see Edit button
    When I edit refrigerator
    Then I should see refrigerator details as refrigerator temperature "3" low alarm events "1" high alarm events "0" notes "miscellaneous"
    And I add refrigerator
    When I delete refrigerator
    Then I should see confirmation for delete
    When I confirm delete
    Then I should see refrigerator "LG;800 LITRES;GR-J287PGHV" deleted successfully


  @smoke
  @ie2

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

  @smoke
  @ie2

  Scenario: User should be able to configure shipment file format using default format
    When I am logged in as Admin
    And I access configure shipment page
    And I should see include column headers as "false"
    And I should see include checkbox for all data fields
    And I should see default value of positions
    When I save shipment file format
    Then I should see successfull message "Shipment file configuration saved successfully!"

  @smoke
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

  @Smoke
  @ie2

  Scenario: User should fill EPI use data
    Given I have the following data for distribution:
      | userSIC       | deliveryZoneCodeFirst | deliveryZoneCodeSecond | deliveryZoneNameFirst | deliveryZoneNameSecond | facilityCodeFirst | facilityCodeSecond | programFirst | programSecond | schedule |
      | storeincharge | DZ1                   | DZ2                    | Delivery Zone First   | Delivery Zone Second   | F10               | F11                | VACCINES     | TB            | M        |
    And I update product "P10" to have product group "penta"
    And I have data available for "Multiple" facilities attached to delivery zones
    And I assign delivery zone "DZ1" to user "storeincharge" having role "store in-charge"
    When I am logged in as "storeincharge"
    And I access plan my distribution page
    And I select delivery zone "Delivery Zone First"
    And I select program "VACCINES"
    And I select period "Period14"
    And I initiate distribution
    And I record data
    And I choose facility "F10"
    And Navigate to EPI tab
    Then Verify "epi use" indicator should be "AMBER"
    Then I should see product group "penta-Name"
    When I Enter "epi use" values:
      | distributed | expirationDate | loss | received | firstOfMonth | endOfMonth |
      | 16          | 11/2012        | 1    | 10       | 12           |            |
    Then Verify "epi use" indicator should be "AMBER"
    When I enter EPI end of month as "5"
    Then Verify "epi use" indicator should be "GREEN"
    And I verify total is "22"
    And I verify saved "epi use" values:
      | distributed | expirationDate | loss | received | firstOfMonth | endOfMonth | total |
      | 16          | 11/2012        | 1    | 10       | 12           | 5          | 22    |

  @Smoke
  @ie2

  Scenario: User should fill general observation data
    Given I have the following data for distribution:
      | userSIC       | deliveryZoneCodeFirst | deliveryZoneCodeSecond | deliveryZoneNameFirst | deliveryZoneNameSecond | facilityCodeFirst | facilityCodeSecond | programFirst | programSecond | schedule |
      | storeincharge | DZ1                   | DZ2                    | Delivery Zone First   | Delivery Zone Second   | F10               | F11                | VACCINES     | TB            | M        |
    And I have data available for "Multiple" facilities attached to delivery zones
    And I assign delivery zone "DZ1" to user "storeincharge" having role "store in-charge"
    When I am logged in as "storeincharge"
    And I access plan my distribution page
    And I select delivery zone "Delivery Zone First"
    And I select program "VACCINES"
    And I select period "Period14"
    And I initiate distribution
    And I record data
    And I choose facility "F10"
    And I navigate to general observations tab
    Then Verify "general observation" indicator should be "RED"
    When I Enter "general observation" values:
      | observations     | confirmedByName | confirmedByTitle | verifiedByName | verifiedByTitle |
      | some observation | samuel          | fc               |                |                 |
    Then Verify "general observation" indicator should be "AMBER"
    When I Enter "general observation" values:
      | observations     | confirmedByName | confirmedByTitle | verifiedByName | verifiedByTitle |
      | some observation | samuel          | fc               | mai ka         | lal             |
    Then Verify "general observation" indicator should be "GREEN"
    And I verify saved "general observation" values:
      | observations     | confirmedByName | confirmedByTitle | verifiedByName | verifiedByTitle |
      | some observation | samuel          | fc               | mai ka         | lal             |

