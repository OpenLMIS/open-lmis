describe('RnrLineItem', function () {
  beforeEach(module('rnr'));

  describe('Calculate consumption', function () {
    var programRnrColumnList;

    beforeEach(function () {
      programRnrColumnList = [
        {"indicator":"C", "name":"quantityDispensed", "source":{"name":"CALCULATED"}}
      ];
    });

    it('should calculate consumption', function () {
      var rnrLineItem = new RnrLineItem({"beginningBalance":5, "quantityReceived":20, "quantityDispensed":null, "totalLossesAndAdjustments":5, "stockInHand":10});

      rnrLineItem.calculateConsumption(programRnrColumnList);

      expect(rnrLineItem.quantityDispensed).toEqual(20);
    });

    it('should not calculate consumption when one of the dependant columns is not set', function () {
      var lineItem = {"beginningBalance":1, "quantityReceived":2, "quantityDispensed":null, "totalLossesAndAdjustments":3, "stockInHand":null};
      var rnrLineItem = new RnrLineItem(lineItem);

      rnrLineItem.calculateConsumption(programRnrColumnList);

      expect(rnrLineItem.quantityDispensed).toEqual(null);
    });

    it('should not calculate consumption when it is not a calculated field', function () {
      programRnrColumnList = [
        {"indicator":"C", "name":"quantityDispensed", "source":{"name":"USER_INPUT"}}
      ];
      var rnrLineItem = new RnrLineItem();

      rnrLineItem.calculateConsumption(programRnrColumnList);

      expect(rnrLineItem.quantityDispensed).toEqual(null);
    });
  });

  describe('Calculate stock in hand', function () {
    var programRnrColumnList;
    beforeEach(function () {
      programRnrColumnList = [
        {"indicator":"E", "name":"stockInHand", "source":{"name":"CALCULATED"}}
      ];
    });

    it('should calculate stock in hand when all values are 0 - NaN check', function () {
      var rnrLineItem = new RnrLineItem({"beginningBalance":0, "quantityReceived":0, "quantityDispensed":0, "totalLossesAndAdjustments":0, "stockInHand":null});

      rnrLineItem.calculateStockInHand(programRnrColumnList);

      expect(rnrLineItem.stockInHand).toEqual(0);
    });

    it('should calculate stock in hand', function () {
      var rnrLineItem = new RnrLineItem({"beginningBalance":10, "quantityReceived":10, "quantityDispensed":10, "totalLossesAndAdjustments":1, "stockInHand":null});

      rnrLineItem.calculateStockInHand(programRnrColumnList);

      expect(rnrLineItem.stockInHand).toEqual(11);
    });

    it('should not calculate stock in hand when one of the dependant columns is not set', function () {
      var rnrLineItem = new RnrLineItem({"beginningBalance":1, "quantityReceived":2, "quantityDispensed":1, "totalLossesAndAdjustments":null, "stockInHand":null});

      rnrLineItem.calculateStockInHand(programRnrColumnList);

      expect(rnrLineItem.stockInHand).toEqual(null);
    });

    it('should not calculate stock in hand when it is not a calculated field', function () {
      programRnrColumnList = [
        {"indicator":"E", "name":"stockInHand", "source":{"name":"USER_INPUT"}}
      ];
      var rnrLineItem = new RnrLineItem();

      rnrLineItem.calculateStockInHand(programRnrColumnList);

      expect(rnrLineItem.stockInHand).toEqual(null);
    });


  });

  describe('Calculate normalized consumption', function () {
    var programRnrColumnList;
    beforeEach(function () {
      programRnrColumnList = [
        {"indicator":"A", "name":"beginningBalance", "source":{"name":"USER_INPUT"}},
        {"indicator":"B", "name":"quantityReceived", "source":{"name":"USER_INPUT"}},
        {"indicator":"C", "name":"quantityDispensed", "source":{"name":"CALCULATED"}},
        {"indicator":"D", "name":"lossesAndAdjustments", "source":{"name":"USER_INPUT"}},
        {"indicator":"E", "name":"stockInHand", "source":{"name":"CALCULATED"}},
        {"indicator":"F", "name":"newPatientCount", "source":{"name":"USER_INPUT"}},
        {"indicator":"X", "name":"stockOutDays", "source":{"name":"USER_INPUT"}}
      ];
    });

    it('should calculate normalized consumption', function () {
      var lineItem = {"beginningBalance":1, "quantityReceived":10, "quantityDispensed":5, "totalLossesAndAdjustments":-4, "stockOutDays":5, "newPatientCount":10, "dosesPerMonth":30, "dosesPerDispensingUnit":28};
      var rnrLineItem = new RnrLineItem(lineItem);

      rnrLineItem.calculateNormalizedConsumption(programRnrColumnList);

      expect(rnrLineItem.normalizedConsumption).toEqual(65);
    });

    it('should not calculate normalized consumption when newPatientCount is displayed but not set', function () {
      var lineItem = {"beginningBalance":1, "quantityReceived":10, "quantityDispensed":4, "totalLossesAndAdjustments":4, "stockOutDays":5, "newPatientCount":null};
      var rnrLineItem = new RnrLineItem(lineItem);

      rnrLineItem.calculateNormalizedConsumption(programRnrColumnList);

      expect(rnrLineItem.normalizedConsumption).toEqual(null);
    });

    it('should not calculate normalized consumption when consumption is empty', function () {
      var lineItem = {"beginningBalance":1, "quantityReceived":2, "quantityDispensed":null, "totalLossesAndAdjustments":3};
      var rnrLineItem = new RnrLineItem(lineItem);

      rnrLineItem.calculateNormalizedConsumption(programRnrColumnList);

      expect(rnrLineItem.normalizedConsumption).toEqual(null);
    });

    it('should not calculate normalized consumption when stockOutDays is not set', function () {
      var lineItem = {"beginningBalance":1, "quantityReceived":10, "quantityDispensed":13, "totalLossesAndAdjustments":4, "stockOutDays":null, "newPatientCount":10};
      var rnrLineItem = new RnrLineItem(lineItem);

      rnrLineItem.calculateNormalizedConsumption(programRnrColumnList);

      expect(rnrLineItem.normalizedConsumption).toEqual(null);
    });

    it('should calculate normalized consumption when facility is stocked out for the entire reporting period', function () {
      var lineItem = {"beginningBalance":1, "quantityReceived":10, "quantityDispensed":13, "totalLossesAndAdjustments":4, "stockOutDays":90, "newPatientCount":10, "dosesPerMonth":30, "dosesPerDispensingUnit":28};
      var rnrLineItem = new RnrLineItem(lineItem);

      rnrLineItem.calculateNormalizedConsumption(programRnrColumnList);

      expect(rnrLineItem.normalizedConsumption).toEqual(73);
    });

    it('should calculate normalized consumption when newPatientCount is not in the template', function () {
      programRnrColumnList = [
        {"indicator":"A", "name":"beginningBalance", "source":{"name":"USER_INPUT"}},
        {"indicator":"B", "name":"quantityReceived", "source":{"name":"USER_INPUT"}},
        {"indicator":"C", "name":"quantityDispensed", "source":{"name":"CALCULATED"}},
        {"indicator":"D", "name":"lossesAndAdjustments", "source":{"name":"USER_INPUT"}},
        {"indicator":"E", "name":"stockInHand", "source":{"name":"CALCULATED"}},
        {"indicator":"X", "name":"stockOutDays", "source":{"name":"USER_INPUT"}}
      ];
      var lineItem = {"beginningBalance":1, "quantityReceived":10, "quantityDispensed":5, "totalLossesAndAdjustments":-4, "stockOutDays":5, "newPatientCount":null, "dosesPerMonth":30, "dosesPerDispensingUnit":28};
      var rnrLineItem = new RnrLineItem(lineItem);

      rnrLineItem.calculateNormalizedConsumption(programRnrColumnList);

      expect(rnrLineItem.normalizedConsumption).toEqual(5);
    });
  });

  describe('Calculate AMC', function () {
    it('should set AMC to be equal to normalized consumption', function () {
      var lineItem = {"normalizedConsumption":10};
      var rnrLineItem = new RnrLineItem(lineItem);

      rnrLineItem.calculateAMC();

      expect(rnrLineItem.amc).toEqual(10);
    });

    it('should not calculate AMC when normalized consumption is not present', function () {
      var lineItem = {"normalizedConsumption":null};
      var rnrLineItem = new RnrLineItem(lineItem);

      rnrLineItem.calculateAMC();

      expect(rnrLineItem.amc).toEqual(null);
    });
  });

  describe('Calculate Max Stock Quantity', function () {
    beforeEach(function () {
      programRnrColumnList = [
        {"indicator":"A", "name":"beginningBalance", "source":{"name":"USER_INPUT"}},
        {"indicator":"B", "name":"quantityReceived", "source":{"name":"USER_INPUT"}},
        {"indicator":"C", "name":"quantityDispensed", "source":{"name":"CALCULATED"}},
        {"indicator":"D", "name":"lossesAndAdjustments", "source":{"name":"USER_INPUT"}},
        {"indicator":"E", "name":"stockInHand", "source":{"name":"CALCULATED"}},
        {"indicator":"F", "name":"newPatientCount", "source":{"name":"USER_INPUT"}},
        {"indicator":"X", "name":"stockOutDays", "source":{"name":"USER_INPUT"}}
      ];
    });

    it('should calculate maxStockQuantity', function () {
      var lineItem = {"amc":15, "maxMonthsOfStock":3};
      var rnrLineItem = new RnrLineItem(lineItem);

      rnrLineItem.calculateMaxStockQuantity();

      expect(rnrLineItem.maxStockQuantity).toEqual(45);
    });

    it('should not calculate maxStockQuantity if amc is not available', function () {
      var lineItem = {"maxMonthsOfStock":3};
      var rnrLineItem = new RnrLineItem(lineItem);

      rnrLineItem.calculateMaxStockQuantity();

      expect(rnrLineItem.maxStockQuantity).toEqual(null);
    });
  });

  describe('Calculate Calculated Order Quantity', function () {
    it('should calculate calculatedOrderQuantity', function () {
      var lineItem = {"stockInHand":7, "maxStockQuantity":10};
      var rnrLineItem = new RnrLineItem(lineItem);

      rnrLineItem.calculateCalculatedOrderQuantity();

      expect(rnrLineItem.calculatedOrderQuantity).toEqual(3);
    });

    it('should not calculate calculatedOrderQuantity when stock in hand is not present', function () {
      var lineItem = {"stockInHand":null, "maxStockQuantity":3};
      var rnrLineItem = new RnrLineItem(lineItem);

      rnrLineItem.calculateCalculatedOrderQuantity();

      expect(rnrLineItem.calculatedOrderQuantity).toEqual(null);
    });

    it('should not calculate calculatedOrderQuantity when maxStockQuantity is not present', function () {
      var lineItem = {"stockInHand":7, "maxStockQuantity":null};
      var rnrLineItem = new RnrLineItem(lineItem);

      rnrLineItem.calculateCalculatedOrderQuantity();

      expect(rnrLineItem.calculatedOrderQuantity).toEqual(null);
    });

    it('should calculate calculatedOrderQuantity to be 0 when value goes negative', function () {
      var lineItem = {"stockInHand":10, "maxStockQuantity":3};
      var rnrLineItem = new RnrLineItem(lineItem);

      rnrLineItem.calculateCalculatedOrderQuantity();

      expect(rnrLineItem.calculatedOrderQuantity).toEqual(0);
    });
  });

  describe('Calculate Packs To Ship', function () {
    beforeEach(function () {
      programRnrColumnList = [
        {"indicator":"A", "name":"beginningBalance", "source":{"name":"USER_INPUT"}},
        {"indicator":"B", "name":"quantityReceived", "source":{"name":"USER_INPUT"}},
        {"indicator":"C", "name":"quantityDispensed", "source":{"name":"USER_INPUT"}},
        {"indicator":"D", "name":"lossesAndAdjustments", "source":{"name":"USER_INPUT"}},
        {"indicator":"E", "name":"stockInHand", "source":{"name":"CALCULATED"}},
        {"indicator":"F", "name":"newPatientCount", "source":{"name":"USER_INPUT"}},
        {"indicator":"X", "name":"stockOutDays", "source":{"name":"USER_INPUT"}}
      ];
    });

    xit('should calculate packsToShip when calculated quantity is available and requested quantity is null', function () {
      var lineItem = {"beginningBalance":5, "quantityReceived":20, "quantityDispensed":15, "newPatientCount":0,
        "stockOutDays":0, "totalLossesAndAdjustments":-5, "stockInHand":null, "dosesPerMonth":10,
        "dosesPerDispensingUnit":10, "maxMonthsOfStock":3, "packSize":12};

      var rnrLineItem = new RnrLineItem(lineItem);

      rnrLineItem.calculatePacksToShip(null, programRnrColumnList);
      expect(3).toEqual(rnrLineItem.packsToShip);
    });

    it('should calculate packsToShip for the given quantity', function () {
      var lineItem = {"packSize":12};
      var rnrLineItem = new RnrLineItem(lineItem);
      spyOn(rnrLineItem, 'applyRoundingRulesToPacksToShip');

      rnrLineItem.calculatePacksToShip(25);

      expect(rnrLineItem.packsToShip).toEqual(2);
      expect(rnrLineItem.applyRoundingRulesToPacksToShip).toHaveBeenCalledWith(25);
    });

    xit('should set packsToShip when requested quantity is available', function () {
      var lineItem = {"beginningBalance":5, "quantityReceived":20, "quantityDispensed":15, "newPatientCount":0,
        "stockOutDays":3, "totalLossesAndAdjustments":5, "stockInHand":null, "dosesPerMonth":10, "dosesPerDispensingUnit":10,
        "maxMonthsOfStock":3, "packSize":12, "quantityRequested":100};

      var rnrLineItem = new RnrLineItem(lineItem);

      rnrLineItem.fill(null, programRnrColumnList);
      expect(8).toEqual(rnrLineItem.packsToShip);
    });
  });

  describe('Apply rounding rules to packs to ship', function () {
    it('should set packsToShip to one when packsToShip is zero and roundToZero is false', function () {
      var lineItem = {"packsToShip":0, "roundToZero":false, "packSize":12, "packRoundingThreshold":7};
      var rnrLineItem = new RnrLineItem(lineItem);

      rnrLineItem.applyRoundingRulesToPacksToShip(5);

      expect(rnrLineItem.packsToShip).toEqual(1);
    });

    it('should set packsToShip to zero when packsToShip is zero and roundToZero is true', function () {
      var lineItem = {"packsToShip":0, "roundToZero":true, "packSize":12, "packRoundingThreshold":7};
      var rnrLineItem = new RnrLineItem(lineItem);

      rnrLineItem.applyRoundingRulesToPacksToShip(5);

      expect(rnrLineItem.packsToShip).toEqual(0);
    });

    it('should increment packsToShip by one when number of remaining items is greater than packRoundingThreshold ', function () {
      var lineItem = {"packsToShip":2, "packSize":12, "packRoundingThreshold":7};
      var rnrLineItem = new RnrLineItem(lineItem);

      rnrLineItem.applyRoundingRulesToPacksToShip(8);

      expect(rnrLineItem.packsToShip).toEqual(3);
    });

    it('should not increment packsToShip when number of remaining items is greater than packRoundingThreshold ', function () {
      var lineItem = {"packsToShip":2, "packSize":12, "packRoundingThreshold":7};
      var rnrLineItem = new RnrLineItem(lineItem);

      rnrLineItem.applyRoundingRulesToPacksToShip(6);

      expect(rnrLineItem.packsToShip).toEqual(2);
    });
  });

  describe('Calculate Cost', function () {
    it('should set cost when pricePerPack and packsToShip are available', function () {
      var lineItem = {"packsToShip":11, "price":200};
      var rnrLineItem = new RnrLineItem(lineItem);

      rnrLineItem.calculateCost();

      expect(rnrLineItem.cost).toEqual(2200);
    });

    it('should set cost to zero when packsToShip is not available', function () {
      var lineItem = {"price":200};
      var rnrLineItem = new RnrLineItem(lineItem);

      rnrLineItem.calculateCost();

      expect(rnrLineItem.cost).toEqual(0);
    });
  });

  describe('Calculate Full Supply Items Submitted Cost For Rnr', function () {
    it('should calculate fullSupplyItemsSubmittedCost', function () {
      var rnrLineItem1 = new RnrLineItem({"cost":100});
      var rnrLineItem2 = new RnrLineItem({"cost":60});
      var rnrLineItem3 = new RnrLineItem({"cost":160});

      var rnr = new Object();
      rnr.lineItems = new Array(rnrLineItem1, rnrLineItem2, rnrLineItem3);
      rnrLineItem1.calculateFullSupplyItemsSubmittedCost(rnr);
      expect(rnr.fullSupplyItemsSubmittedCost).toEqual(320);
    });
  });

  describe('Losses and adjustment for line item', function () {
    it("should re evaluate total losses and adjustments for line item", function () {
      var rnr = new Object();
      var programRnrColumnList = new Object();

      var lossAndAdjustment = {"type":{"name":"CLINIC_RETURN", "additive":true}, "quantity":45};
      var lineItem = {"id":"1", "totalLossesAndAdjustments":40, lossesAndAdjustments:[lossAndAdjustment]};
      var rnrLineItem = new RnrLineItem(lineItem);

      spyOn(rnrLineItem, "updateTotalLossesAndAdjustment");

      rnrLineItem.reEvaluateTotalLossesAndAdjustments(rnr, new Object());

      expect(rnrLineItem.updateTotalLossesAndAdjustment).toHaveBeenCalledWith(45, true, rnr, programRnrColumnList);
    });

    it("should remove losses and adjustments for line item and update total losses and adjustments", function () {
      var rnr = new Object();
      var programRnrColumnList = new Object();
      var lossAndAdjustment = {"type":{"name":"CLINIC_RETURN", "additive":true}, "quantity":45};
      var lineItem = {"id":"1", "totalLossesAndAdjustments":45, lossesAndAdjustments:[lossAndAdjustment]};
      var rnrLineItem = new RnrLineItem(lineItem);

      spyOn(rnrLineItem, "updateTotalLossesAndAdjustment");

      rnrLineItem.removeLossAndAdjustment(rnrLineItem.lossesAndAdjustments[0], rnr, programRnrColumnList);

      expect(rnrLineItem.updateTotalLossesAndAdjustment).toHaveBeenCalledWith(45, false, rnr, programRnrColumnList);
      expect(rnrLineItem.lossesAndAdjustments).toEqual([]);
    });

    it("should add losses and adjustments for line item and update total losses and adjustments", function () {
      var rnr = new Object();
      var programRnrColumnList = new Object();
      var lossAndAdjustment = {"type":{"name":"CLINIC_RETURN", "additive":true}, "quantity":45};
      var expectedLossAndAdjustment = {"type":{"name":"CLINIC_RETURN", "additive":true}, "quantity":45};
      var lineItem = {"id":"1", "totalLossesAndAdjustments":0, lossesAndAdjustments:[]};
      var rnrLineItem = new RnrLineItem(lineItem);

      spyOn(rnrLineItem, "updateTotalLossesAndAdjustment");
      rnrLineItem.addLossAndAdjustment(lossAndAdjustment, rnr, programRnrColumnList);

      expect(rnrLineItem.updateTotalLossesAndAdjustment).toHaveBeenCalledWith(45, true, rnr, programRnrColumnList);
      expect(rnrLineItem.lossesAndAdjustments).toEqual([expectedLossAndAdjustment]);
    });

    it('should update total losses and adjustments and add additive lossAndAdjustment', function () {
      var rnrLineItem = new RnrLineItem({"totalLossesAndAdjustments":20});
      var rnr = {"id":1};
      var programRnrColumnList = [];

      spyOn(rnrLineItem, "fillConsumptionOrStockInHand");

      rnrLineItem.updateTotalLossesAndAdjustment(15, true, rnr, programRnrColumnList);

      expect(rnrLineItem.fillConsumptionOrStockInHand).toHaveBeenCalledWith(rnr, programRnrColumnList);
      expect(rnrLineItem.totalLossesAndAdjustments).toEqual(35);
    });

    it('should update total losses and adjustments and subtract non-additive lossAndAdjustment', function () {
      var rnrLineItem = new RnrLineItem({"totalLossesAndAdjustments":40});
      var rnr = {"id":1};
      var programRnrColumnList = [];

      spyOn(rnrLineItem, "fillConsumptionOrStockInHand");

      rnrLineItem.updateTotalLossesAndAdjustment(15, false, rnr, programRnrColumnList);

      expect(rnrLineItem.fillConsumptionOrStockInHand).toHaveBeenCalledWith(rnr, programRnrColumnList);
      expect(rnrLineItem.totalLossesAndAdjustments).toEqual(25);
    });
  });

  describe('Arithmetic validation', function () {
    var programRnrColumnList;
    beforeEach(function () {
      programRnrColumnList = [
        {"indicator":"A", "name":"beginningBalance", "source":{"name":"USER_INPUT"}, "formulaValidationRequired":true}
      ];
    });

    it("should do arithmetic validations if on ", function () {
      var lineItem = {"id":"1", "beginningBalance":3, "quantityReceived":3, "quantityDispensed":3, "totalLossesAndAdjustments":-3, "stockInHand":3};
      var rnrLineItem = new RnrLineItem(lineItem);
      var arithmeticallyInvalid = rnrLineItem.arithmeticallyInvalid(programRnrColumnList);

      expect(arithmeticallyInvalid).toEqual(true);

      rnrLineItem.quantityDispensed = 0;

      arithmeticallyInvalid = rnrLineItem.arithmeticallyInvalid(programRnrColumnList);
      expect(arithmeticallyInvalid).toEqual(false);

    });

    it("should return false arithmetic validations if off ", function () {
      programRnrColumnList[0].formulaValidationRequired = false;
      var rnrLineItem = new RnrLineItem({});

      expect(rnrLineItem.arithmeticallyInvalid(programRnrColumnList)).toEqual(false);
    });
  });

  describe('Error message to be displayed', function () {
    var programRnrColumnList;
    beforeEach(function () {
      programRnrColumnList = [
        {"indicator":"A", "name":"beginningBalance", "source":{"name":"USER_INPUT"}, "formulaValidationRequired":true}
      ];
    });


    it("should give error message for arithmetic validation error ", function () {
      var lineItem = {"id":"1", "beginningBalance":3, "quantityReceived":3, "quantityDispensed":3, "stockInHand":3};
      var rnrLineItem = new RnrLineItem(lineItem);

      spyOn(rnrLineItem, 'arithmeticallyInvalid').andReturn("error");
      var errorMsg = rnrLineItem.getErrorMessage(programRnrColumnList);
      expect(errorMsg).toEqual("The entries are arithmetically invalid, please recheck");
    });

    it("should give error message for negative stock in hand", function () {
      var lineItem = {"id":"1", "beginningBalance":3, "quantityReceived":3, "quantityDispensed":33, "stockInHand":-3};
      var rnrLineItem = new RnrLineItem(lineItem);


      var errorMsg = rnrLineItem.getErrorMessage(programRnrColumnList);

      expect(errorMsg).toEqual("Stock On Hand is calculated to be negative, please validate entries");
    });

    it("should give error message for negative quantity dispensed ", function () {
      programRnrColumnList[0].formulaValidationRequired = false;
      var lineItem = {"id":"1", "beginningBalance":3, "quantityReceived":3, "quantityDispensed":-3, "stockInHand":3};
      var rnrLineItem = new RnrLineItem(lineItem);

      var errorMsg = rnrLineItem.getErrorMessage(programRnrColumnList);

      expect(errorMsg).toEqual("Total Quantity Consumed is calculated to be negative, please validate entries");
    });

  });

  describe('Get Source name', function () {
    var programRnrColumnList;
    beforeEach(function () {
      programRnrColumnList = [
        {"indicator":"A", "name":"beginningBalance", "source":{"name":"USER_INPUT"}, "formulaValidationRequired":true}
      ];
    });

    it('should get rnr column source name for the provided indicator', function () {
      var lineItem = new RnrLineItem({"id":15});
      expect(lineItem.getSource("A", programRnrColumnList)).toEqual("USER_INPUT");
    });
  });

  describe('Execution workflow for calculation', function () {
    var programRnrColumnList;
    var rnr;
    var rnrLineItem;

    beforeEach(function () {
      programRnrColumnList = [
        {"indicator":"A", "name":"beginningBalance", "source":{"name":"USER_INPUT"}, "formulaValidationRequired":true}
      ];
      rnr = {"id":1};
      rnrLineItem = new RnrLineItem({"id":"1", "beginningBalance":3, "quantityReceived":4, "quantityDispensed":-3, "stockInHand":9, "totalLossesAndAdjustments":34});
    });

    it('should test execution flow when consumption or stock in hand gets filled', function () {
      spyOn(rnrLineItem, "calculateConsumption");
      spyOn(rnrLineItem, "calculateStockInHand");
      spyOn(rnrLineItem, "fillNormalizedConsumption");
      spyOn(utils, "parseIntWithBaseTen");

      rnrLineItem.fillConsumptionOrStockInHand(rnr, programRnrColumnList);

      expect(utils.parseIntWithBaseTen).toHaveBeenCalledWith(3);
      expect(utils.parseIntWithBaseTen).toHaveBeenCalledWith(4);
      expect(utils.parseIntWithBaseTen).toHaveBeenCalledWith(-3);
      expect(utils.parseIntWithBaseTen).toHaveBeenCalledWith(34);
      expect(utils.parseIntWithBaseTen).toHaveBeenCalledWith(9);

      expect(rnrLineItem.calculateConsumption).toHaveBeenCalledWith(programRnrColumnList);
      expect(rnrLineItem.calculateStockInHand).toHaveBeenCalledWith(programRnrColumnList);
      expect(rnrLineItem.fillNormalizedConsumption).toHaveBeenCalledWith(rnr, programRnrColumnList);
    });

    it('should test execution flow when packs to ship gets filled and order quantity is quantity requested', function () {
      rnrLineItem.quantityRequested = 31;

      spyOn(rnrLineItem, "calculatePacksToShip");
      spyOn(rnrLineItem, "fillCost");

      rnrLineItem.fillPacksToShipBasedOnCalculatedOrderQuantityOrQuantityRequested(rnr);

      expect(rnrLineItem.calculatePacksToShip).toHaveBeenCalledWith(31);
      expect(rnrLineItem.fillCost).toHaveBeenCalledWith(rnr);
    });

    it('should test execution flow when packs to ship gets filled and order quantity is not present', function () {
      rnrLineItem.quantityRequested = null;
      rnrLineItem.calculatedOrderQuantity = 12;

      spyOn(rnrLineItem, "calculatePacksToShip");
      spyOn(rnrLineItem, "fillCost");

      rnrLineItem.fillPacksToShipBasedOnCalculatedOrderQuantityOrQuantityRequested(rnr);

      expect(rnrLineItem.calculatePacksToShip).toHaveBeenCalledWith(12);
      expect(rnrLineItem.fillCost).toHaveBeenCalledWith(rnr);
    });

    it('should test execution flow when normalized consumption gets filled', function () {
      spyOn(rnrLineItem, "calculateNormalizedConsumption");
      spyOn(rnrLineItem, "fillAMC");

      rnrLineItem.fillNormalizedConsumption(rnr, programRnrColumnList);

      expect(rnrLineItem.calculateNormalizedConsumption).toHaveBeenCalledWith(programRnrColumnList);
      expect(rnrLineItem.fillAMC).toHaveBeenCalledWith(rnr);
    });

    it('should test execution flow when rnr line item cost gets filled when it is of full supply type', function () {
      rnrLineItem.fullSupply = true;

      spyOn(rnrLineItem, "calculateCost");
      spyOn(rnrLineItem, "calculateFullSupplyItemsSubmittedCost");

      rnrLineItem.fillCost(rnr);

      expect(rnrLineItem.calculateCost).toHaveBeenCalled();
      expect(rnrLineItem.calculateFullSupplyItemsSubmittedCost).toHaveBeenCalledWith(rnr);
    });

    it('should test execution flow when rnr line item cost gets filled when it is of non-full supply type', function () {
      rnrLineItem.fullSupply = false;

      spyOn(rnrLineItem, "calculateCost");
      spyOn(rnrLineItem, "calculateNonFullSupplyItemsSubmittedCost");

      rnrLineItem.fillCost(rnr);

      expect(rnrLineItem.calculateCost).toHaveBeenCalled();
      expect(rnrLineItem.calculateNonFullSupplyItemsSubmittedCost).toHaveBeenCalledWith(rnr);
    });

    it('should test execution flow when amc gets filled', function () {
      spyOn(rnrLineItem, "calculateAMC");
      spyOn(rnrLineItem, "fillMaxStockQuantity");

      rnrLineItem.fillAMC(rnr);

      expect(rnrLineItem.calculateAMC).toHaveBeenCalled();
      expect(rnrLineItem.fillMaxStockQuantity).toHaveBeenCalledWith(rnr);
    });

    it('should test execution flow when max stock quantity gets filled', function () {
      spyOn(rnrLineItem, "calculateMaxStockQuantity");
      spyOn(rnrLineItem, "fillCalculatedOrderQuantity");

      rnrLineItem.fillMaxStockQuantity(rnr);

      expect(rnrLineItem.calculateMaxStockQuantity).toHaveBeenCalled();
      expect(rnrLineItem.fillCalculatedOrderQuantity).toHaveBeenCalledWith(rnr);
    });

    it('should test execution flow when calculated order quantity gets filled', function () {
      spyOn(rnrLineItem, "calculateCalculatedOrderQuantity");
      spyOn(rnrLineItem, "fillPacksToShipBasedOnCalculatedOrderQuantityOrQuantityRequested");

      rnrLineItem.fillCalculatedOrderQuantity(rnr);

      expect(rnrLineItem.calculateCalculatedOrderQuantity).toHaveBeenCalled();
      expect(rnrLineItem.fillPacksToShipBasedOnCalculatedOrderQuantityOrQuantityRequested).toHaveBeenCalledWith(rnr);
    });

    it('should test execution flow when packs to ship gets filled based on approved quantity', function () {
      rnrLineItem.quantityApproved = 30;

      spyOn(rnrLineItem, "calculatePacksToShip");
      spyOn(rnrLineItem, "fillCost");

      rnrLineItem.fillPacksToShipBasedOnApprovedQuantity(rnr);

      expect(rnrLineItem.calculatePacksToShip).toHaveBeenCalledWith(30);
      expect(rnrLineItem.fillCost).toHaveBeenCalledWith(rnr);
    });

    it('should update cost when approved quantity gets filled', function () {
      spyOn(rnrLineItem, "fillPacksToShipBasedOnApprovedQuantity");
      spyOn(rnrLineItem, "fillCost");

      rnrLineItem.getTotalLineItemCost = function (param) {
        return 50;
      }
      rnrLineItem.updateCostWithApprovedQuantity(rnr);

      expect(rnr.fullSupplyItemsSubmittedCost).toEqual(50);
      expect(rnrLineItem.fillPacksToShipBasedOnApprovedQuantity).toHaveBeenCalledWith(rnr);
      expect(rnrLineItem.fillCost).toHaveBeenCalledWith(rnr);
    });
  });
});

