/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
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
      facilityDistributionData: {
        1: { refrigerators: {
          refrigeratorReadings: [
            {refrigerator: {serialNumber: "abc"}},
            {refrigerator: {serialNumber: "XYZ"}}
          ]
        }
        }
      }
    });

    distributionService.distribution = distribution;
    $routeParams.facility = 1;

    $controller(RecordFacilityMenuController, {$scope: scope, $routeParams: routeParams, $location: location, distributionService: distributionService});

  }));

  it('should set distribution data for facility in scope', function () {
    expect(scope.distributionData).toEqual(distribution.facilityDistributionData[1]);
  });

});