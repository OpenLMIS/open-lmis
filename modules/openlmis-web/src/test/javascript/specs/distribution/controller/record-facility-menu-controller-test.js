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

    distributionService = {getDistribution: function () {
    }};

    distribution = {
      facilityDistributionData: {
        1: { refrigerators: {
          refrigeratorReadings: [
            {refrigerator: {serialNumber: "abc"}},
            {refrigerator: {serialNumber: "XYZ"}}
          ]
        }
        }
      }
    };

    distributionService.distribution = distribution;
    $routeParams.facility = 1;

    $controller(RecordFacilityMenuController, {$scope: scope, $routeParams: routeParams, $location: location, distributionService: distributionService});

  }))

  it('should set status indicator to complete if all refrigeratorReadings are complete', function () {
    scope.distribution.facilityDistributionData[1].refrigerators.refrigeratorReadings = [
      {status: 'is-complete'}
    ];

    var status = scope.getRefrigeratorStatus();

    expect(status).toEqual('is-complete');
  });

  it('should set status indicator to empty if all refrigeratorReadings are empty', function () {
    scope.distribution.facilityDistributionData[1].refrigerators.refrigeratorReadings = [
      {status: 'is-empty'},
      {status: 'is-empty'}
    ];

    var status = scope.getRefrigeratorStatus();

    expect(status).toEqual('is-empty');
  });

  it('should set status indicator to incomplete if at least one refrigeratorReading is incomplete', function () {
    scope.distribution.facilityDistributionData[1].refrigerators.refrigeratorReadings = [
      {status: 'is-incomplete'}
    ];

    var status = scope.getRefrigeratorStatus();

    expect(status).toEqual('is-incomplete');
  });

  it('should set status indicator to incomplete if at least one refrigeratorReading is complete and rest are empty',
    function () {
      scope.distribution.facilityDistributionData[1].refrigerators.refrigeratorReadings = [
        {status: 'is-complete'},
        {status: 'is-complete'},
        {status: 'is-empty'}
      ];

      var status = scope.getRefrigeratorStatus();

      expect(status).toEqual('is-incomplete');
    });

  it('should set status indicator to complete if no refrigeratorReading exists', function () {
    scope.distribution.facilityDistributionData[1].refrigerators.refrigeratorReadings = [];

    var status = scope.getRefrigeratorStatus();

    expect(status).toEqual('is-complete');
  });
});