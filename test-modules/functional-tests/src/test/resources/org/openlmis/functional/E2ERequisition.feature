Feature: End to end requisition flow

  @smoke
  Scenario: Requisition initiate, authorize, approve & convert to oder.
    Given I am logged in as Admin
    And I access create facility page
    When I create "HIV" program supported facility
    Then I should see message for successfully created facility
    When I setup supervisory node data
    And I create "Store-in-charge" role having "Requisition" based "Create Requisition,Authorize Requisition,Approve Requisition" rights
    And I create "lmu" role having "Admin" based "Convert To Order Requisition,View Orders Requisition" rights
    And I create "Medical-officer" role having "Requisition" based "Approve Requisition" rights
    And I create users:
      |Email                  |Firstname|Lastname|UserName       |Role           |RoleType    |FacilityCode|Program|Node    |
      |Fatima_Doe@openlmis.com|Fatima   |Doe     |storeincharge  |Store-in-charge|REQUISITION |F10         |HIV    |Node 1  |
      |Jake_Doe@openlmis.com  |Jake     |Doe     |lmu            |lmu            |ADMIN       |F10         |HIV    |Node 1  |
      |Jane_Doe@openlmis.com  |Jane     |Doe     |medicalofficer |Medical-Officer|REQUISITION |F11         |HIV    |Node 2  |
    And I update "storeincharge" home facility
    And I setup product & requisition group data
    And I setup period, schedule & requisition group data
    And I configure "HIV" template
    And I logout
    And I am logged in as "storeincharge"
    And I initiate and submit requisition
    And I add comments
    And I update & verify ordered quantities
    And I update & verify requested quantities
    And I add non full supply items & verify total cost
    And I authorize RnR
    Then I verify cost & authorize message
    And I should not see requisition to approve
    When I logout
    When I am logged in as "medicalofficer"
    And I access requisition on approval page
    Then I should see RnR Header
    And I should see approved quantity
    When I update approve quantity and verify total cost as "290"
    And I add comments without save
    Then I should see blank comment section
    When I add "This is urgent" comment
    Then I should see "medicalofficer" comments as "This is urgent"
    And I should see correct total after authorize
    When I approve requisition
    Then I should see no requisition pending message
    When I logout
    And I am logged in as "storeincharge"
    And I access requisition on approval page
    Then I should see RnR Header
    And I should see approved quantity from lower hierarchy
    When I update approve quantity and verify total cost as "100"
    Then I should see correct total after authorize
    When I approve requisition
    Then I should see no requisition pending message
    When I logout
    And I am logged in as "lmu"
    And I access convert to order page
    Then I should see pending order list
    When I convert to order
    And I access view orders page
    Then I should see ordered list
    When I do not have anything to pack to ship
    Then I should not see download link
