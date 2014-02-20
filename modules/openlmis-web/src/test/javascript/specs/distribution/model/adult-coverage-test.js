/*
 *
 *  * This program is part of the OpenLMIS logistics management information system platform software.
 *  * Copyright © 2013 VillageReach
 *  *
 *  * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  *  
 *  * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 *  
 */

describe('Adult coverage', function () {
  it("should Create adult coverage line items from JSON", function () {
    var adultCoverageLineItem1, adultCoverageLineItem2, openedVialLineItem1;
    adultCoverageLineItem1 = {"id": 5, "facilityVisitId": 3, "targetGroupEntity": "BCG", "healthCenterTetanus1": {value: 1},
      "outreachTetanus1": {value: undefined}, "healthCenterTetanus2To5": {value: 3}, "outreachTetanus2To5": {value: undefined}};
    adultCoverageLineItem2 = {"id": 26, "facilityVisitId": 3, "targetGroupEntity": "Polio (Newborn)", "healthCenter11Months": {value: undefined},
      "outreachTetanus1": {value: undefined}, "healthCenterTetanus2To5": {value: undefined}, "outreachTetanus2To5": {value: undefined}};

    openedVialLineItem1 = {"id": 15, "facilityVisitId": 3, "productVialName": "BCG", "packSize": 10, openedVial: {value: null, notRecorded: true}};

    var adultCoverage = new AdultCoverage(123, {
      adultCoverageLineItems: [adultCoverageLineItem1, adultCoverageLineItem2],
      openedVialLineItems: [openedVialLineItem1]
    });

    expect(adultCoverage.adultCoverageLineItems.size).toEqual(2);
    expect(adultCoverage.adultCoverageLineItems[0].targetGroupEntity).toEqual("BCG");
    expect(adultCoverage.adultCoverageLineItems[1].targetGroupEntity).toEqual("Polio (Newborn)");
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
});