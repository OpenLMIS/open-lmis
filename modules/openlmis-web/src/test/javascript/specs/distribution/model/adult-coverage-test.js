/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 *  Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *    
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe('Adult coverage', function () {

  it("should Create adult coverage line items from JSON", function () {
    var adultCoverageLineItem1, adultCoverageLineItem2;
    adultCoverageLineItem1 = {"id": 5, "facilityVisitId": 3, "demographicGroup": "Pregnant Women", "healthCenterTetanus1": {value: 1},
      "outreachTetanus1": {value: undefined}, "healthCenterTetanus2To5": {value: 3}, "outreachTetanus2To5": {value: undefined}};
    adultCoverageLineItem2 = {"id": 26, "facilityVisitId": 3, "demographicGroup": "Students not MIF", "healthCenterTetanus1": {value: undefined},
      "outreachTetanus1": {value: undefined}, "healthCenterTetanus2To5": {value: undefined}, "outreachTetanus2To5": {value: undefined}};

    var adultCoverage = new AdultCoverage(123, {
      adultCoverageLineItems: [adultCoverageLineItem1, adultCoverageLineItem2]
    });

    expect(adultCoverage.adultCoverageLineItems.length).toEqual(2);
    expect(adultCoverage.adultCoverageLineItems[0].demographicGroup).toEqual("Pregnant Women");
    expect(adultCoverage.adultCoverageLineItems[1].demographicGroup).toEqual("Students not MIF");
  });

  describe("total calculations for line items", function () {

    it("should calculate sum of specified attribute for all line items", function () {
      var adultCoverageLineItem1, adultCoverageLineItem2;
      adultCoverageLineItem1 = {"id": 5, "facilityVisitId": 3, "demographicGroup": "Pregnant Women", "healthCenterTetanus1": {value: 1},
        "outreachTetanus1": {value: undefined}, "healthCenterTetanus2To5": {value: undefined}, "outreachTetanus2To5": {value: 100}};
      adultCoverageLineItem2 = {"id": 26, "facilityVisitId": 3, "demographicGroup": "Students not MIF", "healthCenterTetanus1": {value: 10},
        "outreachTetanus1": {value: 10}, "healthCenterTetanus2To5": {value: undefined}, "outreachTetanus2To5": {value: 100}};

      var adultCoverage = new AdultCoverage(123, {
        adultCoverageLineItems: [adultCoverageLineItem1, adultCoverageLineItem2]
      });

      expect(adultCoverage.sumOfAttributes('healthCenterTetanus1')).toEqual(11);
      expect(adultCoverage.sumOfAttributes('outreachTetanus1')).toEqual(10);
      expect(adultCoverage.sumOfAttributes('healthCenterTetanus2To5')).toEqual(0);
      expect(adultCoverage.sumOfAttributes('outreachTetanus2To5')).toEqual(200);
      expect(adultCoverage.totalHealthCenterTetanus1()).toEqual(11);
      expect(adultCoverage.totalOutreachTetanus1()).toEqual(10);
      expect(adultCoverage.totalHealthCenterTetanus2To5()).toEqual(0);
      expect(adultCoverage.totalOutreachTetanus2To5()).toEqual(200);
    });

    it("should calculate tetanus 1st dose total", function () {
      expect(new AdultCoverageLineItem(
        {healthCenterTetanus1: {value: 100},
          outreachTetanus1: {value: 200},
          healthCenterTetanus2To5: {value: 100},
          outreachTetanus2To5: {value: 2000}}
      ).totalTetanus1()).toEqual(300);

      expect(new AdultCoverageLineItem(
        {healthCenterTetanus1: {value: undefined},
          outreachTetanus1: {value: 200},
          healthCenterTetanus2To5: {value: 1000},
          outreachTetanus2To5: {value: 2000}}
      ).totalTetanus1()).toEqual(200);

      expect(new AdultCoverageLineItem(
        {healthCenterTetanus1: {value: undefined},
          outreachTetanus1: {value: undefined},
          healthCenterTetanus2To5: {value: 1000},
          outreachTetanus2To5: {value: 2000}}
      ).totalTetanus1()).toEqual(0);
    });

    it("should calculate tetanus 2 to 5 dose total", function () {
      expect(new AdultCoverageLineItem(
        {healthCenterTetanus1: {value: 100},
          outreachTetanus1: {value: 200},
          healthCenterTetanus2To5: {value: 5},
          outreachTetanus2To5: {value: 5}}
      ).totalTetanus2To5()).toEqual(10);

      expect(new AdultCoverageLineItem(
        {healthCenterTetanus1: {value: 100},
          outreachTetanus1: {value: 200},
          healthCenterTetanus2To5: {value: undefined},
          outreachTetanus2To5: {value: 5}}
      ).totalTetanus2To5()).toEqual(5);


      expect(new AdultCoverageLineItem(
        {healthCenterTetanus1: {value: 100},
          outreachTetanus1: {value: 200},
          healthCenterTetanus2To5: {value: undefined},
          outreachTetanus2To5: {value: undefined}}
      ).totalTetanus2To5()).toEqual(0);

    });

    it("should get totalTetanus as sum of all tetanus1 and tetanus 2 to 5 values", function () {
      expect(new AdultCoverageLineItem(
        {healthCenterTetanus1: {value: 100},
          outreachTetanus1: {value: 200},
          healthCenterTetanus2To5: {value: 5},
          outreachTetanus2To5: {value: 5}}
      ).totalTetanus()).toEqual(310);

      expect(new AdultCoverageLineItem(
        {healthCenterTetanus1: {value: undefined},
          outreachTetanus1: {value: undefined},
          healthCenterTetanus2To5: {value: undefined},
          outreachTetanus2To5: {value: 5}}
      ).totalTetanus()).toEqual(5);

      expect(new AdultCoverageLineItem(
        {healthCenterTetanus1: {value: undefined},
          outreachTetanus1: {value: undefined},
          healthCenterTetanus2To5: {value: undefined},
          outreachTetanus2To5: {value: undefined}}
      ).totalTetanus()).toEqual(0);
    });
  });

  describe("coverage rate calculations", function () {
    it("should calculate coverage rate for line item when all values and target group are present", function () {
      expect(new AdultCoverageLineItem(
        {healthCenterTetanus1: {value: 10},
          outreachTetanus1: {value: 10},
          healthCenterTetanus2To5: {value: 30},
          outreachTetanus2To5: {value: 20},
          targetGroup: 100}
      ).coverageRate()).toEqual(70);
    });

    it("should calculate coverage rate as null when all values for line item are present and target group is undefined", function () {
      expect(new AdultCoverageLineItem(
        {healthCenterTetanus1: {value: 10},
          outreachTetanus1: {value: 10},
          healthCenterTetanus2To5: {value: 30},
          outreachTetanus2To5: {value: 20}}
      ).coverageRate()).toEqual(null);
    });

    it("should calculate coverage rate as 0 when all values for line item are 0 and target group is valid", function () {
      expect(new AdultCoverageLineItem(
        {healthCenterTetanus1: {value: 0},
          outreachTetanus1: {value: 0},
          healthCenterTetanus2To5: {value: 0},
          outreachTetanus2To5: {value: 0},
          targetGroup: 100}
      ).coverageRate()).toEqual(0);
    });

    it("should calculate coverage rate as null when all values for line item are zero and target group is zero", function () {
      expect(new AdultCoverageLineItem(
        {healthCenterTetanus1: {value: 0},
          outreachTetanus1: {value: 0},
          healthCenterTetanus2To5: {value: 0},
          outreachTetanus2To5: {value: 0},
          targetGroup: 0}
      ).coverageRate()).toEqual(null);
    });
  });

  describe("wastage rate calculation", function () {

    it("should calculate wastage rate", function () {
      var adultCoverageLineItem, openedVialLineItemJSON;
      adultCoverageLineItem = {};

      openedVialLineItemJSON = {openedVial: {value: 100}, packSize: 25, productVialName: "Tetanus"};
      var adultCoverage = new AdultCoverage(123, {
        adultCoverageLineItems: [adultCoverageLineItem],
        openedVialLineItems: [openedVialLineItemJSON]
      });

      var openedVialLineItem = adultCoverage.openedVialLineItems[0];
      spyOn(adultCoverage, 'totalTetanus').andReturn(100);
      spyOn(openedVialLineItem, 'wastageRate').andReturn(80)
      expect(adultCoverage.wastageRate(openedVialLineItem)).toEqual(80);
      expect(openedVialLineItem.wastageRate).toHaveBeenCalledWith(100);
    });

    it("should calculate wastage rate as null if openedVials value is 0", function () {
      var openedVialLineItem = new OpenedVialLineItem({openedVial: {value: 0}, packSize: 10, productVialName: "Tetanus"});

      expect(openedVialLineItem.wastageRate(100)).toBeNull();
    });

    it("should calculate wastage rate as null if packSize is 0", function () {
      var openedVialLineItem = new OpenedVialLineItem({openedVial: {value: 100}, packSize: 0, productVialName: "Tetanus"});

      expect(openedVialLineItem.wastageRate(100)).toBeNull();
    });

    it("should calculate wastage rate for openedVialLineItem", function () {
      var openedVialLineItem = new OpenedVialLineItem({openedVial: {value: 10}, packSize: 25, productVialName: "Tetanus"});

      expect(openedVialLineItem.wastageRate(100)).toEqual(60);
    });
  });

  it("should set not recorder for all line items", function () {
    var adultCoverageLineItem1, adultCoverageLineItem2, openedVialLineItem;
    adultCoverageLineItem1 = {"id": 5, "facilityVisitId": 3, "demographicGroup": "Pregnant Women", "healthCenterTetanus1": {value: 1},
      "outreachTetanus1": {value: undefined}, "healthCenterTetanus2To5": {value: 3}, "outreachTetanus2To5": {value: undefined}};
    adultCoverageLineItem2 = {"id": 26, "facilityVisitId": 3, "demographicGroup": "Students not MIF", "healthCenterTetanus1": {value: undefined},
      "outreachTetanus1": {value: undefined}, "healthCenterTetanus2To5": {value: undefined}, "outreachTetanus2To5": {value: undefined}};

    openedVialLineItem = { facilityVisitId: 100, productVialName: "Tetanus", openedVial: {value: "10"}, packSize: 100 };

    var adultCoverage = new AdultCoverage(123, {
      adultCoverageLineItems: [adultCoverageLineItem1, adultCoverageLineItem2],
      openedVialLineItems: [ openedVialLineItem ]
    });

    spyOn(adultCoverage.adultCoverageLineItems[0], 'setNotRecorded');
    spyOn(adultCoverage.adultCoverageLineItems[1], 'setNotRecorded');

    adultCoverage.setNotRecorded();

    expect(adultCoverage.openedVialLineItems[0].openedVial.notRecorded).toBeTruthy();
    expect(adultCoverage.adultCoverageLineItems[0].setNotRecorded).toHaveBeenCalled();
    expect(adultCoverage.adultCoverageLineItems[1].setNotRecorded).toHaveBeenCalled();
  });

  describe("adult coverage line items", function () {
    it("should set not recorded for all four attributes", function () {
      var adultCoverageLineItem = new AdultCoverageLineItem({"id": 5, "facilityVisitId": 3, "demographicGroup": "Pregnant Women", "healthCenterTetanus1": {value: 1},
        "outreachTetanus1": {value: undefined}, "healthCenterTetanus2To5": {value: 3}, "outreachTetanus2To5": {value: undefined}});

      adultCoverageLineItem.setNotRecorded();

      expect(adultCoverageLineItem.healthCenterTetanus1.notRecorded).toBeTruthy();
      expect(adultCoverageLineItem.outreachTetanus1.notRecorded).toBeTruthy();
      expect(adultCoverageLineItem.healthCenterTetanus2To5.notRecorded).toBeTruthy();
      expect(adultCoverageLineItem.outreachTetanus2To5.notRecorded).toBeTruthy();
    });
  });

  it('should initialize empty health center readings if not defined', function() {
    var adultCoverageLineItem = new AdultCoverageLineItem({"id": 5, "facilityVisitId": 3, "demographicGroup": "Pregnant Women",
      "outreachTetanus1": {value: undefined}, "healthCenterTetanus2To5": {value: 3}, "outreachTetanus2To5": {value: undefined}});

    expect(adultCoverageLineItem.healthCenterTetanus1).toEqual({});
    expect(adultCoverageLineItem.healthCenterTetanus2To5).toEqual({value: 3});
  });

  describe("status computation", function () {
    it("should return empty status if all fields are blank and NR not set", function () {
      var adultCoverageLineItem1, adultCoverageLineItem2, openedVialLineItem;
      adultCoverageLineItem1 = {"id": 5, "facilityVisitId": 3, "demographicGroup": "Pregnant Women",
        "healthCenterTetanus1": {value: undefined, notRecorded: false},
        "outreachTetanus1": {value: undefined, notRecorded: false},
        "healthCenterTetanus2To5": {value: undefined, notRecorded: false},
        "outreachTetanus2To5": {value: undefined, notRecorded: false}};
      adultCoverageLineItem2 = {"id": 26, "facilityVisitId": 3, "demographicGroup": "Students not MIF",
        "healthCenterTetanus1": {value: undefined, notRecorded: false},
        "outreachTetanus1": {value: undefined, notRecorded: false},
        "healthCenterTetanus2To5": {value: undefined, notRecorded: false},
        "outreachTetanus2To5": {value: undefined, notRecorded: false}};

      openedVialLineItem = { facilityVisitId: 100, productVialName: "Tetanus", openedVial: {value: undefined, notRecorded: false}, packSize: 100 };

      var adultCoverage = new AdultCoverage(123, {
        adultCoverageLineItems: [adultCoverageLineItem1, adultCoverageLineItem2],
        openedVialLineItems: [ openedVialLineItem ]
      });

      expect(adultCoverage.computeStatus()).toBe(DistributionStatus.EMPTY);
    });


    it("should return empty status if all fields are blank and NR not undefined", function () {
      var adultCoverageLineItem1, adultCoverageLineItem2, openedVialLineItem;
      adultCoverageLineItem1 = {"id": 5, "facilityVisitId": 3, "demographicGroup": "Pregnant Women",
        "healthCenterTetanus1": {value: undefined},
        "outreachTetanus1": {value: undefined},
        "healthCenterTetanus2To5": {value: undefined},
        "outreachTetanus2To5": {value: undefined}};
      adultCoverageLineItem2 = {"id": 26, "facilityVisitId": 3, "demographicGroup": "Students not MIF",
        "healthCenterTetanus1": {value: undefined},
        "outreachTetanus1": {value: undefined},
        "healthCenterTetanus2To5": {value: undefined},
        "outreachTetanus2To5": {value: undefined}};

      openedVialLineItem = { facilityVisitId: 100, productVialName: "Tetanus", openedVial: {value: undefined}, packSize: 100 };

      var adultCoverage = new AdultCoverage(123, {
        adultCoverageLineItems: [adultCoverageLineItem1, adultCoverageLineItem2],
        openedVialLineItems: [ openedVialLineItem ]
      });

      expect(adultCoverage.computeStatus()).toBe(DistributionStatus.EMPTY);
    });

    it("should return incomplete status if form is partially filled", function () {
      var adultCoverageLineItem1, adultCoverageLineItem2, openedVialLineItem;
      adultCoverageLineItem1 = {"id": 5, "facilityVisitId": 3, "demographicGroup": "Pregnant Women",
        "healthCenterTetanus1": {value: 10, notRecorded: false},
        "outreachTetanus1": {value: 1, notRecorded: false},
        "healthCenterTetanus2To5": {value: undefined, notRecorded: false},
        "outreachTetanus2To5": {value: undefined, notRecorded: false}};
      adultCoverageLineItem2 = {"id": 26, "facilityVisitId": 3, "demographicGroup": "Students not MIF",
        "healthCenterTetanus1": {value: undefined, notRecorded: false},
        "outreachTetanus1": {value: undefined, notRecorded: false},
        "healthCenterTetanus2To5": {value: undefined, notRecorded: false},
        "outreachTetanus2To5": {value: undefined, notRecorded: false}};

      openedVialLineItem = { facilityVisitId: 100, productVialName: "Tetanus", openedVial: {value: 100, notRecorded: false}, packSize: 100 };

      var adultCoverage = new AdultCoverage(123, {
        adultCoverageLineItems: [adultCoverageLineItem1, adultCoverageLineItem2],
        openedVialLineItems: [ openedVialLineItem ]
      });

      expect(adultCoverage.computeStatus()).toBe(DistributionStatus.INCOMPLETE);
    });


    it("should return complete status if form is completely filled", function () {
      var adultCoverageLineItem1, adultCoverageLineItem2, openedVialLineItem;
      adultCoverageLineItem1 = {"id": 5, "facilityVisitId": 3, "demographicGroup": "Pregnant Women",
        "healthCenterTetanus1": {value: 10, notRecorded: false},
        "outreachTetanus1": {value: 1, notRecorded: false},
        "healthCenterTetanus2To5": {value: 1000, notRecorded: false},
        "outreachTetanus2To5": {value: 500, notRecorded: false}};
      adultCoverageLineItem2 = {"id": 26, "facilityVisitId": 3, "demographicGroup": "Students not MIF",
        "healthCenterTetanus1": {value: 100, notRecorded: false},
        "outreachTetanus1": {value: 20, notRecorded: false},
        "healthCenterTetanus2To5": {value: 40, notRecorded: false},
        "outreachTetanus2To5": {value: 50, notRecorded: false}};

      openedVialLineItem = { facilityVisitId: 100, productVialName: "Tetanus", openedVial: {value: 100, notRecorded: false}, packSize: 100 };

      var adultCoverage = new AdultCoverage(123, {
        adultCoverageLineItems: [adultCoverageLineItem1, adultCoverageLineItem2],
        openedVialLineItems: [ openedVialLineItem ]
      });

      expect(adultCoverage.computeStatus()).toBe(DistributionStatus.COMPLETE);
    });

    it("should return complete status if form all NRs are set", function () {
      var adultCoverageLineItem1, adultCoverageLineItem2, openedVialLineItem;
      adultCoverageLineItem1 = {"id": 5, "facilityVisitId": 3, "demographicGroup": "Pregnant Women",
        "healthCenterTetanus1": {value: undefined, notRecorded: true},
        "outreachTetanus1": {value: undefined, notRecorded: true},
        "healthCenterTetanus2To5": {value: undefined, notRecorded: true},
        "outreachTetanus2To5": {value: undefined, notRecorded: true}};
      adultCoverageLineItem2 = {"id": 26, "facilityVisitId": 3, "demographicGroup": "Students not MIF",
        "healthCenterTetanus1": {value: undefined, notRecorded: true},
        "outreachTetanus1": {value: undefined, notRecorded: true},
        "healthCenterTetanus2To5": {value: undefined, notRecorded: true},
        "outreachTetanus2To5": {value: undefined, notRecorded: true}};

      openedVialLineItem = { facilityVisitId: 100, productVialName: "Tetanus", openedVial: {value: undefined, notRecorded: true}, packSize: 100 };

      var adultCoverage = new AdultCoverage(123, {
        adultCoverageLineItems: [adultCoverageLineItem1, adultCoverageLineItem2],
        openedVialLineItems: [ openedVialLineItem ]
      });

      expect(adultCoverage.computeStatus()).toBe(DistributionStatus.COMPLETE);
    });

    it("should return incomplete status if only opened vials is not filled", function () {
      var adultCoverageLineItem1, adultCoverageLineItem2, openedVialLineItem;
      adultCoverageLineItem1 = {"id": 5, "facilityVisitId": 3, "demographicGroup": "Pregnant Women",
        "healthCenterTetanus1": {value: 10, notRecorded: false},
        "outreachTetanus1": {value: 20, notRecorded: false},
        "healthCenterTetanus2To5": {value: 200, notRecorded: false},
        "outreachTetanus2To5": {value: 250, notRecorded: false}};
      adultCoverageLineItem2 = {"id": 26, "facilityVisitId": 3, "demographicGroup": "Students not MIF",
        "healthCenterTetanus1": {value: 40, notRecorded: false},
        "outreachTetanus1": {value: 76, notRecorded: false},
        "healthCenterTetanus2To5": {value: 73, notRecorded: false},
        "outreachTetanus2To5": {value: 72, notRecorded: false}};

      openedVialLineItem = { facilityVisitId: 100, productVialName: "Tetanus", openedVial: {value: undefined, notRecorded: false}, packSize: 100 };

      var adultCoverage = new AdultCoverage(123, {
        adultCoverageLineItems: [adultCoverageLineItem1, adultCoverageLineItem2],
        openedVialLineItems: [ openedVialLineItem ]
      });

      expect(adultCoverage.computeStatus()).toBe(DistributionStatus.INCOMPLETE);
    });


    it("should return incomplete status if only opened vials is not filled", function () {
      var adultCoverageLineItem1, adultCoverageLineItem2, openedVialLineItem;
      adultCoverageLineItem1 = {"id": 5, "facilityVisitId": 3, "demographicGroup": "Pregnant Women",
        "healthCenterTetanus1": {value: 10, notRecorded: false},
        "outreachTetanus1": {value: 20, notRecorded: false},
        "healthCenterTetanus2To5": {value: 200, notRecorded: false},
        "outreachTetanus2To5": {value: 250, notRecorded: false}};
      adultCoverageLineItem2 = {"id": 26, "facilityVisitId": 3, "demographicGroup": "Students not MIF",
        "healthCenterTetanus1": {value: 40, notRecorded: false},
        "outreachTetanus1": {value: 76, notRecorded: false},
        "healthCenterTetanus2To5": {value: 73, notRecorded: false},
        "outreachTetanus2To5": {value: 72, notRecorded: false}};

      openedVialLineItem = { facilityVisitId: 100, productVialName: "Tetanus", openedVial: {value: undefined, notRecorded: false}, packSize: 100 };

      var adultCoverage = new AdultCoverage(123, {
        adultCoverageLineItems: [adultCoverageLineItem1, adultCoverageLineItem2],
        openedVialLineItems: [ openedVialLineItem ]
      });

      expect(adultCoverage.computeStatus()).toBe(DistributionStatus.INCOMPLETE);
    });
  });
});
