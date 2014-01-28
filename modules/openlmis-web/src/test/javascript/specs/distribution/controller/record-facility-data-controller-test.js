/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
describe("RecordFacilityDataController", function () {

  var scope, location, routeParams, distributionService, distribution, indexedDB;

  beforeEach(module("distribution"));
  beforeEach(function () {


    module(function ($provide) {
      $provide.value('IndexedDB', {put: function () {
      }, get: function () {
      }, delete: function () {
      }});
    });
    inject(function (IndexedDB, SharedDistributions, $rootScope, $location, $routeParams, $controller) {
      scope = $rootScope.$new();
      location = $location;
      routeParams = $routeParams;
      indexedDB = IndexedDB;
      spyOn(indexedDB, 'put');
      spyOn(indexedDB, 'delete');
      SharedDistributions.distributionList = [
        {deliveryZone: {id: 1, name: 'zone1'}, program: {id: 1, name: 'program1'}, period: {id: 1, name: 'period1'}},
        {deliveryZone: {id: 2}, program: {id: 2}, period: {id: 2}}
      ];

      distribution = new Distribution({
        facilityDistributions: {
          1: { refrigerators: {
            refrigeratorReadings: [
              {refrigerator: {serialNumber: "abc"}},
              {refrigerator: {serialNumber: "XYZ"}}
            ]
          },
            facilityVisit: {
              id: 1
            }
          }
        }
      });

      distributionService = {distribution: distribution};

      $routeParams.facility = 1;
      $routeParams.distribution = 3;

      $controller(RecordFacilityDataController, {$scope: scope, $routeParams: routeParams, $location: location, distributionService: distributionService});

    });

  });

  it('should set distribution data for facility in scope', function () {
    expect(scope.distribution).toEqual(distribution);
  });

  it('should set label to change facility if facility present', function () {
    expect(scope.label).toEqual('label.change.facility');
  });

  it('should change the route path for a selected facility', function () {
    scope.facilitySelected = {facilityId: 2};
    spyOn(location, 'path');
    scope.chooseFacility();
    expect(location.path).toHaveBeenCalledWith('record-facility-data/3/2/visit-info')
  });

  it('should format append each facility with its status icon', function () {
    var dropDownObject = { text: "Facility1", element: [
      {value: "1"}
    ]};

    var format = scope.format(dropDownObject);

    expect(format).toEqual("<div class='is-incomplete'><span id=1 class='status-icon'></span>Facility1</div>");
  });
});
