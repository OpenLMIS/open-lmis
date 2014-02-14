/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe('RefrigeratorController', function () {
  var scope, distributionService, IndexedDB, distribution;

  beforeEach(module('distribution'));
  beforeEach(inject(function ($rootScope, $controller, $routeParams) {
    IndexedDB = {
      execute: function () {
      },
      get: function () {

      },
      put: function () {

      }
    };

    scope = $rootScope.$new();

    distributionService = {};

    distribution = new Distribution({
      facilityDistributions: {
        1: { refrigerators: {
          readings: [
            {refrigerator: {serialNumber: "abc"}},
            {refrigerator: {serialNumber: "XYZ"}}
          ]
        }, facilityVisit: {
          id: 1,
          visited: true
        }
        }
      }
    });

    distributionService.distribution = distribution;

    $routeParams.facility = 1;
    $controller(RefrigeratorController, {$scope: scope, IndexedDB: IndexedDB, distributionService: distributionService});
  }))
  ;

  it('should initialize controller', function () {
    expect(scope.distribution).toEqual(distribution);
    expect(scope.selectedFacilityId).toEqual(1);
  });

  it('should set edit for specific serial number', function () {
    scope.edit = [
      {'key1': true, 'key2': false}
    ];
    scope.setEdit('key2');
    expect(scope.edit.key1).toBeFalsy();
    expect(scope.edit.key2).toBeTruthy();
  });

  it('should set duplicate serial number if serial number already exists', function () {
    scope.newRefrigeratorReading = {refrigerator: {serialNumber: "Abc"}};
    scope.addRefrigeratorToStore();
    expect(scope.isDuplicateSerialNumber).toBeTruthy();
  });

  it('should add new refrigerator if serial number does not exist', function () {
    scope.newRefrigeratorReading = {refrigerator: {serialNumber: "Abcc"}};
    spyOn(IndexedDB, 'put').andCallThrough();

    scope.addRefrigeratorToStore();

    expect(scope.addRefrigeratorModal).toBeUndefined();
    expect(scope.isDuplicateSerialNumber).toBeUndefined();
    expect(scope.isDuplicateSerialNumber).toBeFalsy();
    expect(scope.distribution.facilityDistributions[1].refrigerators.readings.length).toEqual(3);
    expect(IndexedDB.put).toHaveBeenCalledWith('distributions', scope.distribution);
  });

  it('should return true if facility visit is false', function () {
    scope.distribution = distribution;
    scope.selectedFacilityId = 1;
    scope.distribution.facilityDistributions[1].facilityVisit = {
      id: 1,
      visited: false
    };
    expect(scope.isFormDisabled()).toEqual(true);
  });

  it('should return true if facility distribution is synced', function () {
    scope.selectedFacilityId = 1;
    scope.distribution = distribution;
    scope.distribution.facilityDistributions[1].status = DistributionStatus.SYNCED;
    expect(scope.isFormDisabled()).toEqual(true);
  });

});
