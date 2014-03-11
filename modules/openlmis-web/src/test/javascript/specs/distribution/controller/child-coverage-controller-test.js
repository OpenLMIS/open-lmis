/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

describe('Child Coverage Controller', function () {

  var scope, distributionService, routeParams, childCoverageJSON,
    childCoverageLineItem1, childCoverageLineItem2, childCoverageLineItem3, childCoverageLineItem4, childCoverageLineItem5,
    openedVialLineItem1, openedVialLineItem2, openedVialLineItem3, openedVialLineItem4, openedVialLineItem5;

  beforeEach(module('distribution'));
  beforeEach(inject(function ($controller, $rootScope, _distributionService_) {

    scope = $rootScope.$new();
    distributionService = _distributionService_;

    childCoverageLineItem1 = {"id": 5, "facilityVisitId": 3, "vaccination": "BCG", "healthCenter11Months": {value: undefined},
      "outreach11Months": {value: undefined}, "healthCenter23Months": {value: undefined}, "outreach23Months": {value: undefined}};
    childCoverageLineItem2 = {"id": 26, "facilityVisitId": 3, "vaccination": "Polio (Newborn)", "healthCenter11Months": {value: undefined},
      "outreach11Months": {value: undefined}, "healthCenter23Months": {value: undefined}, "outreach23Months": {value: undefined}};
    childCoverageLineItem3 = {"id": 27, "facilityVisitId": 3, "vaccination": "Polio 1st dose", "healthCenter11Months": {value: undefined},
      "outreach11Months": {value: undefined}, "healthCenter23Months": {value: undefined}, "outreach23Months": {value: undefined}};
    childCoverageLineItem4 = {"id": 28, "facilityVisitId": 3, "vaccination": "Polio 2nd dose"};
    childCoverageLineItem5 = {"id": 29, "facilityVisitId": 3, "vaccination": "Polio 3rd dose"};

    openedVialLineItem1 = {"id": 15, "facilityVisitId": 3, "productVialName": "BCG", "packSize": 10, openedVial: {value: null, notRecorded: true}};
    openedVialLineItem2 = {"id": 16, "facilityVisitId": 3, "productVialName": "Polio10", "packSize": 10, openedVial: {value: null, notRecorded: true}};
    openedVialLineItem3 = {"id": 17, "facilityVisitId": 3, "productVialName": "Polio20", "packSize": 10, openedVial: {value: null, notRecorded: true}};
    openedVialLineItem4 = {"id": 16, "facilityVisitId": 3, "productVialName": "Penta1", "packSize": 10, openedVial: {value: null, notRecorded: true}};
    openedVialLineItem5 = {"id": 17, "facilityVisitId": 3, "productVialName": "Penta10", "packSize": 10, openedVial: {value: null, notRecorded: true}};

    childCoverageJSON = {facilityVisitId: 234,
      childCoverageLineItems: [
        childCoverageLineItem1,
        childCoverageLineItem2,
        childCoverageLineItem3,
        childCoverageLineItem4,
        childCoverageLineItem5
      ],
      openedVialLineItems: [
        openedVialLineItem1,
        openedVialLineItem2,
        openedVialLineItem3,
        openedVialLineItem4,
        openedVialLineItem5
      ]};

    distributionService.distribution = {facilityDistributions: {1: {childCoverage: childCoverageJSON}, 2: {}}};
    routeParams = {facility: 1};
    $controller(ChildCoverageController, {$scope: scope, $routeParams: routeParams, distributionService: distributionService});
  }));

  it('should set distribution in scope', function () {
    expect(scope.distribution).toEqual(distributionService.distribution);
  });

  it('should set facility id in scope from route params', function () {
    expect(scope.selectedFacilityId).toEqual(routeParams.facility);
  });

  it('should set child coverage in scope', function () {
    expect(scope.childCoverage).toEqual(childCoverageJSON);
  });

  it('should set childCoverageMap in scope', function () {
    var childCoverageMap = {
      'BCG': childCoverageLineItem1,
      'Polio (Newborn)': childCoverageLineItem2,
      'Polio 1st dose': childCoverageLineItem3,
      'Polio 2nd dose': childCoverageLineItem4,
      'Polio 3rd dose': childCoverageLineItem5
    };

    expect(scope.childCoverageMap).toEqual(childCoverageMap);
  });

  it('should set openedVialsMap in scope', function () {
    var openedVialMap = {
      'BCG': openedVialLineItem1,
      'Polio10': openedVialLineItem2,
      'Polio20': openedVialLineItem3,
      'Penta1': openedVialLineItem4,
      'Penta10': openedVialLineItem5
    };
    expect(scope.openedVialMap).toEqual(openedVialMap);
  });

  it('should return false if vaccination present in show cell list', function () {
    var isVisible = scope.hideCell(childCoverageJSON.childCoverageLineItems[0].vaccination);
    expect(isVisible).toBeFalsy();
  });

  it('should return true if vaccination is not present in show cell list', function () {
    var isVisible = scope.hideCell(childCoverageJSON.childCoverageLineItems[2].vaccination);
    expect(isVisible).toBeTruthy();
  });

  it('should synchronize NR state of Polio10 with Polio20 and Penta1 with Penta10', function () {
    childCoverageJSON.openedVialLineItems[2].openedVial.notRecorded = false;
    childCoverageJSON.openedVialLineItems[4].openedVial.notRecorded = false;
    scope.$apply();
    expect(childCoverageJSON.openedVialLineItems[1].openedVial.notRecorded).toBeFalsy();
    expect(childCoverageJSON.openedVialLineItems[3].openedVial.notRecorded).toBeFalsy();
  });

  it('should add value of healthCenter and outReach if not undefined', function () {
    childCoverageLineItem1.healthCenter11Months.value = 5;
    childCoverageLineItem1.outreach11Months.value = 10;
    var total = scope.getTotal(childCoverageLineItem1.healthCenter11Months, childCoverageLineItem1.outreach11Months);

    expect(total).toEqual(15);
  });

  it('should return total as 0 if value of healthCenter and outReach is undefined', function () {
    childCoverageLineItem1.outreach11Months = undefined;
    var total = scope.getTotal(childCoverageLineItem1.healthCenter11Months, childCoverageLineItem1.outreach11Months);

    expect(total).toEqual(0);
  });

  it('should get total vaccination for childCoverageLineItem', function () {
    childCoverageLineItem1.healthCenter11Months.value = 5;
    childCoverageLineItem1.healthCenter23Months.value = 5;
    childCoverageLineItem1.outreach11Months.value = 10;
    childCoverageLineItem1.outreach23Months.value = 10;
    var total = scope.getTotalVaccinations(childCoverageLineItem1);

    expect(total).toEqual(30);
  });

  it('should calculate coverage rate using total vaccination and target group of childCoverageLineItem', function () {
    var total = scope.calculateCoverageRate(15, 30);
    expect(total).toEqual(50);
  });

  it('should return coverage rate as null if target group is null', function () {
    var total = scope.calculateCoverageRate(15, null);
    expect(total).toEqual(null);
  });

  it('should calculate wastage rate for vaccinations', function () {
    var productsForVaccination = {
      products: ['BCG'],
      vaccinations: ['BCG'],
      rowSpan: 1
    };
    childCoverageJSON.childCoverageLineItems[0].healthCenter11Months.value = 5;
    childCoverageJSON.childCoverageLineItems[0].healthCenter23Months.value = 5;
    childCoverageJSON.childCoverageLineItems[0].outreach11Months.value = 10;
    childCoverageJSON.childCoverageLineItems[0].outreach23Months.value = 10;
    childCoverageJSON.openedVialLineItems[0].openedVial.value = 2;

    var wastageRate = scope.calculateWastageRate(productsForVaccination);

    expect(wastageRate).toEqual(-50);
  });


  it('should calculate wastage rate as null if packSize is undefined', function () {
    var productsForVaccination = {
      products: ['BCG'],
      vaccinations: ['BCG'],
      rowSpan: 1
    };
    childCoverageJSON.childCoverageLineItems[0].healthCenter11Months.value = 5;
    childCoverageJSON.childCoverageLineItems[0].healthCenter23Months.value = 5;
    childCoverageJSON.childCoverageLineItems[0].outreach11Months.value = 10;
    childCoverageJSON.childCoverageLineItems[0].outreach23Months.value = 10;
    childCoverageJSON.openedVialLineItems[0].openedVial.value = 2;
    childCoverageJSON.openedVialLineItems[0].packSize = undefined;

    var wastageRate = scope.calculateWastageRate(productsForVaccination);

    expect(wastageRate).toEqual(null);
  });

  it('should not consider openedVial value in calculating wastage rate if undefined', function () {
    var productsForVaccination = {
      products: ['Polio10', 'Polio20'],
      vaccinations: ['Polio (Newborn)', 'Polio 1st dose', 'Polio 2nd dose', 'Polio 3rd dose'],
      rowSpan: 4
    };
    childCoverageJSON.childCoverageLineItems[2].healthCenter11Months.value = 5;
    childCoverageJSON.childCoverageLineItems[2].healthCenter23Months.value = 5;
    childCoverageJSON.childCoverageLineItems[2].outreach11Months.value = 10;
    childCoverageJSON.childCoverageLineItems[2].outreach23Months.value = 10;
    childCoverageJSON.openedVialLineItems[1].openedVial.value = 2;

    var wastageRate = scope.calculateWastageRate(productsForVaccination);

    expect(wastageRate).toEqual(-50);
  });

  it('should applyNR to all readings', function () {
    scope.childCoverage = new ChildCoverage(234, scope.childCoverageJSON);
    spyOn(distributionService, 'applyNR');
    spyOn(scope.childCoverage, 'setNotRecorded');

    scope.applyNRAll();

    expect(distributionService.applyNR).toHaveBeenCalled();

    distributionService.applyNR.calls[0].args[0]();

    expect(scope.childCoverage.setNotRecorded).toHaveBeenCalled();
  });

});