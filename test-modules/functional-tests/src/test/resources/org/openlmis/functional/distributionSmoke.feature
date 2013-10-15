Feature: Smoke Tests

  @smokeDistribution
  @ie2

  Scenario: User should able to configure program product ISA
    Given I have data available for program product ISA
    And I am logged in as Admin
    When I access program product ISA page for "VACCINES"
    And I type ratio "3.9" dosesPerYear "3" wastage "10" bufferPercentage "25" adjustmentValue "0" minimumValue "10" maximumValue "1000"
    Then I verify calculated ISA value having population "1000" ratio "3.9" dosesPerYear "3" wastage "10" bufferPercentage "25" adjustmentValue "0" minimumValue "10" maximumValue "1000"
    And I click cancel
    And I access home page

  @smokeDistribution
  @ie2

  Scenario: User should able to initiate & delete distribution
    Given I have the following data for distribution:
      | userSIC       | deliveryZoneCodeFirst | deliveryZoneCodeSecond | deliveryZoneNameFirst | deliveryZoneNameSecond | facilityCodeFirst | facilityCodeSecond | programFirst | programSecond | schedule |
      | storeincharge | DZ1                   | DZ2                    | Delivery Zone First   | Delivery Zone Second   | F10               | F11                | VACCINES     | TB            | M        |
    And I have data available for "Multiple" facilities attached to delivery zones
    And I assign delivery zone "DZ1" to user "storeincharge" having role "store in-charge"
    And I am logged in as "storeincharge"
    And I access plan my distribution page
    Then I see no diistribution in cache
    When I select delivery zone "Delivery Zone First"
    And I select program "VACCINES"
    And I select period "Period14"
    And I initiate distribution
    Then I should see data download successfully
    And I should see delivery zone "Delivery Zone First" program "VACCINES" period "Period14" in table
    And I remove cached distrubution
    Then I observe confirm delete distribution dialog
    When I cancel delete distribution
    And I should see delivery zone "Delivery Zone First" program "VACCINES" period "Period14" in table
    And I remove cached distrubution
    And I confirm delete distribution
    Then I see no diistribution in cache

  @smokeDistribution
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

  @smokeDistribution
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

  @smokeDistribution
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

  @smokeDistribution
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

  @smokeDistribution
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
    Then I should verify facility zone "Health center" in the header
    And  I should verify facility name "Village Dispensary" in the header
    And I verify legends


  @smokeDistribution
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
    And I see "overall" refrigerator icon as "RED"
    When I edit refrigerator
    Then I see "individual" refrigerator icon as "RED"
    And I enter refrigerator temperature "3"
    And I see "overall" refrigerator icon as "AMBER"
    Then I see "individual" refrigerator icon as "AMBER"
    And I verify "Yes" it was working correctly when I left
    And I enter low alarm events "1"
    And I enter high alarm events "0"
    And I verify "No" that there is a problem with refrigerator since last visit
    Then I see "individual" refrigerator icon as "GREEN"
    And I see "overall" refrigerator icon as "GREEN"
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

  @smokeDistribution
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

  @smokeDistribution
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
    Then Verify "epi use" indicator should be "RED"
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
    When I access plan my distribution page
    When I record data
    And I choose facility "F10"
    Then Verify "epi use" indicator should be "GREEN"

  @smokeDistribution
  @ie2

  Scenario: User should verify facility and sync status
    Given I have the following data for distribution:
      | userSIC       | deliveryZoneCodeFirst | deliveryZoneCodeSecond | deliveryZoneNameFirst | deliveryZoneNameSecond | facilityCodeFirst | facilityCodeSecond | programFirst | programSecond | schedule |
      | storeincharge | DZ1                   | DZ2                    | Delivery Zone First   | Delivery Zone Second   | F10               | F11                | VACCINES     | TB            | M        |
    And I update product "P10" to have product group "penta"
    And I have data available for "Multiple" facilities attached to delivery zones
    And I assign delivery zone "DZ1" to user "storeincharge" having role "store in-charge"
    And I disassociate "F11" from delivery zone
    When I am logged in as "storeincharge"
    And I access plan my distribution page
    And I select delivery zone "Delivery Zone First"
    And I select program "VACCINES"
    And I select period "Period14"
    And I initiate distribution
    When I record data
    And I choose facility "F10"
    Then I see "Overall" facility icon as "AMBER"
    And I see "Individual" facility icon as "AMBER"
    When I add new refrigerator
    When I enter Brand "LG"
    And I enter Modal "800 LITRES"
    And I enter Serial Number "GR-J287PGHV"
    And I access done
    Then I see "Overall" facility icon as "RED"
    And I see "Individual" facility icon as "RED"
    When I access plan my distribution page
    When I sync recorded data
    Then I verify sync message as "No facility for the chosen zone, program and period is ready to be synced"
    When I record data

    And I choose facility "F10"
    When I edit refrigerator
    And I enter refrigerator temperature "3"
    And I verify "Yes" it was working correctly when I left
    And I enter low alarm events "1"
    And I enter high alarm events "0"
    And I verify "No" that there is a problem with refrigerator since last visit

    And I navigate to general observations tab
    And I Enter "general observation" values:
      | observations     | confirmedByName | confirmedByTitle | verifiedByName | verifiedByTitle |
      | some observation | samuel          | fc               | mai ka         | lal             |

    And Navigate to EPI tab
    And I Enter "epi use" values:
      | distributed | expirationDate | loss | received | firstOfMonth | endOfMonth |
      | 16          | 11/2012        | 1    | 10       | 12           |            |
    And I enter EPI end of month as "5"

    Then I see "Overall" facility icon as "GREEN"
    And I see "Individual" facility icon as "GREEN"
    When I access plan my distribution page
    When I sync recorded data
    Then I verify sync message as "F10 - Village Dispensary synced successfully"
    And I view observations data in DB:
      | observations     | confirmedByName | confirmedByTitle | verifiedByName | verifiedByTitle |
      | some observation | samuel          | fc               | mai ka         | lal             |
    When I record data
    And I choose facility "F10"
    Then I see "Overall" facility icon as "BLUE"
    And I see "Individual" facility icon as "BLUE"
    When I navigate to general observations tab
    And I see general observations fields disabled
    When Navigate to EPI tab
    Then I see epi fields disabled
    When I navigate to refrigerator tab
    And I access show
    Then I see refrigerator fields disabled
