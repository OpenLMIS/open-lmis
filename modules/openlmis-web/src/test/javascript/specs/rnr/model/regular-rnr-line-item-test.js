/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe('RegularRnrLineItem', function () {
  beforeEach(module('rnr'));

  describe('Create RegularRnrLineItem', function () {
    it('Should set previousNormalizedConsumptions to [] if it is null in json data', function () {
      var programRnrColumnList = [
        {"indicator": "F", "name": "newPatientCount", "source": {"name": "USER_INPUT"}, "configuredOption": {"name": "newPatientCount"}}
      ];

      var regularRnrLineItem = new RegularRnrLineItem({}, 5, programRnrColumnList, "INITIATED");

      expect(regularRnrLineItem.previousNormalizedConsumptions).toEqual([]);
    });

    it('should initialize losses and adjustments, if not present in R&R', function () {

      var regularRnrLineItem = new RegularRnrLineItem({'id': 123, 'product': 'Commodity Name' }, null, null);
      expect(regularRnrLineItem.lossesAndAdjustments).toEqual([]);
    });

  });

  describe('Calculate consumption', function () {
    var programRnrColumnList;
    var rnr;

    beforeEach(function () {
      programRnrColumnList = [
        {"indicator": "C", "name": "quantityDispensed", "source": {"name": "CALCULATED"}}
      ];
      rnr = {};
    });

    it('should calculate consumption', function () {
      var lineItem = {"beginningBalance": 5, "quantityReceived": 20, "quantityDispensed": null, "stockInHand": 10};
      var regularRnrLineItem = new RegularRnrLineItem(lineItem, rnr, programRnrColumnList);
      regularRnrLineItem.totalLossesAndAdjustments = 5;

      regularRnrLineItem.calculateConsumption();

      expect(regularRnrLineItem.quantityDispensed).toEqual(20);
    });

    it('should not calculate consumption when one of the dependant columns is not set', function () {
      var lineItem = {"beginningBalance": 1, "quantityReceived": 2, "quantityDispensed": null, "totalLossesAndAdjustments": 3, "stockInHand": null};
      var regularRnrLineItem = new RegularRnrLineItem(lineItem, rnr, programRnrColumnList);

      regularRnrLineItem.calculateConsumption();

      expect(regularRnrLineItem.quantityDispensed).toEqual(null);
    });

    it('should not calculate consumption when one of the dependant columns is not set', function () {
      var lineItem = {"beginningBalance": 1, "quantityReceived": 2, "quantityDispensed": null, "totalLossesAndAdjustments": 3, "stockInHand": null};
      var regularRnrLineItem = new RegularRnrLineItem(lineItem, rnr, programRnrColumnList);

      regularRnrLineItem.calculateConsumption();

      expect(regularRnrLineItem.quantityDispensed).toEqual(null);
    });

    it('should not calculate consumption when it is not a calculated field', function () {
      programRnrColumnList = [
        {"indicator": "C", "name": "quantityDispensed", "source": {"name": "USER_INPUT"}}
      ];
      var regularRnrLineItem = new RegularRnrLineItem(null, rnr, programRnrColumnList);

      regularRnrLineItem.calculateConsumption();

      expect(regularRnrLineItem.quantityDispensed).toEqual(null);
    });

    it('should not calculate consumption when rnr status is after authorized', function () {
      programRnrColumnList = [
        {"indicator": "C", "name": "quantityDispensed", "source": {"name": "USER_INPUT"}}
      ];
      var regularRnrLineItem = new RegularRnrLineItem({"quantityDispensed" : 666}, rnr, programRnrColumnList, "RELEASED");

      regularRnrLineItem.calculateConsumption();

      expect(regularRnrLineItem.quantityDispensed).toEqual(666);
    });
  });

  describe('Calculate stock in hand', function () {
    var programRnrColumnList;
    var rnr;
    beforeEach(function () {
      programRnrColumnList = [
        {"indicator": "E", "name": "stockInHand", "source": {"name": "CALCULATED"}}
      ];
      rnr = {};
    });

    it('should calculate stock in hand when all values are 0 - NaN check', function () {
      var lineItem = {"beginningBalance": 0, "quantityReceived": 0, "quantityDispensed": 0, "totalLossesAndAdjustments": 0, "stockInHand": null};
      var regularRnrLineItem = new RegularRnrLineItem(lineItem, rnr, programRnrColumnList);

      regularRnrLineItem.calculateStockInHand();

      expect(regularRnrLineItem.stockInHand).toEqual(0);
    });

    it('should not calculate stock in hand if rnr status is after athorized', function () {
      var lineItem = {"beginningBalance": 0, "quantityReceived": 0, "quantityDispensed": 0, "totalLossesAndAdjustments": 0, "stockInHand": 78};
      var regularRnrLineItem = new RegularRnrLineItem(lineItem, rnr, programRnrColumnList, "IN_APPROVAL");

      regularRnrLineItem.calculateStockInHand();

      expect(regularRnrLineItem.stockInHand).toEqual(78);
    });

    it('should calculate stock in hand', function () {
      var lineItem = {"beginningBalance": 10, "quantityReceived": 10, "quantityDispensed": 10, "stockInHand": null};
      var regularRnrLineItem = new RegularRnrLineItem(lineItem, rnr, programRnrColumnList);
      regularRnrLineItem.totalLossesAndAdjustments = 1;

      regularRnrLineItem.calculateStockInHand();

      expect(regularRnrLineItem.stockInHand).toEqual(11);
    });

    it('should not calculate stock in hand when one of the dependant columns is not set', function () {
      var lineItem = {"beginningBalance": 1, "quantityReceived": 2, "quantityDispensed": null, "totalLossesAndAdjustments": null, "stockInHand": null};
      var regularRnrLineItem = new RegularRnrLineItem(lineItem, rnr, programRnrColumnList);

      regularRnrLineItem.calculateStockInHand();

      expect(regularRnrLineItem.stockInHand).toEqual(null);
    });

    it('should not calculate stock in hand when it is not a calculated field', function () {
      programRnrColumnList = [
        {"indicator": "E", "name": "stockInHand", "source": {"name": "USER_INPUT"}}
      ];
      var regularRnrLineItem = new RegularRnrLineItem(null, rnr, programRnrColumnList);

      regularRnrLineItem.calculateStockInHand();

      expect(regularRnrLineItem.stockInHand).toEqual(null);
    });


  });

  describe('Calculate normalized consumption', function () {
    var programRnrColumnList;
    var rnr;
    beforeEach(function () {
      programRnrColumnList = [
        {"indicator": "A", "name": "beginningBalance", "source": {"name": "USER_INPUT"}},
        {"indicator": "B", "name": "quantityReceived", "source": {"name": "USER_INPUT"}},
        {"indicator": "C", "name": "quantityDispensed", "source": {"name": "CALCULATED"}},
        {"indicator": "D", "name": "lossesAndAdjustments", "source": {"name": "USER_INPUT"}},
        {"indicator": "E", "name": "stockInHand", "source": {"name": "CALCULATED"}},
        {"indicator": "F", "name": "newPatientCount", "source": {"name": "USER_INPUT"}, "configuredOption": {"name": "newPatientCount"}},
        {"indicator": "X", "name": "stockOutDays", "source": {"name": "USER_INPUT"}}
      ]
      rnr = {};
    });

    it('should calculate normalized consumption', function () {
      var lineItem = {"beginningBalance": 1, "quantityReceived": 10, "quantityDispensed": 5,
        "stockOutDays": 5, "newPatientCount": 10, "dosesPerMonth": 30, "dosesPerDispensingUnit": 28, "reportingDays": 30};
      var regularRnrLineItem = new RegularRnrLineItem({}, rnr, programRnrColumnList);
      jQuery.extend(regularRnrLineItem, lineItem);
      regularRnrLineItem.totalLossesAndAdjustments = -4;

      regularRnrLineItem.calculateNormalizedConsumption();

      expect(regularRnrLineItem.normalizedConsumption).toEqual(16);
    });

    it('should retain normalized consumption if rnr status is AUTHORIZED/APPROVED/IN_APPROVAL/RELEASED', function () {
      var lineItem = {"beginningBalance": 1, "quantityReceived": 10, "quantityDispensed": 5,
        "stockOutDays": 5, "newPatientCount": 10, "dosesPerMonth": 30, "dosesPerDispensingUnit": 28, "reportingDays": 30, "normalizedConsumption": 55};
      var regularRnrLineItem = new RegularRnrLineItem({}, rnr, programRnrColumnList, "AUTHORIZED");
      jQuery.extend(regularRnrLineItem, lineItem);

      regularRnrLineItem.totalLossesAndAdjustments = -4;

      regularRnrLineItem.calculateNormalizedConsumption();

      expect(regularRnrLineItem.normalizedConsumption).toEqual(55);
    });

    it('should not calculate normalized consumption when newPatientCount is displayed but not set', function () {
      var lineItem = {"beginningBalance": 1, "quantityReceived": 10, "quantityDispensed": 4, "totalLossesAndAdjustments": 4, "stockOutDays": 5, "newPatientCount": null};
      var regularRnrLineItem = new RegularRnrLineItem(lineItem, rnr, programRnrColumnList);

      regularRnrLineItem.calculateNormalizedConsumption();

      expect(regularRnrLineItem.normalizedConsumption).toEqual(null);
    });

    it('should not calculate normalized consumption when consumption is empty', function () {
      var lineItem = {"beginningBalance": 1, "quantityReceived": 2, "quantityDispensed": null, "totalLossesAndAdjustments": 3};
      var regularRnrLineItem = new RegularRnrLineItem({}, rnr, programRnrColumnList);
      jQuery.extend(regularRnrLineItem, lineItem);

      regularRnrLineItem.calculateNormalizedConsumption();

      expect(regularRnrLineItem.normalizedConsumption).toEqual(null);
    });

    it('should not calculate normalized consumption when stockOutDays is not set', function () {
      var lineItem = {"beginningBalance": 1, "quantityReceived": 10, "quantityDispensed": 13, "totalLossesAndAdjustments": 4, "stockOutDays": null, "newPatientCount": 10};
      var regularRnrLineItem = new RegularRnrLineItem(lineItem, rnr, programRnrColumnList);


      regularRnrLineItem.calculateNormalizedConsumption();

      expect(regularRnrLineItem.normalizedConsumption).toEqual(null);
    });

    it('should calculate normalized consumption when facility is stocked out for the entire reporting period',
        function () {
          var lineItem = {"beginningBalance": 1, "quantityReceived": 10, "quantityDispensed": 13,
            "totalLossesAndAdjustments": 4, "stockOutDays": 90, "newPatientCount": 10, "dosesPerMonth": 30, "dosesPerDispensingUnit": 28,
            "reportingDays": 90};
          var regularRnrLineItem = new RegularRnrLineItem({}, rnr, programRnrColumnList);
          jQuery.extend(regularRnrLineItem, lineItem);

          regularRnrLineItem.calculateNormalizedConsumption();

          expect(regularRnrLineItem.normalizedConsumption).toEqual(23);
        });

    it('should calculate normalized consumption when reporting days are less than stock out days',
        function () {
          var lineItem = {"beginningBalance": 1, "quantityReceived": 10, "quantityDispensed": 13,
            "totalLossesAndAdjustments": 4, "stockOutDays": 90, "newPatientCount": 0, "dosesPerMonth": 30, "dosesPerDispensingUnit": 28,
            "reportingDays": 80};
          var regularRnrLineItem = new RegularRnrLineItem({}, rnr, programRnrColumnList);
          jQuery.extend(regularRnrLineItem, lineItem);

          regularRnrLineItem.calculateNormalizedConsumption();

          expect(regularRnrLineItem.normalizedConsumption).toEqual(13);
        });

    xit('should calculate normalized consumption when newPatientCount is not in the template', function () {
      programRnrColumnList = [
        {"indicator": "A", "name": "beginningBalance", "source": {"name": "USER_INPUT"}},
        {"indicator": "B", "name": "quantityReceived", "source": {"name": "USER_INPUT"}},
        {"indicator": "C", "name": "quantityDispensed", "source": {"name": "CALCULATED"}},
        {"indicator": "D", "name": "lossesAndAdjustments", "source": {"name": "USER_INPUT"}},
        {"indicator": "E", "name": "stockInHand", "source": {"name": "CALCULATED"}},
        {"indicator": "X", "name": "stockOutDays", "source": {"name": "USER_INPUT"}}
      ];
      var lineItem = {"beginningBalance": 1, "quantityReceived": 10, "quantityDispensed": 5,
        "totalLossesAndAdjustments": -4, "stockOutDays": 5, "newPatientCount": null, "dosesPerMonth": 30, "dosesPerDispensingUnit": 28,
        "reportingDays": 30};
      var regularRnrLineItem = new RegularRnrLineItem({}, rnr, programRnrColumnList);
      jQuery.extend(regularRnrLineItem, lineItem);

      regularRnrLineItem.calculateNormalizedConsumption();

      expect(regularRnrLineItem.normalizedConsumption).toEqual(6);
    });
  });

  describe('Calculate AMC', function () {
    it('should calculate AMC when previous normalized consumption is not available', function () {
      var regularRnrLineItem = new RegularRnrLineItem({}, 3, null, 'INITIATED');
      regularRnrLineItem.normalizedConsumption = 10;
      regularRnrLineItem.previousNormalizedConsumptions = [];

      regularRnrLineItem.calculateAMC();

      expect(regularRnrLineItem.amc).toEqual(10);
    });

    it('should calculate AMC when previous one normalized consumption is available', function () {
      var regularRnrLineItem = new RegularRnrLineItem({}, 2, null, 'INITIATED');
      regularRnrLineItem.normalizedConsumption = 10;
      regularRnrLineItem.previousNormalizedConsumptions = [14];

      regularRnrLineItem.calculateAMC();

      expect(regularRnrLineItem.amc).toEqual(12);
    });

    it('should calculate AMC when 2 previous normalized consumption are available',
        function () {
          var regularRnrLineItem = new RegularRnrLineItem({}, 2, null, 'INITIATED');
          regularRnrLineItem.normalizedConsumption = 10;
          regularRnrLineItem.previousNormalizedConsumptions = [12, 7];

          regularRnrLineItem.calculateAMC();

          expect(regularRnrLineItem.amc).toEqual(10);
        });

    it('should calculate AMC when number of months in a period is 1', function () {
      var regularRnrLineItem = new RegularRnrLineItem({}, 1, null, 'INITIATED');
      regularRnrLineItem.normalizedConsumption = 10;
      regularRnrLineItem.previousNormalizedConsumptions = [14, 12];

      regularRnrLineItem.calculateAMC();

      expect(regularRnrLineItem.amc).toEqual(12);
    });

    it('should calculate AMC when number of months in a period is 1 and only one of the two previous normalized consumption is available',
        function () {
          var regularRnrLineItem = new RegularRnrLineItem({}, 1, null, 'INITIATED');
          regularRnrLineItem.normalizedConsumption = 10;
          regularRnrLineItem.previousNormalizedConsumptions = [14];

          regularRnrLineItem.calculateAMC();

          expect(regularRnrLineItem.amc).toEqual(12);
        });

    it('should reset AMC to null when normalized consumption is not present', function () {
      var lineItem = {"normalizedConsumption": null};
      var regularRnrLineItem = new RegularRnrLineItem({amc: 5}, null, null);
      jQuery.extend(regularRnrLineItem, lineItem);

      regularRnrLineItem.calculateAMC();

      expect(regularRnrLineItem.amc).toEqual(null);
    });
  });

  describe('Calculate Max Stock Quantity', function () {
    var programRnrColumnList;
    beforeEach(function () {
      programRnrColumnList = [
        {"indicator": "A", "name": "beginningBalance", "source": {"name": "USER_INPUT"}},
        {"indicator": "B", "name": "quantityReceived", "source": {"name": "USER_INPUT"}},
        {"indicator": "C", "name": "quantityDispensed", "source": {"name": "CALCULATED"}},
        {"indicator": "D", "name": "lossesAndAdjustments", "source": {"name": "USER_INPUT"}},
        {"indicator": "E", "name": "stockInHand", "source": {"name": "CALCULATED"}},
        {"indicator": "F", "name": "newPatientCount", "source": {"name": "USER_INPUT"}},
        {"indicator": "X", "name": "stockOutDays", "source": {"name": "USER_INPUT"}}
      ];
    });

    it('should calculate maxStockQuantity', function () {
      var lineItem = {"amc": 15, "maxMonthsOfStock": 3};
      var regularRnrLineItem = new RegularRnrLineItem({}, null, programRnrColumnList);
      jQuery.extend(regularRnrLineItem, lineItem);

      regularRnrLineItem.calculateMaxStockQuantity();

      expect(regularRnrLineItem.maxStockQuantity).toEqual(45);
    });

    it('should not calculate maxStockQuantity if amc is not available', function () {
      var lineItem = {"maxMonthsOfStock": 3};
      var regularRnrLineItem = new RegularRnrLineItem({}, null, programRnrColumnList);
      jQuery.extend(regularRnrLineItem, lineItem);

      regularRnrLineItem.calculateMaxStockQuantity();

      expect(regularRnrLineItem.maxStockQuantity).toEqual(null);
    });
  });

  describe('Calculate Calculated Order Quantity', function () {
    it('should calculate calculatedOrderQuantity', function () {
      var lineItem = {"stockInHand": 7, "maxStockQuantity": 10};
      var regularRnrLineItem = new RegularRnrLineItem({}, null, null);
      jQuery.extend(regularRnrLineItem, lineItem);

      regularRnrLineItem.calculateCalculatedOrderQuantity();

      expect(regularRnrLineItem.calculatedOrderQuantity).toEqual(3);
    });

    it('should not calculate calculatedOrderQuantity when stock in hand is not present', function () {
      var lineItem = {"stockInHand": null, "maxStockQuantity": 3};
      var regularRnrLineItem = new RegularRnrLineItem({}, null, null);
      jQuery.extend(regularRnrLineItem, lineItem);

      regularRnrLineItem.calculateCalculatedOrderQuantity();

      expect(regularRnrLineItem.calculatedOrderQuantity).toEqual(null);
    });

    it('should not calculate calculatedOrderQuantity when maxStockQuantity is not present', function () {
      var lineItem = {"stockInHand": 7, "maxStockQuantity": null};
      var regularRnrLineItem = new RegularRnrLineItem({}, null, null);
      jQuery.extend(regularRnrLineItem, lineItem);

      regularRnrLineItem.calculateCalculatedOrderQuantity();

      expect(regularRnrLineItem.calculatedOrderQuantity).toEqual(null);
    });

    it('should calculate calculatedOrderQuantity to be 0 when value goes negative', function () {
      var lineItem = {"stockInHand": 10, "maxStockQuantity": 3};
      var regularRnrLineItem = new RegularRnrLineItem({}, null, null);
      jQuery.extend(regularRnrLineItem, lineItem);

      regularRnrLineItem.calculateCalculatedOrderQuantity();

      expect(regularRnrLineItem.calculatedOrderQuantity).toEqual(0);
    });
  });

  describe('Calculate Packs To Ship', function () {
    var programRnrColumnList;
    beforeEach(function () {
      programRnrColumnList = [
        {"indicator": "A", "name": "beginningBalance", "source": {"name": "USER_INPUT"}},
        {"indicator": "B", "name": "quantityReceived", "source": {"name": "USER_INPUT"}},
        {"indicator": "C", "name": "quantityDispensed", "source": {"name": "USER_INPUT"}},
        {"indicator": "D", "name": "lossesAndAdjustments", "source": {"name": "USER_INPUT"}},
        {"indicator": "E", "name": "stockInHand", "source": {"name": "CALCULATED"}},
        {"indicator": "F", "name": "newPatientCount", "source": {"name": "USER_INPUT"}},
        {"indicator": "X", "name": "stockOutDays", "source": {"name": "USER_INPUT"}}
      ];
    });

    it('should calculate packsToShip when calculated quantity is available and requested quantity is null', function () {
      var regularRnrLineItem = new RegularRnrLineItem(null, null, null, 'INITIATED');
      regularRnrLineItem.calculatedOrderQuantity = 8;

      spyOn(regularRnrLineItem, 'calculatePacksToShip');

      regularRnrLineItem.fillPacksToShip();
      expect(regularRnrLineItem.calculatePacksToShip).toHaveBeenCalledWith(8);
    });

    it('should calculate packsToShip for the given quantity', function () {
      var lineItem = {"packSize": 12};
      var regularRnrLineItem = new RegularRnrLineItem({}, null, programRnrColumnList);
      jQuery.extend(regularRnrLineItem, lineItem);
      spyOn(regularRnrLineItem, 'applyRoundingRulesToPacksToShip');

      regularRnrLineItem.calculatePacksToShip(25);

      expect(regularRnrLineItem.packsToShip).toEqual(2);
      expect(regularRnrLineItem.applyRoundingRulesToPacksToShip).toHaveBeenCalledWith(25);
    });

    it('should return zero packs to ship if ordered quantity is Zero and roundToZero flag is true', function () {
      var lineItem = {roundToZero: true};
      var regularRnrLineItem = new RegularRnrLineItem({}, null, null);
      jQuery.extend(regularRnrLineItem, lineItem);

      regularRnrLineItem.calculatePacksToShip(0);

      expect(regularRnrLineItem.packsToShip).toEqual(0);
    });

    it('should return one packs to ship if ordered quantity is Zero and roundToZero flag is false', function () {
      var lineItem = {roundToZero: false};
      var regularRnrLineItem = new RegularRnrLineItem({}, null, null);
      jQuery.extend(regularRnrLineItem, lineItem);

      regularRnrLineItem.calculatePacksToShip(0);

      expect(regularRnrLineItem.packsToShip).toEqual(1);
    });
  });

  describe('Apply rounding rules to packs to ship', function () {
    it('should set packsToShip to one when packsToShip is zero and roundToZero is false', function () {
      var lineItem = {"packsToShip": 0, "roundToZero": false, "packSize": 12, "packRoundingThreshold": 7};
      var regularRnrLineItem = new RegularRnrLineItem({}, null, null);
      jQuery.extend(regularRnrLineItem, lineItem);

      regularRnrLineItem.applyRoundingRulesToPacksToShip(5);

      expect(regularRnrLineItem.packsToShip).toEqual(1);
    });

    it('should set packsToShip to zero when packsToShip is zero and roundToZero is true', function () {
      var lineItem = {"packsToShip": 0, "roundToZero": true, "packSize": 12, "packRoundingThreshold": 7};
      var regularRnrLineItem = new RegularRnrLineItem({}, null, null);
      jQuery.extend(regularRnrLineItem, lineItem);

      regularRnrLineItem.applyRoundingRulesToPacksToShip(5);

      expect(regularRnrLineItem.packsToShip).toEqual(0);
    });

    it('should increment packsToShip by one when number of remaining items is greater than packRoundingThreshold ',
        function () {
          var lineItem = {"packsToShip": 2, "packSize": 12, "packRoundingThreshold": 7};
          var regularRnrLineItem = new RegularRnrLineItem({}, null, null);
          jQuery.extend(regularRnrLineItem, lineItem);

          regularRnrLineItem.applyRoundingRulesToPacksToShip(8);

          expect(regularRnrLineItem.packsToShip).toEqual(3);
        });

    it('should not increment packsToShip when number of remaining items is greater than packRoundingThreshold ',
        function () {
          var lineItem = {"packsToShip": 2, "packSize": 12, "packRoundingThreshold": 7};
          var regularRnrLineItem = new RegularRnrLineItem({}, null, null);
          jQuery.extend(regularRnrLineItem, lineItem);

          regularRnrLineItem.applyRoundingRulesToPacksToShip(6);

          expect(regularRnrLineItem.packsToShip).toEqual(2);
        });
  });

  describe('Calculate Cost', function () {
    it('should set cost when pricePerPack and packsToShip are available', function () {
      var lineItem = {"packsToShip": 11, "price": 200};
      var regularRnrLineItem = new RegularRnrLineItem({}, null, null);
      jQuery.extend(regularRnrLineItem, lineItem);

      regularRnrLineItem.calculateCost();

      expect(regularRnrLineItem.cost).toEqual(2200.00.toFixed(2));
    });

    it('should set cost to zero when packsToShip is not available', function () {
      var lineItem = {"price": 200};
      var regularRnrLineItem = new RegularRnrLineItem({}, null, null);
      jQuery.extend(regularRnrLineItem, lineItem);

      regularRnrLineItem.calculateCost();

      expect(regularRnrLineItem.cost).toEqual(0);
    });
  });

  describe('Calculate Total', function () {
    it('should set total when beginningBalance and quantityReceived are available', function () {
      var lineItem = {"beginningBalance": 11, "quantityReceived": 200};
      var regularRnrLineItem = new RegularRnrLineItem({}, null, null);
      jQuery.extend(regularRnrLineItem, lineItem);

      regularRnrLineItem.calculateTotal();

      expect(regularRnrLineItem.total).toEqual(211);
    });

    it('should not calculate total when beginningBalance is not available', function () {
      var lineItem = {"quantityReceived": 200};
      var regularRnrLineItem = new RegularRnrLineItem({}, null, null);
      jQuery.extend(regularRnrLineItem, lineItem);

      regularRnrLineItem.calculateTotal();

      expect(regularRnrLineItem.total).toEqual(null);
    });

    it('should not calculate total when quantityReceived is not available', function () {
      var lineItem = {"beginningBalance": 200};
      var regularRnrLineItem = new RegularRnrLineItem({}, null, null);
      jQuery.extend(regularRnrLineItem, lineItem);

      regularRnrLineItem.calculateTotal();

      expect(regularRnrLineItem.total).toEqual(null);
    });
  });

  describe('Losses and adjustment for line item', function () {
    it('should create losses and adjustment object out of losses and adjustment json data when RegularRnrLineItem Is Created',
        function () {
          var lossAndAdjustment1 = {"type": {"name": "Loss1", "additive": true}, "quantity": 45};
          var lossAndAdjustment2 = {"type": {"name": "Adjust1", "additive": false}, "quantity": 55};
          var lineItem = {"id": 1, "lossesAndAdjustments": [lossAndAdjustment1, lossAndAdjustment2]};

          var regularRnrLineItem = new RegularRnrLineItem(lineItem);

          expect(regularRnrLineItem.lossesAndAdjustments.length).toEqual(2);

          expect("isQuantityValid" in regularRnrLineItem.lossesAndAdjustments[0]).toBeTruthy();
          expect("isQuantityValid" in regularRnrLineItem.lossesAndAdjustments[1]).toBeTruthy();
        });

    it("should re evaluate total losses and adjustments for line item", function () {
      var rnr = new Object();
      var programRnrColumnList = new Object();

      var lossAndAdjustment = {"type": {"name": "CLINIC_RETURN", "additive": true}, "quantity": 45};
      var lineItem = {"id": "1", "totalLossesAndAdjustments": 40, lossesAndAdjustments: [lossAndAdjustment]};
      var regularRnrLineItem = new RegularRnrLineItem({}, rnr, programRnrColumnList);
      jQuery.extend(regularRnrLineItem, lineItem);

      spyOn(regularRnrLineItem, "updateTotalLossesAndAdjustment");

      regularRnrLineItem.reEvaluateTotalLossesAndAdjustments();

      expect(regularRnrLineItem.updateTotalLossesAndAdjustment).toHaveBeenCalledWith(45, true);
    });

    it("should remove losses and adjustments for line item and update total losses and adjustments", function () {
      var rnr = new Object();
      var programRnrColumnList = new Object();
      var lossAndAdjustment = {"type": {"name": "CLINIC_RETURN", "additive": true}, "quantity": 45};
      var lineItem = {"id": "1", "totalLossesAndAdjustments": 45, lossesAndAdjustments: [new LossAndAdjustment(lossAndAdjustment)]};
      var regularRnrLineItem = new RegularRnrLineItem({}, rnr, programRnrColumnList);
      jQuery.extend(regularRnrLineItem, lineItem);

      spyOn(regularRnrLineItem, "updateTotalLossesAndAdjustment");

      regularRnrLineItem.removeLossAndAdjustment(regularRnrLineItem.lossesAndAdjustments[0]);

      expect(regularRnrLineItem.updateTotalLossesAndAdjustment).toHaveBeenCalledWith(45, false);
      expect(regularRnrLineItem.lossesAndAdjustments).toEqual([]);
    });

    it("should add losses and adjustments for line item and update total losses and adjustments", function () {
      var rnr = new Object();
      var programRnrColumnList = new Object();
      var lossAndAdjustment = {"type": {"name": "CLINIC_RETURN", "additive": true}, "quantity": 45};
      var expectedLossAndAdjustment = new LossAndAdjustment({"type": {"name": "CLINIC_RETURN", "additive": true}, "quantity": 45});
      var lineItem = {"id": "1", "totalLossesAndAdjustments": 0, lossesAndAdjustments: []};
      var regularRnrLineItem = new RegularRnrLineItem({}, rnr, programRnrColumnList);
      jQuery.extend(regularRnrLineItem, lineItem);

      spyOn(regularRnrLineItem, "updateTotalLossesAndAdjustment");
      regularRnrLineItem.addLossAndAdjustment(lossAndAdjustment);

      expect(regularRnrLineItem.updateTotalLossesAndAdjustment).toHaveBeenCalledWith(45, true);
      expect(regularRnrLineItem.lossesAndAdjustments).toEqual([expectedLossAndAdjustment]);
      expect("isQuantityValid" in regularRnrLineItem.lossesAndAdjustments[0]).toBeTruthy();
    });

    it('should update total losses and adjustments and add additive lossAndAdjustment', function () {
      var rnr = {"id": 1};
      var programRnrColumnList = [];
      var regularRnrLineItem = new RegularRnrLineItem({}, rnr, programRnrColumnList);
      jQuery.extend(regularRnrLineItem, {"totalLossesAndAdjustments": 20});

      spyOn(regularRnrLineItem, "fillConsumptionOrStockInHand");

      regularRnrLineItem.updateTotalLossesAndAdjustment(15, true);

      expect(regularRnrLineItem.fillConsumptionOrStockInHand).toHaveBeenCalled();
      expect(regularRnrLineItem.totalLossesAndAdjustments).toEqual(35);
    });

    it('should update total losses and adjustments and subtract non-additive lossAndAdjustment', function () {
      var rnr = {"id": 1};
      var programRnrColumnList = [];
      var regularRnrLineItem = new RegularRnrLineItem({}, rnr, programRnrColumnList);
      jQuery.extend(regularRnrLineItem, {"totalLossesAndAdjustments": 40});

      spyOn(regularRnrLineItem, "fillConsumptionOrStockInHand");

      regularRnrLineItem.updateTotalLossesAndAdjustment(15, false);

      expect(regularRnrLineItem.fillConsumptionOrStockInHand).toHaveBeenCalled();
      expect(regularRnrLineItem.totalLossesAndAdjustments).toEqual(25);
    });

    it('should return true on validate losses and adjustments if no losses and adjustments present in the regularRnrLineItem',
        function () {
          var rnr = new Object();
          var programRnrColumnList = new Object();
          var regularRnrLineItem = new RegularRnrLineItem({"id": "1"}, rnr, programRnrColumnList);

          expect(regularRnrLineItem.validateLossesAndAdjustments()).toBeTruthy();
        });

    it('should return false if any loss and adjustment is not valid', function () {
      var rnr = new Object();
      var programRnrColumnList = new Object();
      var lossAndAdjustment1 = {"type": {"name": "LOSS1", "additive": true}, "quantity": 45};
      var lossAndAdjustment2 = {"type": {"name": "LOSS2", "additive": true}, "quantity": 89};

      var lineItem = {"id": "1", lossesAndAdjustments: [lossAndAdjustment1, lossAndAdjustment2]};
      var regularRnrLineItem = new RegularRnrLineItem(lineItem, rnr, programRnrColumnList);

      spyOn(regularRnrLineItem.lossesAndAdjustments[0], "isQuantityValid").andReturn(true);
      spyOn(regularRnrLineItem.lossesAndAdjustments[1], "isQuantityValid").andReturn(false);

      expect(regularRnrLineItem.validateLossesAndAdjustments()).toBeFalsy();
    });

    it('should return true if all losses and adjustments are valid', function () {
      var rnr = new Object();
      var programRnrColumnList = new Object();
      var lossAndAdjustment1 = {"type": {"name": "LOSS1", "additive": true}, "quantity": 45};
      var lossAndAdjustment2 = {"type": {"name": "LOSS2", "additive": true}, "quantity": 89};

      var lineItem = {"id": "1", lossesAndAdjustments: [lossAndAdjustment1, lossAndAdjustment2]};
      var regularRnrLineItem = new RegularRnrLineItem(lineItem, rnr, programRnrColumnList);

      spyOn(regularRnrLineItem.lossesAndAdjustments[0], "isQuantityValid").andReturn(true);
      spyOn(regularRnrLineItem.lossesAndAdjustments[1], "isQuantityValid").andReturn(true);

      expect(regularRnrLineItem.validateLossesAndAdjustments()).toBeTruthy();
    });

  });

  describe('Arithmetic validation', function () {
    var programRnrColumnList;
    beforeEach(function () {
      programRnrColumnList = [
        {"indicator": "A", "name": "beginningBalance", "source": {"name": "USER_INPUT"}, "formulaValidationRequired": true}
      ];
    });

    it("should do arithmetic validations if on ", function () {
      var lineItem = {"id": "1", "beginningBalance": 3, "quantityReceived": 3, "quantityDispensed": 3, "totalLossesAndAdjustments": -3, "stockInHand": 3};
      var regularRnrLineItem = new RegularRnrLineItem({}, null, programRnrColumnList);
      jQuery.extend(regularRnrLineItem, lineItem);
      var arithmeticallyInvalid = regularRnrLineItem.arithmeticallyInvalid();

      expect(arithmeticallyInvalid).toEqual(true);

      regularRnrLineItem.quantityDispensed = 0;

      arithmeticallyInvalid = regularRnrLineItem.arithmeticallyInvalid();
      expect(arithmeticallyInvalid).toEqual(false);

    });

    it("should return false arithmetic validations if off ", function () {
      programRnrColumnList[0].formulaValidationRequired = false;
      var regularRnrLineItem = new RegularRnrLineItem({}, null, programRnrColumnList);

      expect(regularRnrLineItem.arithmeticallyInvalid()).toEqual(false);
    });
  });

  describe('Error message to be displayed', function () {
    var programRnrColumnList;
    beforeEach(function () {
      programRnrColumnList = [
        {"indicator": "A", "name": "beginningBalance", "source": {"name": "USER_INPUT"}, "formulaValidationRequired": true}
      ];
    });

    it("should not give error if line item is skipped", function () {
      var regularRnrLineItem = new RegularRnrLineItem(
          {"id": "1",
            "beginningBalance": 3,
            "quantityReceived": 3,
            "quantityDispensed": 3,
            "stockInHand": 3,
            "skipped": true}
          , null, programRnrColumnList);

      spyOn(regularRnrLineItem, 'arithmeticallyInvalid').andReturn("error");
      var errorMsg = regularRnrLineItem.getErrorMessage();
      expect(errorMsg).toEqual('');

    });

    it("should give error message for negative stock in hand", function () {
      var regularRnrLineItem = new RegularRnrLineItem(
          {"id": "1", "stockInHand": -3},
          null, programRnrColumnList);

      var errorMsg = regularRnrLineItem.getErrorMessage();

      expect(errorMsg).toEqual("error.stock.on.hand.negative");
    });

    it("should give error message for arithmetic validation error ", function () {
      var lineItem = {"id": "1", "beginningBalance": 3, "quantityReceived": 3, "quantityDispensed": 3, "stockInHand": 3};
      var regularRnrLineItem = new RegularRnrLineItem({}, null, programRnrColumnList);
      jQuery.extend(regularRnrLineItem, lineItem);

      spyOn(regularRnrLineItem, 'arithmeticallyInvalid').andReturn("error");
      var errorMsg = regularRnrLineItem.getErrorMessage();
      expect(errorMsg).toEqual("error.arithmetically.invalid");
    });

    it("should give error message for negative quantity dispensed ", function () {
      programRnrColumnList[0].formulaValidationRequired = false;
      var lineItem = {"id": "1", "beginningBalance": 3, "quantityReceived": 3, "quantityDispensed": -3, "stockInHand": 3};
      var regularRnrLineItem = new RegularRnrLineItem({}, null, programRnrColumnList);
      jQuery.extend(regularRnrLineItem, lineItem);

      var errorMsg = regularRnrLineItem.getErrorMessage();

      expect(errorMsg).toEqual("error.quantity.consumed.negative");
    });

  });

  describe('Get Source name', function () {
    var programRnrColumnList;
    beforeEach(function () {
      programRnrColumnList = [
        {"indicator": "A", "name": "beginningBalance", "source": {"name": "USER_INPUT"}, "formulaValidationRequired": true}
      ];
    });

    it('should get rnr column source name for the provided indicator', function () {
      var lineItem = new RegularRnrLineItem({"id": 15}, null, programRnrColumnList);
      expect(lineItem.getSource("beginningBalance")).toEqual("USER_INPUT");
    });
  });

  describe('Execution workflow for calculation', function () {
    var programRnrColumnList;
    var rnr;
    var regularRnrLineItem;

    beforeEach(function () {
      programRnrColumnList = [
        {"indicator": "A", "name": "beginningBalance", "source": {"name": "USER_INPUT"}, "formulaValidationRequired": true}
      ];
      rnr = {"id": 1};
      var lineItem = {"id": "1", "beginningBalance": 3, "quantityReceived": 4, "quantityDispensed": -3, "stockInHand": 9};
      regularRnrLineItem = new RegularRnrLineItem(lineItem, 1, programRnrColumnList, 'INITIATED');
      regularRnrLineItem.totalLossesAndAdjustments = 34;
    });

    it('should test execution flow when consumption or stock in hand gets filled', function () {
      spyOn(regularRnrLineItem, "calculateConsumption");
      spyOn(regularRnrLineItem, "calculateStockInHand");
      spyOn(regularRnrLineItem, "fillNormalizedConsumption");
      spyOn(utils, "parseIntWithBaseTen");

      regularRnrLineItem.fillConsumptionOrStockInHand();

      expect(utils.parseIntWithBaseTen).toHaveBeenCalledWith(3);
      expect(utils.parseIntWithBaseTen).toHaveBeenCalledWith(4);
      expect(utils.parseIntWithBaseTen).toHaveBeenCalledWith(-3);
      expect(utils.parseIntWithBaseTen).toHaveBeenCalledWith(34);
      expect(utils.parseIntWithBaseTen).toHaveBeenCalledWith(9);

      expect(regularRnrLineItem.calculateConsumption).toHaveBeenCalled();
      expect(regularRnrLineItem.calculateStockInHand).toHaveBeenCalled();
      expect(regularRnrLineItem.fillNormalizedConsumption).toHaveBeenCalled();
    });

    it('should test execution flow when packs to ship gets filled and order quantity is quantity requested',
        function () {
          regularRnrLineItem.quantityRequested = 31;

          spyOn(regularRnrLineItem, "calculatePacksToShip");
          spyOn(regularRnrLineItem, "calculateCost");

          regularRnrLineItem.fillPacksToShip();

          expect(regularRnrLineItem.calculatePacksToShip).toHaveBeenCalledWith(31);
          expect(regularRnrLineItem.calculateCost).toHaveBeenCalled();
        });

    it('should test execution flow when packs to ship gets filled and order quantity is not present', function () {
      regularRnrLineItem.quantityRequested = null;
      regularRnrLineItem.calculatedOrderQuantity = 12;

      spyOn(regularRnrLineItem, "calculatePacksToShip");
      spyOn(regularRnrLineItem, "calculateCost");

      regularRnrLineItem.fillPacksToShip();

      expect(regularRnrLineItem.calculatePacksToShip).toHaveBeenCalledWith(12);
      expect(regularRnrLineItem.calculateCost).toHaveBeenCalled();
    });

    it('should test execution flow when normalized consumption gets filled', function () {
      spyOn(regularRnrLineItem, "calculateNormalizedConsumption");
      spyOn(regularRnrLineItem, "fillPeriodNormalizedConsumption");
      spyOn(regularRnrLineItem, "fillAMC");

      regularRnrLineItem.fillNormalizedConsumption();

      expect(regularRnrLineItem.calculateNormalizedConsumption).toHaveBeenCalled();
      expect(regularRnrLineItem.fillPeriodNormalizedConsumption).toHaveBeenCalled();
      expect(regularRnrLineItem.fillAMC).toHaveBeenCalled();
    });

    it('should calculate period normalized consumption', function () {
      regularRnrLineItem.normalizedConsumption = 10;
      regularRnrLineItem.numberOfMonths = 2;

      regularRnrLineItem.fillPeriodNormalizedConsumption();

      expect(regularRnrLineItem.periodNormalizedConsumption).toEqual(20);
    });

    it('should calculate period normalized consumption as null if normalized consumption is also null', function () {
      regularRnrLineItem.normalizedConsumption = null;
      regularRnrLineItem.numberOfMonths = 2;

      regularRnrLineItem.fillPeriodNormalizedConsumption();

      expect(regularRnrLineItem.periodNormalizedConsumption).toEqual(null);
    });

    xit('should test execution flow when rnr line item cost gets filled when it is of full supply type', function () {
      regularRnrLineItem.fullSupply = true;

      spyOn(regularRnrLineItem, "calculateCost");

      regularRnrLineItem.fillCost();

      expect(regularRnrLineItem.calculateCost).toHaveBeenCalled();
    });

    xit('should test execution flow when rnr line item cost gets filled when it is of non-full supply type',
        function () {
          regularRnrLineItem.fullSupply = false;

          spyOn(regularRnrLineItem, "calculateCost");
          spyOn(regularRnrLineItem, "calculateNonFullSupplyItemsSubmittedCost");

          regularRnrLineItem.fillCost();

          expect(regularRnrLineItem.calculateCost).toHaveBeenCalled();
          expect(regularRnrLineItem.calculateNonFullSupplyItemsSubmittedCost).toHaveBeenCalled();
        });

    it('should test execution flow when amc gets filled', function () {
      spyOn(regularRnrLineItem, "calculateAMC");
      spyOn(regularRnrLineItem, "fillMaxStockQuantity");

      regularRnrLineItem.fillAMC();

      expect(regularRnrLineItem.calculateAMC).toHaveBeenCalled();
      expect(regularRnrLineItem.fillMaxStockQuantity).toHaveBeenCalled();
    });

    it('should test execution flow when max stock quantity gets filled', function () {
      spyOn(regularRnrLineItem, "calculateMaxStockQuantity");
      spyOn(regularRnrLineItem, "fillCalculatedOrderQuantity");

      regularRnrLineItem.fillMaxStockQuantity();

      expect(regularRnrLineItem.calculateMaxStockQuantity).toHaveBeenCalled();
      expect(regularRnrLineItem.fillCalculatedOrderQuantity).toHaveBeenCalled();
    });

    it('should test execution flow when calculated order quantity gets filled', function () {
      spyOn(regularRnrLineItem, "calculateCalculatedOrderQuantity");
      spyOn(regularRnrLineItem, "fillPacksToShip");

      regularRnrLineItem.fillCalculatedOrderQuantity();

      expect(regularRnrLineItem.calculateCalculatedOrderQuantity).toHaveBeenCalled();
      expect(regularRnrLineItem.fillPacksToShip).toHaveBeenCalled();
    });

    xit('should consider approved quantity as zero when negative or not defined', function () {
      regularRnrLineItem.quantityApproved = -30;

      spyOn(regularRnrLineItem, "calculatePacksToShip");
      spyOn(regularRnrLineItem, "fillCost");

      regularRnrLineItem.fillPacksToShip();

      expect(regularRnrLineItem.calculatePacksToShip).toHaveBeenCalledWith(0);
      expect(regularRnrLineItem.fillCost).toHaveBeenCalled();
    });

    it('should update cost when approved quantity gets filled', function () {
      spyOn(regularRnrLineItem, "calculateCost");

      regularRnrLineItem.fillPacksToShip();

      expect(regularRnrLineItem.calculateCost).toHaveBeenCalled();
    });

    it('should consider approved quantity to calculate packs to ship status is in approval', function () {
      regularRnrLineItem = new RegularRnrLineItem({}, 5, [], 'IN_APPROVAL');
      regularRnrLineItem.quantityApproved = 7;
      regularRnrLineItem.quantityRequested = 78;
      regularRnrLineItem.calculatedOrderQuantity = 90;
      spyOn(regularRnrLineItem, 'calculatePacksToShip');

      regularRnrLineItem.fillPacksToShip();

      expect(regularRnrLineItem.calculatePacksToShip).toHaveBeenCalledWith(7);
    });

    it('should consider approved quantity to calculate packs to ship status is in approval', function () {
      regularRnrLineItem = new RegularRnrLineItem({}, 5, [], 'APPROVED');
      regularRnrLineItem.quantityApproved = 7;
      regularRnrLineItem.quantityRequested = 78;
      regularRnrLineItem.calculatedOrderQuantity = 90;
      spyOn(regularRnrLineItem, 'calculatePacksToShip');

      regularRnrLineItem.fillPacksToShip();

      expect(regularRnrLineItem.calculatePacksToShip).toHaveBeenCalledWith(7);
    });

    it('should return true if visible user input fields are filled', function () {
      programRnrColumnList = [
        {"source": {"name": "USER_INPUT"}, "name": "beginningBalance", "visible": true},
        {"source": {"name": "USER_INPUT"}, "name": "quantityReceived", "visible": true},
        {"source": {"name": "USER_INPUT"}, "name": "quantityDispensed", "visible": true},
        {"source": {"name": "USER_INPUT"}, "visible": true, "name": "newPatientCount"},
        {"source": {"name": "USER_INPUT"}, "visible": false, "name": "stockOutDays"}
      ];
      var regularRnrLineItem = {'beginningBalance': '45', 'quantityDispensed': '23', 'quantityReceived': 3, 'newPatientCount': 45,
        'stockOutDays': ''};
      regularRnrLineItem = new RegularRnrLineItem(regularRnrLineItem, null, programRnrColumnList);
      var isValid = regularRnrLineItem.validateRequiredFieldsForFullSupply();
      expect(isValid).toBeTruthy();
    });

    it('should return false if visible user input field missing', function () {
      programRnrColumnList = [
        {"source": {"name": "USER_INPUT"}, "name": "beginningBalance", "visible": true},
        {"source": {"name": "USER_INPUT"}, "name": "quantityReceived", "visible": true},
        {"source": {"name": "USER_INPUT"}, "name": "quantityDispensed", "visible": true},
        {"source": {"name": "USER_INPUT"}, "visible": true, "name": "newPatientCount"},
        {"source": {"name": "USER_INPUT"}, "visible": true, "name": "stockOutDays"}
      ];
      var regularRnrLineItem = {'beginningBalance': '', 'quantityDispensed': '23', 'quantityReceived': 3, 'newPatientCount': 45,
        'stockOutDays': ''};
      regularRnrLineItem = new RegularRnrLineItem(regularRnrLineItem, null, programRnrColumnList);
      var isValid = regularRnrLineItem.validateRequiredFieldsForFullSupply();
      expect(isValid).toBeFalsy();
    });

    it('should return false if requested quantity is filled and reason is not filled', function () {
      programRnrColumnList = [
        {"source": {"name": "USER_INPUT"}, "name": "beginningBalance", "visible": true},
        {"source": {"name": "USER_INPUT"}, "name": "quantityReceived", "visible": true},
        {"source": {"name": "USER_INPUT"}, "name": "quantityDispensed", "visible": true},
        {"source": {"name": "USER_INPUT"}, "visible": true, "name": "newPatientCount", "configuredOption": {"name": "newPatientCount"}},
        {"source": {"name": "USER_INPUT"}, "visible": true, "name": "stockOutDays"},
        {"source": {"name": "USER_INPUT"}, "visible": true, "name": "quantityRequested"},
        {"source": {"name": "USER_INPUT"}, "visible": true, "name": "reasonForRequestedQuantity"}
      ];
      var regularRnrLineItem = {'beginningBalance': '45', 'stockOutDays': '23', 'quantityDispensed': '23', 'quantityReceived': '89', 'newPatientCount': 45,
        'quantityRequested': '7', 'reasonForRequestedQuantity': ''};
      regularRnrLineItem = new RegularRnrLineItem(regularRnrLineItem, null, programRnrColumnList);
      var isValid = regularRnrLineItem.validateRequiredFieldsForFullSupply();
      expect(isValid).toBeFalsy();
    });

    it('should return true if requested quantity is filled and reason is also filled', function () {
      programRnrColumnList = [
        {"source": {"name": "USER_INPUT"}, "name": "beginningBalance", "visible": true},
        {"source": {"name": "USER_INPUT"}, "name": "quantityReceived", "visible": true},
        {"source": {"name": "USER_INPUT"}, "name": "quantityDispensed", "visible": true},
        {"source": {"name": "USER_INPUT"}, "visible": true, "name": "newPatientCount", "configuredOption": {"name": "newPatientCount"}},
        {"source": {"name": "USER_INPUT"}, "visible": true, "name": "stockOutDays"},
        {"source": {"name": "USER_INPUT"}, "visible": true, "name": "quantityRequested"},
        {"source": {"name": "USER_INPUT"}, "visible": true, "name": "reasonForRequestedQuantity"},
        {"source": {"name": "USER_INPUT"}, "visible": true, "name": "remarks"},
        {"source": {"name": "USER_INPUT"}, "visible": true, "name": "lossesAndAdjustments"},
        {"source": {"name": "USER_INPUT"}, "visible": true, "name": "quantityApproved"}
      ];
      var regularRnrLineItem = {'beginningBalance': '45', 'stockOutDays': '23', 'quantityDispensed': '23', 'quantityReceived': '89', 'newPatientCount': 45,
        'quantityRequested': '7', 'reasonForRequestedQuantity': 'reason', remarks: '', lossesAndAdjustments: '', quantityApproved: ''};
      regularRnrLineItem = new RegularRnrLineItem(regularRnrLineItem, null, programRnrColumnList);
      var isValid = regularRnrLineItem.validateRequiredFieldsForFullSupply();
      expect(isValid).toBeTruthy();
    });

    it('should return true if expiration date is valid', function () {
      programRnrColumnList = [
        {"source": {"name": "USER_INPUT"}, "name": "beginningBalance", "visible": true},
        {"source": {"name": "USER_INPUT"}, "name": "quantityReceived", "visible": true},
        {"source": {"name": "USER_INPUT"}, "name": "quantityDispensed", "visible": true},
        {"source": {"name": "USER_INPUT"}, "visible": true, "name": "newPatientCount", "configuredOption": {"name": "newPatientCount"}},
        {"source": {"name": "USER_INPUT"}, "visible": true, "name": "stockOutDays"},
        {"source": {"name": "USER_INPUT"}, "visible": true, "name": "quantityRequested"},
        {"source": {"name": "USER_INPUT"}, "visible": true, "name": "reasonForRequestedQuantity"},
        {"source": {"name": "USER_INPUT"}, "visible": true, "name": "remarks"},
        {"source": {"name": "USER_INPUT"}, "visible": true, "name": "lossesAndAdjustments"},
        {"source": {"name": "USER_INPUT"}, "visible": true, "name": "expirationDate"},
        {"source": {"name": "USER_INPUT"}, "visible": true, "name": "quantityApproved"}
      ];
      var regularRnrLineItem = {'beginningBalance': '45', 'stockOutDays': '23', 'quantityDispensed': '23', 'quantityReceived': '89', 'newPatientCount': 45,
        'quantityRequested': '7', 'reasonForRequestedQuantity': 'reason', remarks: '', lossesAndAdjustments: '', expirationDate: '11/2012', quantityApproved: ''};
      regularRnrLineItem = new RegularRnrLineItem(regularRnrLineItem, null, programRnrColumnList);
      var isValid = regularRnrLineItem.validateRequiredFieldsForFullSupply();
      expect(isValid).toBeTruthy();
    });

    it('should return false if expiration date is invalid', function () {
      programRnrColumnList = [
        {"source": {"name": "USER_INPUT"}, "name": "beginningBalance", "visible": true},
        {"source": {"name": "USER_INPUT"}, "name": "quantityReceived", "visible": true},
        {"source": {"name": "USER_INPUT"}, "name": "quantityDispensed", "visible": true},
        {"source": {"name": "USER_INPUT"}, "visible": true, "name": "newPatientCount", "configuredOption": {"name": "newPatientCount"}},
        {"source": {"name": "USER_INPUT"}, "visible": true, "name": "stockOutDays"},
        {"source": {"name": "USER_INPUT"}, "visible": true, "name": "quantityRequested"},
        {"source": {"name": "USER_INPUT"}, "visible": true, "name": "reasonForRequestedQuantity"},
        {"source": {"name": "USER_INPUT"}, "visible": true, "name": "remarks"},
        {"source": {"name": "USER_INPUT"}, "visible": true, "name": "lossesAndAdjustments"},
        {"source": {"name": "USER_INPUT"}, "visible": true, "name": "expirationDate"},
        {"source": {"name": "USER_INPUT"}, "visible": true, "name": "quantityApproved"}
      ];
      var regularRnrLineItem = {'beginningBalance': '45', 'stockOutDays': '23', 'quantityDispensed': '23', 'quantityReceived': '89', 'newPatientCount': 45,
        'quantityRequested': '7', 'reasonForRequestedQuantity': 'reason', remarks: '', lossesAndAdjustments: '', expirationDate: '11/212', quantityApproved: ''};
      regularRnrLineItem = new RegularRnrLineItem(regularRnrLineItem, null, programRnrColumnList);
      var isValid = regularRnrLineItem.validateRequiredFieldsForFullSupply();
      expect(isValid).toBeFalsy();
    });

    it('should return true if required fields for non full supply are not filled', function () {
      programRnrColumnList = [
        {"source": {"name": "USER_INPUT"}, "visible": true, "name": "quantityRequested"},
        {"source": {"name": "USER_INPUT"}, "visible": true, "name": "reasonForRequestedQuantity"},
        {"source": {"name": "USER_INPUT"}, "visible": true, "name": "remarks"}
      ];
      var regularRnrLineItem = {'quantityRequested': '', 'reasonForRequestedQuantity': 'reason', remarks: ''};
      regularRnrLineItem = new RegularRnrLineItem(regularRnrLineItem, null, programRnrColumnList);
      var isValid = regularRnrLineItem.validateRequiredFieldsForNonFullSupply();
      expect(isValid).toBeFalsy();
    });

    it('should return true if required fields for non full supply are filled', function () {
      programRnrColumnList = [
        {"source": {"name": "USER_INPUT"}, "visible": true, "name": "quantityRequested"},
        {"source": {"name": "USER_INPUT"}, "visible": true, "name": "reasonForRequestedQuantity"},
        {"source": {"name": "USER_INPUT"}, "visible": true, "name": "remarks"}
      ];
      var regularRnrLineItem = {'quantityRequested': '45', 'reasonForRequestedQuantity': 'reason', remarks: ''};
      regularRnrLineItem = new RegularRnrLineItem(regularRnrLineItem, null, programRnrColumnList);
      var isValid = regularRnrLineItem.validateRequiredFieldsForNonFullSupply();
      expect(isValid).toBeTruthy();
    });

    it('should validate stock in hand formula and return true if stock in hand positive', function () {
      var regularRnrLineItem = {'stockInHand': 90, 'quantityDispensed': 90};
      regularRnrLineItem = new RegularRnrLineItem(regularRnrLineItem, null, programRnrColumnList);
      var isValid = regularRnrLineItem.formulaValid();
      expect(isValid).toBeTruthy();
    });

    it('should validate stock in hand formula and return false if stock in hand negative', function () {
      var regularRnrLineItem = {'stockInHand': -90, 'quantityDispensed': 90};
      regularRnrLineItem = new RegularRnrLineItem(regularRnrLineItem, null, programRnrColumnList);
      var isValid = regularRnrLineItem.formulaValid();
      expect(isValid).toBeFalsy();
    });

    it('should validate stock in hand formula and return true if quantity dispensed positive', function () {
      var regularRnrLineItem = {'quantityDispensed': 90, 'stockInHand': 90};
      regularRnrLineItem = new RegularRnrLineItem(regularRnrLineItem, null, programRnrColumnList);
      var isValid = regularRnrLineItem.formulaValid();
      expect(isValid).toBeTruthy();
    });

    it('should validate stock in hand formula and return false if quantity dispensed negative', function () {
      var regularRnrLineItem = {'quantityDispensed': -90, 'stockInHand': 90};
      regularRnrLineItem = new RegularRnrLineItem(regularRnrLineItem, null, programRnrColumnList);
      var isValid = regularRnrLineItem.formulaValid();
      expect(isValid).toBeFalsy();
    });

    it('should validate stock in hand formula and and return false if arithmetically invalid', function () {
      var regularRnrLineItem = {'quantityDispensed': 90, 'stockInHand': 90};
      regularRnrLineItem = new RegularRnrLineItem(regularRnrLineItem, null, programRnrColumnList);

      spyOn(regularRnrLineItem, 'arithmeticallyInvalid').andReturn(true);

      var isValid = regularRnrLineItem.formulaValid();

      expect(isValid).toBeFalsy();
    });

    it('should validate stock in hand formula and and return false if arithmetically valid', function () {
      var regularRnrLineItem = {'quantityDispensed': 90, 'stockInHand': 90};
      regularRnrLineItem = new RegularRnrLineItem(regularRnrLineItem, null, programRnrColumnList);

      spyOn(regularRnrLineItem, 'arithmeticallyInvalid').andReturn(false);

      var isValid = regularRnrLineItem.formulaValid();

      expect(isValid).toBeTruthy();
    });

    it('should validate line item and and return true if valid', function () {
      var regularRnrLineItem = {fullSupply: true};
      regularRnrLineItem = new RegularRnrLineItem(regularRnrLineItem, null, programRnrColumnList);

      spyOn(regularRnrLineItem, 'formulaValid').andReturn(true);
      spyOn(regularRnrLineItem, 'validateRequiredFieldsForFullSupply').andReturn(true);

      var isValid = regularRnrLineItem.valid();

      expect(isValid).toBeTruthy();
    });

    it('should validate line item and and return false if invalid', function () {
      var regularRnrLineItem = {fullSupply: true};
      regularRnrLineItem = new RegularRnrLineItem(regularRnrLineItem, null, programRnrColumnList);

      spyOn(regularRnrLineItem, 'formulaValid').andReturn(false);
      spyOn(regularRnrLineItem, 'validateRequiredFieldsForFullSupply').andReturn(true);

      var isValid = regularRnrLineItem.valid();

      expect(isValid).toBeFalsy();
    });

    it('should avoid all validations in case line item skipped', function () {
      var rnrStatus = 'INITIATED';
      var lineItem = new RegularRnrLineItem({skipped: true}, null, programRnrColumnList, rnrStatus);

      spyOn(lineItem, 'validateRequiredFieldsForFullSupply').andCallThrough();
      spyOn(lineItem, 'formulaValid').andCallThrough();

      var isValid = lineItem.valid();


      expect(isValid).toBeTruthy();

    });

    it('should validate line item and and return false if arithmetically invalid', function () {
      var regularRnrLineItem = {fullSupply: true};
      regularRnrLineItem = new RegularRnrLineItem(regularRnrLineItem, null, programRnrColumnList);

      spyOn(regularRnrLineItem, 'formulaValid').andReturn(true);
      spyOn(regularRnrLineItem, 'validateRequiredFieldsForFullSupply').andReturn(false);

      var isValid = regularRnrLineItem.valid();

      expect(isValid).toBeFalsy();
    });

    it('should validate line item and and return false if invalid', function () {
      var regularRnrLineItem = {fullSupply: false};
      regularRnrLineItem = new RegularRnrLineItem(regularRnrLineItem, null, programRnrColumnList);

      spyOn(regularRnrLineItem, 'validateRequiredFieldsForNonFullSupply').andReturn(false);

      var isValid = regularRnrLineItem.valid();

      expect(isValid).toBeFalsy();
    });

    it('should validate non full supply line item and and return true if valid', function () {
      var regularRnrLineItem = {fullSupply: false};
      regularRnrLineItem = new RegularRnrLineItem(regularRnrLineItem, null, programRnrColumnList);

      spyOn(regularRnrLineItem, 'validateRequiredFieldsForNonFullSupply').andReturn(true);

      var isValid = regularRnrLineItem.valid();

      expect(isValid).toBeTruthy();
    });

    it('should validate non full supply line item in approved rnr and and return true if valid', function () {
      var regularRnrLineItem = {fullSupply: false};
      regularRnrLineItem = new RegularRnrLineItem(regularRnrLineItem, null, programRnrColumnList, 'IN_APPROVAL');

      spyOn(regularRnrLineItem, 'validateForApproval').andReturn(true);

      var isValid = regularRnrLineItem.valid();

      expect(isValid).toBeTruthy();
    });

    it('should validate non full supply line item in rnr for authorization and and return true if valid', function () {
      var regularRnrLineItem = {fullSupply: false};
      regularRnrLineItem = new RegularRnrLineItem(regularRnrLineItem, null, programRnrColumnList, 'AUTHORIZED');

      spyOn(regularRnrLineItem, 'validateForApproval').andReturn(true);

      var isValid = regularRnrLineItem.valid();

      expect(isValid).toBeTruthy();
    });

    it('should return true if quantity approved filled', function () {
      var regularRnrLineItem = {fullSupply: false, quantityApproved: 56};
      regularRnrLineItem = new RegularRnrLineItem(regularRnrLineItem, 5, [], 'IN_APPROVAL');
      var valid = regularRnrLineItem.validateForApproval();

      expect(valid).toBeTruthy();
    });

    it('should return false if quantity approved filled', function () {
      var regularRnrLineItem = {fullSupply: false, quantityApproved: ''};
      regularRnrLineItem = new RegularRnrLineItem(regularRnrLineItem, 5, [], 'IN_APPROVAL');
      var valid = regularRnrLineItem.validateForApproval();

      expect(valid).toBeFalsy();
    });

    it('should reduce rnr line item to have only productCode, approvedQuantity and remarks', function () {
      var regularRnrLineItem = {id: 1, beginningBalance: 10, quantityDispensed: 5, quantityReceived: 2, fullSupply: true, quantityApproved: 3, remarks: 'some remarks', productCode: 'P10'};
      regularRnrLineItem = new RegularRnrLineItem(regularRnrLineItem, 5, [], 'IN_APPROVAL');
      var reducedRnrLineItem = regularRnrLineItem.reduceForApproval();

      expect(reducedRnrLineItem).toEqual({id: 1, productCode: 'P10', quantityApproved: 3, remarks: 'some remarks'});
    });

  });

  describe('Compare RnrLineItems', function () {

    function createRnrLineItem(productCategoryDisplayOrder, productCategory, productCode, productDisplayOrder) {
      var regularRnrLineItem = new RegularRnrLineItem();
      regularRnrLineItem.productCategoryDisplayOrder = productCategoryDisplayOrder;
      regularRnrLineItem.productCategory = productCategory;
      regularRnrLineItem.productCode = productCode;
      regularRnrLineItem.productDisplayOrder = productDisplayOrder;
      return regularRnrLineItem;
    }

    it('Should compare rnr line items', function () {
      var regularRnrLineItem1 = createRnrLineItem(1, "C1", "P990", null);
      var regularRnrLineItem2 = createRnrLineItem(10, "C3", "P990", null);
      var regularRnrLineItem3 = createRnrLineItem(1, "C1", "P990", 1);
      var regularRnrLineItem4 = createRnrLineItem(1, "C1", "P990", 3);
      var regularRnrLineItem5 = createRnrLineItem(10, "C1", "aaa", null);
      var regularRnrLineItem6 = createRnrLineItem(10, "C1", "ggg", null);
      var regularRnrLineItem7 = createRnrLineItem(10, "C2", null, null);
      var regularRnrLineItem8 = createRnrLineItem(10, "C1", null, null);

      expect(regularRnrLineItem1.compareTo(regularRnrLineItem2)).toBeLessThan(0);
      expect(regularRnrLineItem2.compareTo(regularRnrLineItem2)).toBe(0);
      expect(regularRnrLineItem3.compareTo(regularRnrLineItem4)).toBeLessThan(0);
      expect(regularRnrLineItem4.compareTo(regularRnrLineItem3)).toBeGreaterThan(0);
      expect(regularRnrLineItem5.compareTo(regularRnrLineItem6)).toBeLessThan(0);
      expect(regularRnrLineItem3.compareTo(regularRnrLineItem6)).toBeLessThan(0);
      expect(regularRnrLineItem6.compareTo(regularRnrLineItem3)).toBeGreaterThan(0);
      expect(regularRnrLineItem6.compareTo(undefined)).toBeLessThan(0);
      expect(regularRnrLineItem7.compareTo(regularRnrLineItem8)).toBeGreaterThan(0);
    });

    it('Should compare rnr line items on product code when display order is same', function () {
      var regularRnrLineItem1 = createRnrLineItem(1, "C1", "P2", 1);
      var regularRnrLineItem2 = createRnrLineItem(1, "C1", "P10", 1);

      expect(regularRnrLineItem1.compareTo(regularRnrLineItem2)).toBeGreaterThan(0);
    });

  });
});

