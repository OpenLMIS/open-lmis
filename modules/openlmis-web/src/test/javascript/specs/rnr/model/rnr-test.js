/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe('R&R test', function () {

  function createRegularRnr(json, columnsArray) {
    if (json == undefined)  json = {};
    json.emergency = false;
    var regularRnr = new Rnr(json, columnsArray, 3);
    return regularRnr;
  }

  it('should sort non full supply line items during init', function () {
    var rnrLineItem1 = {productCategoryDisplayOrder: 3};
    var rnrLineItem2 = {productCategoryDisplayOrder: 1};
    var rnrLineItem3 = {productCategoryDisplayOrder: 2};
    var rnrJson = {'nonFullSupplyLineItems': [rnrLineItem1, rnrLineItem2, rnrLineItem3]}

    var rnr = createRegularRnr();
    jQuery.extend(rnr, rnrJson);
    rnr.init();

    expect(rnr.nonFullSupplyLineItems.length).toEqual(3);
    expect(rnr.nonFullSupplyLineItems[0].productCategoryDisplayOrder).toEqual(1);
    expect(rnr.nonFullSupplyLineItems[1].productCategoryDisplayOrder).toEqual(2);
    expect(rnr.nonFullSupplyLineItems[2].productCategoryDisplayOrder).toEqual(3);
  });

  it('should leave the R&R only with basic information', function () {
    var lineItem1 = new RegularRnrLineItem();
    var lineItem2 = new RegularRnrLineItem();
    spyOn(lineItem1, 'reduceForApproval');
    spyOn(lineItem2, 'reduceForApproval');
    spyOn(_, 'pick').andCallThrough();

    var rnr = createRegularRnr({ fullSupplyLineItems: [lineItem1, lineItem2]}, []);

    rnr.reduceForApproval();

    expect(lineItem1.reduceForApproval).toHaveBeenCalled();
    expect(lineItem2.reduceForApproval).toHaveBeenCalled();
    expect(_.pick).toHaveBeenCalledWith(rnr, 'id', 'fullSupplyLineItems', 'nonFullSupplyLineItems');
  });

  it("should set skipAll to false on rnr creation", function () {
    var rnr = new Rnr({}, []);
    expect(rnr.skipAll).toBeFalsy();
  });

  it("should set numberOfMonths on rnr creation", function () {
    var rnr = new Rnr({}, [], 5);
    expect(rnr.numberOfMonths).toEqual(5);
  });

  it('should prepare line item objects inside rnr', function () {
    var lineItemSpy = spyOn(window, 'RegularRnrLineItem');
    var lineItem1 = {"lineItem": "lineItem1"};
    var lineItem2 = {};

    var rnr = {emergency: false, 'fullSupplyLineItems': [lineItem1], 'nonFullSupplyLineItems': [lineItem2], status: 'status'};
    var constructedRnr = createRegularRnr(rnr, null);

    expect(lineItemSpy).toHaveBeenCalledWith(lineItem1, 3, null, 'status');
    expect(lineItemSpy.calls.length).toEqual(2);
    expect(constructedRnr.fullSupplyLineItems.length).toEqual(1);
    expect(constructedRnr.nonFullSupplyLineItems.length).toEqual(1);
  });

  it('should set rnrColumns in scope', function () {
    var rnrColumns = [
      {'name': 'beginningBalance'}
    ];

    var constructedRnr = createRegularRnr({}, rnrColumns);

    expect(constructedRnr.programRnrColumnList).toEqual(rnrColumns);
  });

  it('should validate R&R full supply line items and return false if required field missing', function () {
    var lineItem1 = {"lineItem": "lineItem1"};
    var lineItem2 = {};
    var rnr = {period: {numberOfMonths: 3}, status: 'INITIATED', 'fullSupplyLineItems': [lineItem1, lineItem2]};
    var programRnrColumnList = [
      {"name": "beginningBalance"},
      {"name": "noOfPatients"}
    ];

    rnr = createRegularRnr(rnr, programRnrColumnList);
    spyOn(rnr.fullSupplyLineItems[0], 'validateRequiredFieldsForFullSupply').andReturn(false);
    spyOn(rnr.fullSupplyLineItems[1], 'validateRequiredFieldsForFullSupply').andReturn(true);

    var errorMessage = rnr.validateFullSupply();

    expect(rnr.fullSupplyLineItems[0].validateRequiredFieldsForFullSupply.calls.length).toEqual(1);
    expect(rnr.fullSupplyLineItems[1].validateRequiredFieldsForFullSupply.calls.length).toEqual(0);
    expect(errorMessage).toEqual('error.rnr.validation');
  });

  it("should not validate R&R full supply line items if line item is skipped", function () {
    var lineItem1 = {"lineItem": "lineItem1", skipped: true};
    var rnr = {period: {numberOfMonths: 3}, status: 'INITIATED', 'fullSupplyLineItems': [lineItem1]};

    var programRnrColumnList = [
      {"name": "beginningBalance"},
      {"name": "noOfPatients"}
    ];
    rnr = createRegularRnr(rnr, programRnrColumnList);
    spyOn(rnr.fullSupplyLineItems[0], 'validateRequiredFieldsForFullSupply').andReturn(false);

    var errorMessage = rnr.validateFullSupply();
    expect(errorMessage).toEqual('');
  });

  it('should validate R&R full supply line items and return true if required field is not missing', function () {
    var lineItem1 = {"lineItem": "lineItem1"};
    var lineItem2 = {};
    var rnr = {period: {numberOfMonths: 3}, status: 'INITIATED', 'fullSupplyLineItems': [lineItem1, lineItem2]};
    var programRnrColumnList = [
      {"name": "beginningBalance"},
      {"name": "noOfPatients"}
    ];

    rnr = createRegularRnr(rnr, programRnrColumnList);
    spyOn(rnr.fullSupplyLineItems[0], 'validateRequiredFieldsForFullSupply').andReturn(true);
    spyOn(rnr.fullSupplyLineItems[1], 'validateRequiredFieldsForFullSupply').andReturn(true);

    var errorMessage = rnr.validateFullSupply();

    expect(rnr.fullSupplyLineItems[0].validateRequiredFieldsForFullSupply.calls.length).toEqual(1);
    expect(rnr.fullSupplyLineItems[1].validateRequiredFieldsForFullSupply.calls.length).toEqual(1);
    expect(errorMessage).toEqual('');
  });

  it('should validate R&R full supply line items and return false if required field is not missing but arithmetically invalid', function () {
    var lineItem1 = {"lineItem": "lineItem1"};
    var lineItem2 = {};
    var rnr = {period: {numberOfMonths: 3}, status: 'INITIATED', 'fullSupplyLineItems': [lineItem1, lineItem2]};
    var programRnrColumnList = [
      {"name": "beginningBalance"},
      {"name": "noOfPatients"}
    ];

    rnr = createRegularRnr(rnr, programRnrColumnList);
    spyOn(rnr.fullSupplyLineItems[0], 'validateRequiredFieldsForFullSupply').andReturn(true);
    spyOn(rnr.fullSupplyLineItems[0], 'formulaValid').andReturn(false);
    spyOn(rnr.fullSupplyLineItems[1], 'validateRequiredFieldsForFullSupply').andReturn(true);
    spyOn(rnr.fullSupplyLineItems[1], 'formulaValid').andReturn(false);


    var errorMessage = rnr.validateFullSupply();

    expect(rnr.fullSupplyLineItems[0].validateRequiredFieldsForFullSupply.calls.length).toEqual(1);
    expect(rnr.fullSupplyLineItems[0].formulaValid.calls.length).toEqual(1);
    expect(rnr.fullSupplyLineItems[1].validateRequiredFieldsForFullSupply.calls.length).toEqual(0);
    expect(rnr.fullSupplyLineItems[1].formulaValid.calls.length).toEqual(0);
    expect(errorMessage).toEqual('error.rnr.validation');
  });

  it('should validate R&R full supply line items and return true if required field is not missing and arithmetically valid', function () {
    var lineItem1 = {"lineItem": "lineItem1"};
    var lineItem2 = {};
    var rnr = {period: {numberOfMonths: 3}, status: 'INITIATED', 'fullSupplyLineItems': [lineItem1, lineItem2]};
    var programRnrColumnList = [
      {"name": "beginningBalance"},
      {"name": "noOfPatients"}
    ];

    rnr = createRegularRnr(rnr, programRnrColumnList);
    spyOn(rnr.fullSupplyLineItems[0], 'validateRequiredFieldsForFullSupply').andReturn(true);
    spyOn(rnr.fullSupplyLineItems[0], 'formulaValid').andReturn(true);
    spyOn(rnr.fullSupplyLineItems[1], 'validateRequiredFieldsForFullSupply').andReturn(true);
    spyOn(rnr.fullSupplyLineItems[1], 'formulaValid').andReturn(true);

    var errorMessage = rnr.validateFullSupply();

    expect(rnr.fullSupplyLineItems[0].validateRequiredFieldsForFullSupply.calls.length).toEqual(1);
    expect(rnr.fullSupplyLineItems[0].formulaValid.calls.length).toEqual(1);
    expect(rnr.fullSupplyLineItems[1].validateRequiredFieldsForFullSupply.calls.length).toEqual(1);
    expect(rnr.fullSupplyLineItems[1].formulaValid.calls.length).toEqual(1);
    expect(errorMessage).toEqual('');
  });

  it('should validate R&R non full supply line items and return true if required fields are not missing', function () {
    var lineItem1 = {"lineItem": "lineItem1"};
    var lineItem2 = {};
    var rnr = {period: {numberOfMonths: 3}, status: 'INITIATED', 'nonFullSupplyLineItems': [lineItem1, lineItem2]};

    var programRnrColumnList = [
      {"name": "quantityRequested"},
      {"name": "reasonForRequestedQuantity"}
    ];
    rnr = createRegularRnr(rnr, programRnrColumnList);
    spyOn(rnr.nonFullSupplyLineItems[0], 'validateRequiredFieldsForNonFullSupply').andReturn(true);
    spyOn(rnr.nonFullSupplyLineItems[1], 'validateRequiredFieldsForNonFullSupply').andReturn(true);

    var errorMessage = rnr.validateNonFullSupply();

    expect(rnr.nonFullSupplyLineItems[0].validateRequiredFieldsForNonFullSupply.calls.length).toEqual(1);
    expect(rnr.nonFullSupplyLineItems[1].validateRequiredFieldsForNonFullSupply.calls.length).toEqual(1);
    expect(errorMessage).toEqual('');
  });

  it('should validate R&R non full supply line items and return false if required fields are missing', function () {
    var lineItem1 = {"lineItem": "lineItem1"};
    var lineItem2 = {};

    var rnr = {period: {numberOfMonths: 3}, status: 'INITIATED', 'nonFullSupplyLineItems': [lineItem1, lineItem2]};

    var programRnrColumnList = [
      {"name": "quantityRequested"},
      {"name": "reasonForRequestedQuantity"}
    ];
    rnr = createRegularRnr(rnr, programRnrColumnList);
    spyOn(rnr.nonFullSupplyLineItems[0], 'validateRequiredFieldsForNonFullSupply').andReturn(false);
    spyOn(rnr.nonFullSupplyLineItems[1], 'validateRequiredFieldsForNonFullSupply').andReturn(true);

    var errorMessage = rnr.validateNonFullSupply();

    expect(rnr.nonFullSupplyLineItems[0].validateRequiredFieldsForNonFullSupply.calls.length).toEqual(1);
    expect(rnr.nonFullSupplyLineItems[1].validateRequiredFieldsForNonFullSupply.calls.length).toEqual(0);
    expect(errorMessage).toEqual('error.rnr.validation');
  });

  it('should fill normalized consumption and update cost', function () {
    var rnrLineItem = new RegularRnrLineItem({}, 1, null, 'INITIATED');
    var rnr = createRegularRnr();
    spyOn(rnrLineItem, 'fillNormalizedConsumption');
    spyOn(rnr, 'fillCost');

    rnr.fillNormalizedConsumption(rnrLineItem);

    expect(rnrLineItem.fillNormalizedConsumption).toHaveBeenCalled();
    expect(rnr.fillCost).toHaveBeenCalled();
  });

  it('should fill Consumption Or StockInHand and update cost', function () {
    var rnrLineItem = new RegularRnrLineItem({}, 1, null, 'INITIATED');
    var rnr = createRegularRnr();
    spyOn(rnrLineItem, 'fillConsumptionOrStockInHand');
    spyOn(rnr, 'fillCost');

    rnr.fillConsumptionOrStockInHand(rnrLineItem);

    expect(rnrLineItem.fillConsumptionOrStockInHand).toHaveBeenCalled();
    expect(rnr.fillCost).toHaveBeenCalled();
  });

  it('should fill packs to ship based on quantity requested or calculated quantity and update cost', function () {
    var rnrLineItem = new RegularRnrLineItem({}, 1, null, 'INITIATED');
    var rnr = createRegularRnr();
    spyOn(rnrLineItem, 'fillPacksToShip');
    spyOn(rnr, 'fillCost');

    rnr.fillPacksToShip(rnrLineItem);

    expect(rnrLineItem.fillPacksToShip).toHaveBeenCalled();
    expect(rnr.fillCost).toHaveBeenCalled();
  });

  describe('Calculate Total Cost For Rnr', function () {
    it('should calculate fullSupplyItemsSubmittedCost', function () {
      var rnr = createRegularRnr();

      var rnrLineItem1 = new RegularRnrLineItem({"productCode": "p1"}, 2, null, 'INITIATED');
      rnrLineItem1.cost = 100;
      var rnrLineItem2 = new RegularRnrLineItem({"productCode": "p2"}, 2, null, 'INITIATED');
      rnrLineItem2.cost = 60;
      var rnrLineItem3 = new RegularRnrLineItem({"productCode": "p3"}, 2, null, 'INITIATED');
      rnrLineItem3.cost = 160;

      rnr.fullSupplyLineItems = [rnrLineItem1, rnrLineItem2, rnrLineItem3];
      rnr.calculateFullSupplyItemsSubmittedCost();

      expect(rnr.fullSupplyItemsSubmittedCost).toEqual('320.00');
    });

    it("should not include skipped line items in total cost caluculations", function () {
      var rnr = createRegularRnr();

      var rnrLineItem1 = new RegularRnrLineItem({productCode: "p1", skipped: true}, 2, null, 'INITIATED');
      rnrLineItem1.cost = 100;
      rnr.fullSupplyLineItems = [rnrLineItem1];
      rnr.calculateFullSupplyItemsSubmittedCost();

      expect(rnr.fullSupplyItemsSubmittedCost).toEqual('0.00');
    });

    it('should calculate nonFullSupplyItemsSubmittedCost', function () {
      var rnr = createRegularRnr();

      var rnrLineItem1 = new RegularRnrLineItem({"productCode": "p1"}, 2, null, 'INITIATED');
      rnrLineItem1.cost = 100;
      var rnrLineItem2 = new RegularRnrLineItem({"productCode": "p2"}, 2, null, 'INITIATED');
      rnrLineItem2.cost = 60;
      var rnrLineItem3 = new RegularRnrLineItem({"productCode": "p3"}, 2, null, 'INITIATED');
      rnrLineItem3.cost = 160;

      rnr.nonFullSupplyLineItems = [rnrLineItem1, rnrLineItem2, rnrLineItem3];

      rnr.calculateNonFullSupplyItemsSubmittedCost();

      expect(rnr.nonFullSupplyItemsSubmittedCost).toEqual('320.00');
    });

    it('should update cost for full supply line items', function () {
      var rnr = createRegularRnr({emergency: false});
      spyOn(rnr, 'calculateFullSupplyItemsSubmittedCost');

      rnr.fillCost(true);

      expect(rnr.calculateFullSupplyItemsSubmittedCost).toHaveBeenCalled();
    });

    it('should update cost for non-full supply line items', function () {
      var rnr = createRegularRnr({emergency: false});
      spyOn(rnr, 'calculateNonFullSupplyItemsSubmittedCost');

      rnr.fillCost(false);

      expect(rnr.calculateNonFullSupplyItemsSubmittedCost).toHaveBeenCalled();
    });
  });

  it('should validate rnr full supply line items for approval', function () {
    var rnr = createRegularRnr({emergency: false});
    rnr.fullSupplyLineItems = [
      {'quantityApproved': '', 'reasonForRequestedQuantity': ''}
    ];

    var error = rnr.validateFullSupplyForApproval();

    expect(error).toEqual('error.rnr.validation');
  });

  it('should validate rnr full supply line items for approval and return true if required fields not missing', function () {
    var rnr = createRegularRnr({emergency: false});
    rnr.fullSupplyLineItems = [
      {'quantityApproved': '23', 'reasonForRequestedQuantity': 'some message'}
    ];

    var error = rnr.validateFullSupplyForApproval();

    expect(error).toEqual('');
  });

  it('should validate rnr full supply line items for approval', function () {
    var rnr = createRegularRnr({emergency: false});
    rnr.nonFullSupplyLineItems = [
      {'quantityApproved': '', 'reasonForRequestedQuantity': ''}
    ];

    var error = rnr.validateNonFullSupplyForApproval();

    expect(error).toEqual('error.rnr.validation');
  });

  it('should validate rnr full supply line items for approval and return true if required fields not missing', function () {
    var rnr = createRegularRnr({emergency: false});
    rnr.nonFullSupplyLineItems = [
      {'quantityApproved': '23', 'reasonForRequestedQuantity': 'some message'}
    ];

    var error = rnr.validateNonFullSupplyForApproval();

    expect(error).toEqual('');
  });

  it('should find indexes of invalid full supply line items', function () {
    var rnr = createRegularRnr({emergency: false});
    var rnrLineItem1 = new RegularRnrLineItem();
    var rnrLineItem2 = new RegularRnrLineItem();
    var rnrLineItem3 = new RegularRnrLineItem();
    spyOn(rnrLineItem1, "valid").andReturn(false);
    spyOn(rnrLineItem2, "valid").andReturn(true);
    spyOn(rnrLineItem3, "valid").andReturn(false);
    rnr.fullSupplyLineItems = [rnrLineItem1, rnrLineItem2, rnrLineItem3];

    var fullSupplyErrorLineItemIndexes = rnr.getFullSupplyErrorLineItemIndexes();
    expect(fullSupplyErrorLineItemIndexes).toEqual([0, 2]);
  });

  it('should find indexes of invalid non full supply line items', function () {
    var rnr = createRegularRnr({emergency: false});
    var rnrLineItem1 = new RegularRnrLineItem();
    var rnrLineItem2 = new RegularRnrLineItem();
    var rnrLineItem3 = new RegularRnrLineItem();
    spyOn(rnrLineItem1, "valid").andReturn(false);
    spyOn(rnrLineItem2, "valid").andReturn(true);
    spyOn(rnrLineItem3, "valid").andReturn(false);
    rnr.nonFullSupplyLineItems = [rnrLineItem1, rnrLineItem2, rnrLineItem3];

    var nonFullSupplyErrorLineItemIndexes = rnr.getNonFullSupplyErrorLineItemIndexes();
    expect(nonFullSupplyErrorLineItemIndexes).toEqual([0, 2]);
  });

  it('should calculate pages which have errors on approve', function () {
    var rnr = createRegularRnr({"id": "1", "fullSupplyLineItems": [
      {id: 1},
      {id: 2},
      {id: 3}
    ], period: {numberOfMonths: 7}, emergency: false}, null);

    spyOn(rnr, 'getNonFullSupplyErrorLineItemIndexes').andReturn([0, 5]);
    spyOn(rnr, 'getFullSupplyErrorLineItemIndexes').andReturn([7, 16]);

    var errorPages = rnr.getErrorPages(5);

    expect(errorPages).toEqual({nonFullSupply: [1, 2], fullSupply: [2, 4], regimen: [ ]});
    expect(rnr.getNonFullSupplyErrorLineItemIndexes).toHaveBeenCalled();
    expect(rnr.getFullSupplyErrorLineItemIndexes).toHaveBeenCalled();
  });

  it('should prepare period display name', function () {
    var rnr = createRegularRnr({"id": "1", period: {"name": "Period 1", "stringStartDate": "16/01/2013", "stringEndDate": "30/04/2013"}}, null)
    expect(rnr.periodDisplayName()).toEqual('16/01/2013 - 30/04/2013');
  });

  it('should set budget exceed flag if applicable', function () {
    var rnr = createRegularRnr({"id": "1", period: {"name": "Period 1", "stringStartDate": "16/01/2013", "stringEndDate": "30/04/2013"}}, null);
    rnr.fullSupplyItemsSubmittedCost = 100;
    rnr.nonFullSupplyItemsSubmittedCost = 200;
    rnr.allocatedBudget = 100;
    rnr.program = {budgetingApplies: true}

    rnr.calculateTotalLineItemCost()

    expect(rnr.costExceedsBudget).toBeTruthy();
  });


});

