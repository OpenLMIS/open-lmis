Feature: End to end requisition flow

  @e2e
  Scenario: Requisition initiate, authorize, approve & convert to order.
    Given I am logged in as "Admin123"
    And I access create facility page
    When I create "HIV" program supported facility
    Then I should see message for successfully created facility
    When I setup supervisory node data
    And I setup warehouse data
    And I create "Store-in-charge" role having "Requisition" based "Create Requisition,Authorize Requisition,Approve Requisition" rights
    And I create "lmu" role having "Fulfillment" based "Convert To Order Requisition,View Orders Requisition,Manage POD" rights
    And I create "Medical-officer" role having "Requisition" based "Approve Requisition" rights
    And I create users:
      | Email                   | FirstName | LastName | UserName       | Role            | RoleType    | FacilityCode | Program | Node   | Warehouse        | WarehouseRole |
      | Fatima_Doe@openlmis.com | Fatima    | Doe      | storeInCharge  | Store-in-charge | REQUISITION | F10          | HIV     | Node 1 | Central Hospital | lmu           |
      | Jane_Doe@openlmis.com   | Jane      | Doe      | medicalOfficer | Medical-Officer | REQUISITION | F11          | HIV     | Node 2 | Central Hospital | lmu           |
      | Jake_Doe@openlmis.com   | Jake      | Doe      | lmu            | Store-in-charge | FULFILLMENT | F10          | HIV     | Node 1 | Central Hospital | lmu           |
    And I update "storeInCharge" home facility
    And I setup product & requisition group data
    And I setup period, schedule & requisition group data
    And I configure "HIV" template
    And I logout
    And I am logged in as "storeInCharge"
    And I initiate and submit requisition
    And I enter beginning balance as "10", quantityDispensed as "10", quantityReceived as "10" and totalAdjustmentAndLoses as "1"
    And I verify normalized consumption as "5" and amc as "5"
    And I submit RnR
    When I click print
    Then I close new window
    When I add comments
    And I update & verify ordered quantities
    And I update & verify requested quantities
    And I add non full supply items & verify total cost
    And I authorize RnR
    And I verify normalized consumption as "36" and amc as "36" for product "P10" in Database
    Then I verify cost & authorize message
    When I click print
    Then I close new window
    And I should not see requisition to approve
    When I logout
    When I am logged in as "medicalOfficer"
    And I access requisition on approval page
    Then I should see RnR Header
    And I should see full supply approved quantity
    When I access non full supply tab
    And I should see non full supply approved quantity
    When I update full supply approve quantity as "290"
    And I add comments without save
    Then I should see blank comment section
    When I add "This is urgent" comment
    Then I should see "medicalOfficer" comments as "This is urgent"
    When I click print
    Then I close new window
    And I should see correct total after authorize
    When I approve requisition
    Then I should see no requisition pending message
    When I logout
    And I am logged in as "storeInCharge"
    And I access requisition on approval page
    Then I should see RnR Header
    And I should see approved quantity from lower hierarchy
    When I update full supply approve quantity as "100"
    Then I verify full supply cost for approved quantity "100"
    When I update non full supply approve quantity as "100"
    Then I verify non full supply cost for approved quantity "100"
    Then I should see correct total after authorize
    When I click print
    Then I close new window
    When I approve requisition
    Then I should see no requisition pending message
    When I logout
    And I am logged in as "lmu"
    And I access convert to order page
    Then I should see pending order list
    When I convert to order
    And I access view orders page
    Then I should see ordered list with download link
    When I access Manage POD page
    Then I should see list of orders to manage POD for "Regular" Rnr
    When I access Manage POD page
    When I click on update Pod link for Row "1"
    Then I should see all products to update pod
    When I do not have anything to pack to ship
    When I click POD print
    Then I close new window
    And I access view orders page
    Then I should see ordered list without download link
    When I logout

    And I am logged in as "storeInCharge"
    And I initiate and submit emergency requisition
    And I update & verify quantities for emergency RnR
    And I update & verify requested quantities
    And I add non full supply items & verify total cost
    And I authorize RnR
    When I click resize button
    And I click full view print button
    Then I close new window
    Then I click resize button
    When I am logged in as "medicalOfficer"
    And I access requisition on approval page
    When I update full supply approve quantity as "290"
    Then I verify full supply cost for approved quantity "290"
    When I update non full supply approve quantity as "290"
    Then I verify non full supply cost for approved quantity "290"
    When I approve requisition
    When I logout
    And I am logged in as "storeInCharge"
    And I access requisition on approval page
    When I update full supply approve quantity as "100"
    Then I verify full supply cost for approved quantity "100"
    When I update non full supply approve quantity as "100"
    Then I verify non full supply cost for approved quantity "100"
    When I click print
    Then I close new window
    When I approve requisition
    When I logout
    And I am logged in as "lmu"
    And I access convert to order page
    Then I should see pending order list
    When I convert to order
    And I access view orders page
    And I verify order status as "Transfer failed" in row "1"
    And I verify order status as "Transfer failed" in row "2"
    Then I should see ordered list with download link
    And I change order status to "RELEASED"
    When I access Manage POD page
    Then I should see list of orders to manage POD for "Emergency" Rnr
    When I click on update Pod link for Row "2"
    Then I should see all products to update pod
    And I enter "10" as quantity received, "78" as quantity returned and "notes" as notes in row "1"
    And I enter "35" as quantity received, "" as quantity returned and "Other" as notes in row "2"
    And I enter "openLMIS" as deliveredBy,"Facility In charge" as receivedBy and "27/02/2014" as receivedDate
    Then I submit POD
    When I click POD print
    Then I close new window
    Then I verify quantity received, quantity returned,notes,deliveredBy,receivedBy,receivedDate disabled
    And I verify in database deliveredBy as "openLMIS",receivedBy as "Facility In charge" and receivedDate as "2014-02-27 00:00:00"
    Then I access view orders page
    And I verify order status as "Received" in row "1"
    Then I logout