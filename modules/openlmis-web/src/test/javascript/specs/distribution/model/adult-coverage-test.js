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
  describe("adult coverage calculations", function () {
    it("should calculate sum of specified attribute for all line items", function () {
      var adultCoverageLineItem1, adultCoverageLineItem2, openedVialLineItem1;
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
  });
  it("should Create adult coverage line items from JSON", function () {
    var adultCoverageLineItem1, adultCoverageLineItem2, openedVialLineItem1;
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
  })
  it("should calculate tetanus 1st dose total", function () {
    expect(new AdultCoverageLineItem(
      {healthCenterTetanus1: {value: 100}},
      {healthCenterTetanus2To5: {value: 200}}
    ).totalTetanus2To5(300))

    expect(new AdultCoverageLineItem(
      {healthCenterTetanus1: {value: undefined}},
      {healthCenterTetanus2To5: {value: 200}}
    ).totalTetanus2To5(100))

    expect(new AdultCoverageLineItem(
      {healthCenterTetanus1: {value: undefined}},
      {healthCenterTetanus2To5: {value: undefined}}
    ).totalTetanus2To5(0))
  });

  it("should calculate tetanus 2 to 5 dose total", function () {
    expect(new AdultCoverageLineItem(
      {healthCenter2To5: {value: 5}},
      {outreachTetanus2To5: {value: 5}}
    ).totalTetanus2To5(10))

    expect(new AdultCoverageLineItem(
      {healthCenter2To5: {value: undefined}},
      {outreachTetanus2To5: {value: 5}}
    ).totalTetanus2To5(5))

    expect(new AdultCoverageLineItem(
      {healthCenter2To5: {value: undefined}},
      {outreachTetanus2To5: {value: undefined}}
    ).totalTetanus2To5(0))

  });
  it("should get totalTetanus as sum of all tetanus1 and tetanus 2 to 5 values", function () {
    expect(new AdultCoverageLineItem(
      {healthCenterTetanus1: {value: 100}},
      {healthCenterTetanus2To5: {value: 200}},
      {healthCenter2To5: {value: 5}},
      {outreachTetanus2To5: {value: 5}}
    ).totalTetanus2To5(310))

    expect(new AdultCoverageLineItem(
      {healthCenterTetanus1: {value: undefined}},
      {healthCenterTetanus2To5: {value: undefined}},
      {healthCenter2To5: {value: undefined}},
      {outreachTetanus2To5: {value: 5}}
    ).totalTetanus2To5(5))

    expect(new AdultCoverageLineItem(
      {healthCenter2To5: {value: undefined}},
      {outreachTetanus2To5: {value: undefined}},
      {healthCenter2To5: {value: undefined}},
      {outreachTetanus2To5: {value: undefined}}
    ).totalTetanus2To5(0))
  });

  describe("total calculations for line items", function () {
    it("should calculate total for Tetanus1", function () {
      expect(new AdultCoverageLineItem(
        {healthCenterTetanus1: {value: undefined}},
        {outreachTetanus1: {value: undefined}}
      ).totalTetanus1(0));

      expect(new AdultCoverageLineItem(
        {healthCenterTetanus1: {value: 10}},
        {outreachTetanus1: {value: undefined}}
      ).totalTetanus1(10));

      expect(new AdultCoverageLineItem(
        {healthCenterTetanus1: {value: 10}},
        {outreachTetanus1: {value: 25}}
      ).totalTetanus1(35));

    });

    it("should calculate total for Tetanus2To5", function () {
      expect(new AdultCoverageLineItem(
        {healthCenterTetanus2To5: {value: undefined}},
        {outreachTetanus2To5: {value: undefined}}
      ).totalTetanus2To5(0));

      expect(new AdultCoverageLineItem(
        {healthCenterTetanus2To5: {value: undefined}},
        {outreachTetanus2To5: {value: 10}}
      ).totalTetanus2To5(10));

      expect(new AdultCoverageLineItem(
        {healthCenterTetanus2To5: {value: 15}},
        {outreachTetanus2To5: {value: 20}}
      ).totalTetanus2To5(35));
    });

    it("should calculate total for tetanus", function () {
      expect(new AdultCoverageLineItem(
        {healthCenterTetanus1: {value: undefined}},
        {outreachTetanus1: {value: undefined}},
        {healthCenterTetanus2To5: {value: undefined}},
        {outreachTetanus2To5: {value: undefined}}
      ).totalTetanus(0));

      expect(new AdultCoverageLineItem(
        {healthCenterTetanus1: {value: 10}},
        {outreachTetanus1: {value: 20}},
        {healthCenterTetanus2To5: {value: undefined}},
        {outreachTetanus2To5: {value: 10}}
      ).totalTetanus(40));

      expect(new AdultCoverageLineItem(
        {healthCenterTetanus1: {value: 10}},
        {outreachTetanus1: {value: 10}},
        {healthCenterTetanus2To5: {value: 30}},
        {outreachTetanus2To5: {value: 20}}
      ).totalTetanus(70));
    });

    it("should calculate coverage rate for line item", function () {
      expect(new AdultCoverageLineItem(
        {healthCenterTetanus1: {value: 10}},
        {outreachTetanus1: {value: 10}},
        {healthCenterTetanus2To5: {value: 30}},
        {outreachTetanus2To5: {value: 20}},
        {targetGroup: 100}
      ).coverageRate(70));

      expect(new AdultCoverageLineItem(
        {healthCenterTetanus1: {value: 10}},
        {outreachTetanus1: {value: 10}},
        {healthCenterTetanus2To5: {value: 30}},
        {outreachTetanus2To5: {value: 20}}
      ).coverageRate(null));
    });
  });
});