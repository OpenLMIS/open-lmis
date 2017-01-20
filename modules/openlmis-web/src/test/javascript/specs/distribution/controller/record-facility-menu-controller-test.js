/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
describe("RecordFacilityMenuController", function () {

  var scope, location, routeParams, distributionService, distribution;

  beforeEach(module("distribution"));
  beforeEach(inject(function ($rootScope, $location, $routeParams, $controller) {
    scope = $rootScope.$new();
    location = $location;
    routeParams = $routeParams;

    distributionService = {};

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
          },
          facilityTypeCode: 'lvl3_hospital'
        }
      }
    });

    distributionService.distribution = distribution;
    $routeParams.facility = 1;

    $controller(RecordFacilityMenuController, {$scope: scope, $routeParams: routeParams, $location: location, distributionService: distributionService});

  }));

  it('should set distribution data for facility in scope', function () {
    expect(scope.distributionData).toEqual(distribution.facilityDistributions[1]);
  });

});