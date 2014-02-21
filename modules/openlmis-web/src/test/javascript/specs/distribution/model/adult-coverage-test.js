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
    adultCoverageLineItem2 = {"id": 26, "facilityVisitId": 3, "demographicGroup": "Students not MIF", "healthCenter11Months": {value: undefined},
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
      var adultCoverageLineItem, openedVialLineItem;
      adultCoverageLineItem = {};

      openedVialLineItem = {value: 100, packSize: 25, productVialName: "Tetanus"};
      var adultCoverage = new AdultCoverage(123, {
        adultCoverageLineItems: [adultCoverageLineItem],
        openedVialLineItems: [openedVialLineItem]
      });

      spyOn(adultCoverage, 'totalTetanus').andReturn(100);
      expect(adultCoverage.wastageRate(openedVialLineItem)).toEqual(96);
    });

    it("should calculate wastage rate as null if openedVials value is 0", function () {
      var adultCoverageLineItem, openedVialLineItem;
      adultCoverageLineItem = {};

      openedVialLineItem = {value: 0, packSize: 10, productVialName: "Tetanus"};
      var adultCoverage = new AdultCoverage(123, {
        adultCoverageLineItems: [adultCoverageLineItem],
        openedVialLineItems: [openedVialLineItem]
      });

      spyOn(adultCoverage, 'totalTetanus').andReturn(100);
      expect(adultCoverage.wastageRate(openedVialLineItem)).toBeNull();
    });

    it("should calculate wastage rate as null if packSize is 0", function () {
      var adultCoverageLineItem, openedVialLineItem;
      adultCoverageLineItem = {};

      openedVialLineItem = {value: 10, packSize: 0, productVialName: "Tetanus"};
      var adultCoverage = new AdultCoverage(123, {
        adultCoverageLineItems: [adultCoverageLineItem],
        openedVialLineItems: [openedVialLineItem]
      });

      spyOn(adultCoverage, 'totalTetanus').andReturn(100);
      expect(adultCoverage.wastageRate(openedVialLineItem)).toBeNull();
    });
  });
});