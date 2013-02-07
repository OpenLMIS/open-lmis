describe('RnrLineItem', function () {
  beforeEach(module('rnr'));

  describe('Create RnrLineItem', function () {
    it('Should create rnr with empty previousNormalizedConsumptions if it is null in json data', function () {
      var rnr = new Object();
      var programRnrColumnList = [
        {"column1":"column 1"}
      ];

      var rnrLineItem = new RnrLineItem({}, rnr, programRnrColumnList);

      expect(rnrLineItem.previousNormalizedConsumptions).toEqual([]);
      expect(rnrLineItem.rnr).toEqual(rnr);
      expect(rnrLineItem.programRnrColumnList).toEqual(programRnrColumnList);
    });

    it('should initialize losses and adjustments, if not present in R&R', function () {

      var rnrLineItem = new RnrLineItem({'id':123, 'product':'Commodity Name' }, null, null);
      expect(rnrLineItem.lossesAndAdjustments).toEqual([]);
    });

  });

  describe('Calculate consumption', function () {
    var programRnrColumnList;
    var rnr;

    beforeEach(function () {
      programRnrColumnList = [
        {"indicator":"C", "name":"quantityDispensed", "source":{"name":"CALCULATED"}}
      ];
      rnr = {};
    });

    it('should calculate consumption', function () {

      var lineItem = {"beginningBalance":5, "quantityReceived":20, "quantityDispensed":null, "stockInHand":10};
      var rnrLineItem = new RnrLineItem(lineItem, rnr, programRnrColumnList);
      rnrLineItem.totalLossesAndAdjustments = 5;

      rnrLineItem.calculateConsumption();

      expect(rnrLineItem.quantityDispensed).toEqual(20);
    });

    it('should not calculate consumption when one of the dependant columns is not set', function () {
      var lineItem = {"beginningBalance":1, "quantityReceived":2, "quantityDispensed":null, "totalLossesAndAdjustments":3, "stockInHand":null};
      var rnrLineItem = new RnrLineItem(lineItem, rnr, programRnrColumnList);

      rnrLineItem.calculateConsumption();

      expect(rnrLineItem.quantityDispensed).toEqual(null);
    });

    it('should not calculate consumption when it is not a calculated field', function () {
      programRnrColumnList = [
        {"indicator":"C", "name":"quantityDispensed", "source":{"name":"USER_INPUT"}}
      ];
      var rnrLineItem = new RnrLineItem(null, rnr, programRnrColumnList);

      rnrLineItem.calculateConsumption();

      expect(rnrLineItem.quantityDispensed).toEqual(null);
    });
  });

  describe('Calculate stock in hand', function () {
    var programRnrColumnList;
    var rnr;
    beforeEach(function () {
      programRnrColumnList = [
        {"indicator":"E", "name":"stockInHand", "source":{"name":"CALCULATED"}}
      ];
      rnr = {};
    });

    it('should calculate stock in hand when all values are 0 - NaN check', function () {
      var lineItem = {"beginningBalance":0, "quantityReceived":0, "quantityDispensed":0, "totalLossesAndAdjustments":0, "stockInHand":null};
      var rnrLineItem = new RnrLineItem(lineItem, rnr, programRnrColumnList);

      rnrLineItem.calculateStockInHand();

      expect(rnrLineItem.stockInHand).toEqual(0);
    });

    it('should calculate stock in hand', function () {
      var lineItem = {"beginningBalance":10, "quantityReceived":10, "quantityDispensed":10,"stockInHand":null};
      var rnrLineItem = new RnrLineItem(lineItem, rnr, programRnrColumnList);
      rnrLineItem.totalLossesAndAdjustments=1;

      rnrLineItem.calculateStockInHand();

      expect(rnrLineItem.stockInHand).toEqual(11);
    });

    it('should not calculate stock in hand when one of the dependant columns is not set', function () {
      var lineItem = {"beginningBalance":1, "quantityReceived":2, "quantityDispensed":null, "totalLossesAndAdjustments":null, "stockInHand":null};
      var rnrLineItem = new RnrLineItem(lineItem, rnr, programRnrColumnList);

      rnrLineItem.calculateStockInHand();

      expect(rnrLineItem.stockInHand).toEqual(null);
    });

    it('should not calculate stock in hand when it is not a calculated field', function () {
      programRnrColumnList = [
        {"indicator":"E", "name":"stockInHand", "source":{"name":"USER_INPUT"}}
      ];
      var rnrLineItem = new RnrLineItem(null, rnr, programRnrColumnList);

      rnrLineItem.calculateStockInHand();

      expect(rnrLineItem.stockInHand).toEqual(null);
    });


  });

  describe('Calculate normalized consumption', function () {
    var programRnrColumnList;
    var rnr;
    beforeEach(function () {
      programRnrColumnList = [
        {"indicator":"A", "name":"beginningBalance", "source":{"name":"USER_INPUT"}},
        {"indicator":"B", "name":"quantityReceived", "source":{"name":"USER_INPUT"}},
        {"indicator":"C", "name":"quantityDispensed", "source":{"name":"CALCULATED"}},
        {"indicator":"D", "name":"lossesAndAdjustments", "source":{"name":"USER_INPUT"}},
        {"indicator":"E", "name":"stockInHand", "source":{"name":"CALCULATED"}},
        {"indicator":"F", "name":"newPatientCount", "source":{"name":"USER_INPUT"}},
        {"indicator":"X", "name":"stockOutDays", "source":{"name":"USER_INPUT"}}
      ]
      rnr = {};
    });

    it('should calculate normalized consumption', function () {
      var lineItem = {"beginningBalance":1, "quantityReceived":10, "quantityDispensed":5,
        "stockOutDays":5, "newPatientCount":10, "dosesPerMonth":30, "dosesPerDispensingUnit":28};
      var rnrLineItem = new RnrLineItem({}, rnr, programRnrColumnList);
      jQuery.extend(rnrLineItem, lineItem);
      rnrLineItem.totalLossesAndAdjustments = -4;

      rnrLineItem.calculateNormalizedConsumption();

      expect(rnrLineItem.normalizedConsumption).toEqual(65);
    });

    it('should not calculate normalized consumption when newPatientCount is displayed but not set', function () {
      var lineItem = {"beginningBalance":1, "quantityReceived":10, "quantityDispensed":4, "totalLossesAndAdjustments":4, "stockOutDays":5, "newPatientCount":null};
      var rnrLineItem = new RnrLineItem(lineItem, rnr, programRnrColumnList);

      rnrLineItem.calculateNormalizedConsumption();

      expect(rnrLineItem.normalizedConsumption).toEqual(null);
    });

    it('should not calculate normalized consumption when consumption is empty', function () {
      var lineItem = {"beginningBalance":1, "quantityReceived":2, "quantityDispensed":null, "totalLossesAndAdjustments":3};
      var rnrLineItem = new RnrLineItem({}, rnr, programRnrColumnList);
      jQuery.extend(rnrLineItem, lineItem);

      rnrLineItem.calculateNormalizedConsumption();

      expect(rnrLineItem.normalizedConsumption).toEqual(null);
    });

    it('should not calculate normalized consumption when stockOutDays is not set', function () {
      var lineItem = {"beginningBalance":1, "quantityReceived":10, "quantityDispensed":13, "totalLossesAndAdjustments":4, "stockOutDays":null, "newPatientCount":10};
      var rnrLineItem = new RnrLineItem(lineItem, rnr, programRnrColumnList);


      rnrLineItem.calculateNormalizedConsumption();

      expect(rnrLineItem.normalizedConsumption).toEqual(null);
    });

    it('should calculate normalized consumption when facility is stocked out for the entire reporting period', function () {
      var lineItem = {"beginningBalance":1, "quantityReceived":10, "quantityDispensed":13, "totalLossesAndAdjustments":4, "stockOutDays":90, "newPatientCount":10, "dosesPerMonth":30, "dosesPerDispensingUnit":28};
      var rnrLineItem = new RnrLineItem({}, rnr, programRnrColumnList);
      jQuery.extend(rnrLineItem, lineItem);

      rnrLineItem.calculateNormalizedConsumption();

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
      var rnrLineItem = new RnrLineItem({}, rnr, programRnrColumnList);
      jQuery.extend(rnrLineItem, lineItem);

      rnrLineItem.calculateNormalizedConsumption();

      expect(rnrLineItem.normalizedConsumption).toEqual(5);
    });
  });

  describe('Calculate AMC', function () {
    it('should calculate AMC when number of months in a period is 3 or more', function () {
      var period = {"numberOfMonths":3};
      var rnr = {"period":period};
      var lineItem = {"normalizedConsumption":10, "previousNormalizedConsumptions":[]};
      var rnrLineItem = new RnrLineItem({}, rnr, null);
      jQuery.extend(rnrLineItem, lineItem);

      rnrLineItem.calculateAMC();

      expect(rnrLineItem.amc).toEqual(3);
    });

    it('should calculate AMC when number of months in a period is 2', function () {
      var period = {"numberOfMonths":2};
      var rnr = {"period":period};

      var lineItem = {"normalizedConsumption":10, "previousNormalizedConsumptions":[14]};
      var rnrLineItem = new RnrLineItem({}, rnr, null);
      jQuery.extend(rnrLineItem, lineItem);

      rnrLineItem.calculateAMC();
      expect(rnrLineItem.amc).toEqual(6);
    });

    it('should calculate AMC when number of months in a period is 2 but previous normalized consumption is not available', function () {
      var period = {"numberOfMonths":2};
      var rnr = {"period":period};
      var lineItem = {"normalizedConsumption":10, "previousNormalizedConsumptions":[]};
      var rnrLineItem = new RnrLineItem({}, rnr, null);
      jQuery.extend(rnrLineItem, lineItem);

      rnrLineItem.calculateAMC();
      expect(rnrLineItem.amc).toEqual(5);
    });

    it('should calculate AMC when number of months in a period is 1', function () {
      var period = {"numberOfMonths":1};
      var rnr = {"period":period};
      var lineItem = {"normalizedConsumption":10, "previousNormalizedConsumptions":[14, 12]};
      var rnrLineItem = new RnrLineItem({}, rnr, null);
      jQuery.extend(rnrLineItem, lineItem);

      rnrLineItem.calculateAMC();

      expect(rnrLineItem.amc).toEqual(12);
    });

    it('should calculate AMC when number of months in a period is 1 and only one of the two previous normalized consumption is available', function () {
      var period = {"numberOfMonths":1};
      var rnr = {"period":period};
      var lineItem = {"normalizedConsumption":10, "previousNormalizedConsumptions":[14]};
      var rnrLineItem = new RnrLineItem({}, rnr, null);
      jQuery.extend(rnrLineItem, lineItem);

      rnrLineItem.calculateAMC();

      expect(rnrLineItem.amc).toEqual(12);
    });

    it('should not calculate AMC when normalized consumption is not present', function () {
      var lineItem = {"normalizedConsumption":null};
      var rnrLineItem = new RnrLineItem({}, null, null);
      jQuery.extend(rnrLineItem, lineItem);

      rnrLineItem.calculateAMC();

      expect(rnrLineItem.amc).toEqual(null);
    });
  });

  describe('Calculate Max Stock Quantity', function () {
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

    it('should calculate maxStockQuantity', function () {
      var lineItem = {"amc":15, "maxMonthsOfStock":3};
      var rnrLineItem = new RnrLineItem({}, null, programRnrColumnList);
      jQuery.extend(rnrLineItem, lineItem);

      rnrLineItem.calculateMaxStockQuantity();

      expect(rnrLineItem.maxStockQuantity).toEqual(45);
    });

    it('should not calculate maxStockQuantity if amc is not available', function () {
      var lineItem = {"maxMonthsOfStock":3};
      var rnrLineItem = new RnrLineItem({}, null, programRnrColumnList);
      jQuery.extend(rnrLineItem, lineItem);

      rnrLineItem.calculateMaxStockQuantity();

      expect(rnrLineItem.maxStockQuantity).toEqual(null);
    });
  });

  describe('Calculate Calculated Order Quantity', function () {
    it('should calculate calculatedOrderQuantity', function () {
      var lineItem = {"stockInHand":7, "maxStockQuantity":10};
      var rnrLineItem = new RnrLineItem({}, null, null);
      jQuery.extend(rnrLineItem, lineItem);

      rnrLineItem.calculateCalculatedOrderQuantity();

      expect(rnrLineItem.calculatedOrderQuantity).toEqual(3);
    });

    it('should not calculate calculatedOrderQuantity when stock in hand is not present', function () {
      var lineItem = {"stockInHand":null, "maxStockQuantity":3};
      var rnrLineItem = new RnrLineItem({}, null, null);
      jQuery.extend(rnrLineItem, lineItem);

      rnrLineItem.calculateCalculatedOrderQuantity();

      expect(rnrLineItem.calculatedOrderQuantity).toEqual(null);
    });

    it('should not calculate calculatedOrderQuantity when maxStockQuantity is not present', function () {
      var lineItem = {"stockInHand":7, "maxStockQuantity":null};
      var rnrLineItem = new RnrLineItem({}, null, null);
      jQuery.extend(rnrLineItem, lineItem);

      rnrLineItem.calculateCalculatedOrderQuantity();

      expect(rnrLineItem.calculatedOrderQuantity).toEqual(null);
    });

    it('should calculate calculatedOrderQuantity to be 0 when value goes negative', function () {
      var lineItem = {"stockInHand":10, "maxStockQuantity":3};
      var rnrLineItem = new RnrLineItem({}, null, null);
      jQuery.extend(rnrLineItem, lineItem);

      rnrLineItem.calculateCalculatedOrderQuantity();

      expect(rnrLineItem.calculatedOrderQuantity).toEqual(0);
    });
  });

  describe('Calculate Packs To Ship', function () {
    var programRnrColumnList;
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

      var rnrLineItem = new RnrLineItem({}, null, programRnrColumnList);
      jQuery.extend(rnrLineItem, lineItem);

      rnrLineItem.calculatePacksToShip();
      expect(3).toEqual(rnrLineItem.packsToShip);
    });

    it('should calculate packsToShip for the given quantity', function () {
      var lineItem = {"packSize":12};
      var rnrLineItem = new RnrLineItem({}, null, programRnrColumnList);
      jQuery.extend(rnrLineItem, lineItem);
      spyOn(rnrLineItem, 'applyRoundingRulesToPacksToShip');

      rnrLineItem.calculatePacksToShip(25);

      expect(rnrLineItem.packsToShip).toEqual(2);
      expect(rnrLineItem.applyRoundingRulesToPacksToShip).toHaveBeenCalledWith(25);
    });
  });

  describe('Apply rounding rules to packs to ship', function () {
    it('should set packsToShip to one when packsToShip is zero and roundToZero is false', function () {
      var lineItem = {"packsToShip":0, "roundToZero":false, "packSize":12, "packRoundingThreshold":7};
      var rnrLineItem = new RnrLineItem({}, null, null);
      jQuery.extend(rnrLineItem, lineItem);

      rnrLineItem.applyRoundingRulesToPacksToShip(5);

      expect(rnrLineItem.packsToShip).toEqual(1);
    });

    it('should set packsToShip to zero when packsToShip is zero and roundToZero is true', function () {
      var lineItem = {"packsToShip":0, "roundToZero":true, "packSize":12, "packRoundingThreshold":7};
      var rnrLineItem = new RnrLineItem({}, null, null);
      jQuery.extend(rnrLineItem, lineItem);

      rnrLineItem.applyRoundingRulesToPacksToShip(5);

      expect(rnrLineItem.packsToShip).toEqual(0);
    });

    it('should increment packsToShip by one when number of remaining items is greater than packRoundingThreshold ', function () {
      var lineItem = {"packsToShip":2, "packSize":12, "packRoundingThreshold":7};
      var rnrLineItem = new RnrLineItem({}, null, null);
      jQuery.extend(rnrLineItem, lineItem);

      rnrLineItem.applyRoundingRulesToPacksToShip(8);

      expect(rnrLineItem.packsToShip).toEqual(3);
    });

    it('should not increment packsToShip when number of remaining items is greater than packRoundingThreshold ', function () {
      var lineItem = {"packsToShip":2, "packSize":12, "packRoundingThreshold":7};
      var rnrLineItem = new RnrLineItem({}, null, null);
      jQuery.extend(rnrLineItem, lineItem);

      rnrLineItem.applyRoundingRulesToPacksToShip(6);

      expect(rnrLineItem.packsToShip).toEqual(2);
    });
  });

  describe('Calculate Cost', function () {
    it('should set cost when pricePerPack and packsToShip are available', function () {
      var lineItem = {"packsToShip":11, "price":200};
      var rnrLineItem = new RnrLineItem({}, null, null);
      jQuery.extend(rnrLineItem, lineItem);

      rnrLineItem.calculateCost();

      expect(rnrLineItem.cost).toEqual(2200.00.toFixed(2));
    });

    it('should set cost to zero when packsToShip is not available', function () {
      var lineItem = {"price":200};
      var rnrLineItem = new RnrLineItem({}, null, null);
      jQuery.extend(rnrLineItem, lineItem);

      rnrLineItem.calculateCost();

      expect(rnrLineItem.cost).toEqual(0);
    });
  });

  describe('Calculate Full Supply Items Submitted Cost For Rnr', function () {
    it('should calculate fullSupplyItemsSubmittedCost', function () {
      var rnr = new Object();

      var rnrLineItem1 = new RnrLineItem({"productCode" :"p1"}, rnr, null);
      rnrLineItem1.cost = 100;
      var rnrLineItem2 = new RnrLineItem({"productCode" :"p2"}, rnr, null);
      rnrLineItem2.cost = 60;
      var rnrLineItem3 = new RnrLineItem({"productCode" :"p3"}, rnr, null);
      rnrLineItem3.cost = 160;
      rnr.lineItems = new Array(rnrLineItem1, rnrLineItem2, rnrLineItem3);
      rnrLineItem1.calculateFullSupplyItemsSubmittedCost();
      expect(rnr.fullSupplyItemsSubmittedCost).toEqual(320.00.toFixed(2));
    });
  });

  describe('Losses and adjustment for line item', function () {
    it("should re evaluate total losses and adjustments for line item", function () {
      var rnr = new Object();
      var programRnrColumnList = new Object();

      var lossAndAdjustment = {"type":{"name":"CLINIC_RETURN", "additive":true}, "quantity":45};
      var lineItem = {"id":"1", "totalLossesAndAdjustments":40, lossesAndAdjustments:[lossAndAdjustment]};
      var rnrLineItem = new RnrLineItem({}, rnr, programRnrColumnList);
      jQuery.extend(rnrLineItem, lineItem);

      spyOn(rnrLineItem, "updateTotalLossesAndAdjustment");

      rnrLineItem.reEvaluateTotalLossesAndAdjustments();

      expect(rnrLineItem.updateTotalLossesAndAdjustment).toHaveBeenCalledWith(45, true);
    });

    it("should remove losses and adjustments for line item and update total losses and adjustments", function () {
      var rnr = new Object();
      var programRnrColumnList = new Object();
      var lossAndAdjustment = {"type":{"name":"CLINIC_RETURN", "additive":true}, "quantity":45};
      var lineItem = {"id":"1", "totalLossesAndAdjustments":45, lossesAndAdjustments:[lossAndAdjustment]};
      var rnrLineItem = new RnrLineItem({}, rnr, programRnrColumnList);
      jQuery.extend(rnrLineItem, lineItem);

      spyOn(rnrLineItem, "updateTotalLossesAndAdjustment");

      rnrLineItem.removeLossAndAdjustment(rnrLineItem.lossesAndAdjustments[0]);

      expect(rnrLineItem.updateTotalLossesAndAdjustment).toHaveBeenCalledWith(45, false);
      expect(rnrLineItem.lossesAndAdjustments).toEqual([]);
    });

    it("should add losses and adjustments for line item and update total losses and adjustments", function () {
      var rnr = new Object();
      var programRnrColumnList = new Object();
      var lossAndAdjustment = {"type":{"name":"CLINIC_RETURN", "additive":true}, "quantity":45};
      var expectedLossAndAdjustment = {"type":{"name":"CLINIC_RETURN", "additive":true}, "quantity":45};
      var lineItem = {"id":"1", "totalLossesAndAdjustments":0, lossesAndAdjustments:[]};
      var rnrLineItem = new RnrLineItem({}, rnr, programRnrColumnList);
      jQuery.extend(rnrLineItem, lineItem);

      spyOn(rnrLineItem, "updateTotalLossesAndAdjustment");
      rnrLineItem.addLossAndAdjustment(lossAndAdjustment);

      expect(rnrLineItem.updateTotalLossesAndAdjustment).toHaveBeenCalledWith(45, true);
      expect(rnrLineItem.lossesAndAdjustments).toEqual([expectedLossAndAdjustment]);
    });

    it('should update total losses and adjustments and add additive lossAndAdjustment', function () {
      var rnr = {"id":1};
      var programRnrColumnList = [];
      var rnrLineItem = new RnrLineItem({}, rnr, programRnrColumnList);
      jQuery.extend(rnrLineItem, {"totalLossesAndAdjustments":20});

      spyOn(rnrLineItem, "fillConsumptionOrStockInHand");

      rnrLineItem.updateTotalLossesAndAdjustment(15, true);

      expect(rnrLineItem.fillConsumptionOrStockInHand).toHaveBeenCalled();
      expect(rnrLineItem.totalLossesAndAdjustments).toEqual(35);
    });

    it('should update total losses and adjustments and subtract non-additive lossAndAdjustment', function () {
      var rnr = {"id":1};
      var programRnrColumnList = [];
      var rnrLineItem = new RnrLineItem({}, rnr, programRnrColumnList);
      jQuery.extend(rnrLineItem, {"totalLossesAndAdjustments":40});

      spyOn(rnrLineItem, "fillConsumptionOrStockInHand");

      rnrLineItem.updateTotalLossesAndAdjustment(15, false);

      expect(rnrLineItem.fillConsumptionOrStockInHand).toHaveBeenCalled();
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
      var rnrLineItem = new RnrLineItem({}, null, programRnrColumnList);
      jQuery.extend(rnrLineItem, lineItem);
      var arithmeticallyInvalid = rnrLineItem.arithmeticallyInvalid();

      expect(arithmeticallyInvalid).toEqual(true);

      rnrLineItem.quantityDispensed = 0;

      arithmeticallyInvalid = rnrLineItem.arithmeticallyInvalid();
      expect(arithmeticallyInvalid).toEqual(false);

    });

    it("should return false arithmetic validations if off ", function () {
      programRnrColumnList[0].formulaValidationRequired = false;
      var rnrLineItem = new RnrLineItem({}, null, programRnrColumnList);

      expect(rnrLineItem.arithmeticallyInvalid()).toEqual(false);
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
      var rnrLineItem = new RnrLineItem({}, null, programRnrColumnList);
      jQuery.extend(rnrLineItem, lineItem);

      spyOn(rnrLineItem, 'arithmeticallyInvalid').andReturn("error");
      var errorMsg = rnrLineItem.getErrorMessage();
      expect(errorMsg).toEqual("The entries are arithmetically invalid, please recheck");
    });

    it("should give error message for negative stock in hand", function () {
      var lineItem = {"id":"1", "beginningBalance":3, "quantityReceived":3, "quantityDispensed":33, "stockInHand":-3};
      var rnrLineItem = new RnrLineItem({}, null, programRnrColumnList);
      jQuery.extend(rnrLineItem, lineItem);

      var errorMsg = rnrLineItem.getErrorMessage();

      expect(errorMsg).toEqual("Stock On Hand is calculated to be negative, please validate entries");
    });

    it("should give error message for negative quantity dispensed ", function () {
      programRnrColumnList[0].formulaValidationRequired = false;
      var lineItem = {"id":"1", "beginningBalance":3, "quantityReceived":3, "quantityDispensed":-3, "stockInHand":3};
      var rnrLineItem = new RnrLineItem({}, null, programRnrColumnList);
      jQuery.extend(rnrLineItem, lineItem);

      var errorMsg = rnrLineItem.getErrorMessage();

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
      var lineItem = new RnrLineItem({"id":15}, null, programRnrColumnList);
      expect(lineItem.getSource("A")).toEqual("USER_INPUT");
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
      var lineItem = {"id":"1", "beginningBalance":3, "quantityReceived":4, "quantityDispensed":-3, "stockInHand":9, "totalLossesAndAdjustments":34};
      rnrLineItem = new RnrLineItem({}, rnr, programRnrColumnList);
      jQuery.extend(rnrLineItem, lineItem);
    });

    it('should test execution flow when consumption or stock in hand gets filled', function () {
      spyOn(rnrLineItem, "calculateConsumption");
      spyOn(rnrLineItem, "calculateStockInHand");
      spyOn(rnrLineItem, "fillNormalizedConsumption");
      spyOn(utils, "parseIntWithBaseTen");

      rnrLineItem.fillConsumptionOrStockInHand();

      expect(utils.parseIntWithBaseTen).toHaveBeenCalledWith(3);
      expect(utils.parseIntWithBaseTen).toHaveBeenCalledWith(4);
      expect(utils.parseIntWithBaseTen).toHaveBeenCalledWith(-3);
      expect(utils.parseIntWithBaseTen).toHaveBeenCalledWith(34);
      expect(utils.parseIntWithBaseTen).toHaveBeenCalledWith(9);

      expect(rnrLineItem.calculateConsumption).toHaveBeenCalled();
      expect(rnrLineItem.calculateStockInHand).toHaveBeenCalled();
      expect(rnrLineItem.fillNormalizedConsumption).toHaveBeenCalled();
    });

    it('should test execution flow when packs to ship gets filled and order quantity is quantity requested', function () {
      rnrLineItem.quantityRequested = 31;

      spyOn(rnrLineItem, "calculatePacksToShip");
      spyOn(rnrLineItem, "fillCost");

      rnrLineItem.fillPacksToShipBasedOnCalculatedOrderQuantityOrQuantityRequested();

      expect(rnrLineItem.calculatePacksToShip).toHaveBeenCalledWith(31);
      expect(rnrLineItem.fillCost).toHaveBeenCalled();
    });

    it('should test execution flow when packs to ship gets filled and order quantity is not present', function () {
      rnrLineItem.quantityRequested = null;
      rnrLineItem.calculatedOrderQuantity = 12;

      spyOn(rnrLineItem, "calculatePacksToShip");
      spyOn(rnrLineItem, "fillCost");

      rnrLineItem.fillPacksToShipBasedOnCalculatedOrderQuantityOrQuantityRequested();

      expect(rnrLineItem.calculatePacksToShip).toHaveBeenCalledWith(12);
      expect(rnrLineItem.fillCost).toHaveBeenCalled();
    });

    it('should test execution flow when normalized consumption gets filled', function () {
      spyOn(rnrLineItem, "calculateNormalizedConsumption");
      spyOn(rnrLineItem, "fillAMC");

      rnrLineItem.fillNormalizedConsumption();

      expect(rnrLineItem.calculateNormalizedConsumption).toHaveBeenCalled();
      expect(rnrLineItem.fillAMC).toHaveBeenCalled();
    });

    it('should test execution flow when rnr line item cost gets filled when it is of full supply type', function () {
      rnrLineItem.fullSupply = true;

      spyOn(rnrLineItem, "calculateCost");
      spyOn(rnrLineItem, "calculateFullSupplyItemsSubmittedCost");

      rnrLineItem.fillCost();

      expect(rnrLineItem.calculateCost).toHaveBeenCalled();
      expect(rnrLineItem.calculateFullSupplyItemsSubmittedCost).toHaveBeenCalled();
    });

    it('should test execution flow when rnr line item cost gets filled when it is of non-full supply type', function () {
      rnrLineItem.fullSupply = false;

      spyOn(rnrLineItem, "calculateCost");
      spyOn(rnrLineItem, "calculateNonFullSupplyItemsSubmittedCost");

      rnrLineItem.fillCost();

      expect(rnrLineItem.calculateCost).toHaveBeenCalled();
      expect(rnrLineItem.calculateNonFullSupplyItemsSubmittedCost).toHaveBeenCalled();
    });

    it('should test execution flow when amc gets filled', function () {
      spyOn(rnrLineItem, "calculateAMC");
      spyOn(rnrLineItem, "fillMaxStockQuantity");

      rnrLineItem.fillAMC();

      expect(rnrLineItem.calculateAMC).toHaveBeenCalled();
      expect(rnrLineItem.fillMaxStockQuantity).toHaveBeenCalled();
    });

    it('should test execution flow when max stock quantity gets filled', function () {
      spyOn(rnrLineItem, "calculateMaxStockQuantity");
      spyOn(rnrLineItem, "fillCalculatedOrderQuantity");

      rnrLineItem.fillMaxStockQuantity();

      expect(rnrLineItem.calculateMaxStockQuantity).toHaveBeenCalled();
      expect(rnrLineItem.fillCalculatedOrderQuantity).toHaveBeenCalled();
    });

    it('should test execution flow when calculated order quantity gets filled', function () {
      spyOn(rnrLineItem, "calculateCalculatedOrderQuantity");
      spyOn(rnrLineItem, "fillPacksToShipBasedOnCalculatedOrderQuantityOrQuantityRequested");

      rnrLineItem.fillCalculatedOrderQuantity();

      expect(rnrLineItem.calculateCalculatedOrderQuantity).toHaveBeenCalled();
      expect(rnrLineItem.fillPacksToShipBasedOnCalculatedOrderQuantityOrQuantityRequested).toHaveBeenCalled();
    });

    it('should test execution flow when packs to ship gets filled based on approved quantity', function () {
      rnrLineItem.quantityApproved = 30;

      spyOn(rnrLineItem, "calculatePacksToShip");
      spyOn(rnrLineItem, "fillCost");

      rnrLineItem.fillPacksToShipBasedOnApprovedQuantity();

      expect(rnrLineItem.calculatePacksToShip).toHaveBeenCalledWith(30);
      expect(rnrLineItem.fillCost).toHaveBeenCalled();
    });

    it('should update cost when approved quantity gets filled', function () {
      spyOn(rnrLineItem, "fillPacksToShipBasedOnApprovedQuantity");

      rnrLineItem.updateCostWithApprovedQuantity();

      expect(rnrLineItem.fillPacksToShipBasedOnApprovedQuantity).toHaveBeenCalled();
    });
  });
});

