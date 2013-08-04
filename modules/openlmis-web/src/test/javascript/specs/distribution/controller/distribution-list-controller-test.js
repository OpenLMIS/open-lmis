/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 *  If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

describe('DistributionListController', function () {

  var scope, location;
  var sharedDistribution;

  beforeEach(module('distribution'));
  beforeEach(module('IndexedDB'));

  beforeEach(inject(function ($rootScope, $location, $controller) {
    scope = $rootScope.$new();
    location = $location;
    var controller = $controller;
    sharedDistribution = {update: function () {
    }};

    spyOn(sharedDistribution, 'update');

    controller(DistributionListController, {$scope: scope, $location: location, SharedDistributions: sharedDistribution })
  }));

  it('should set location path', function () {
    var zpp = "1_2_3";
    var path = "/record-facility-data/" + zpp;
    var locationPath = spyOn(location, 'path').andCallThrough();

    scope.recordFacilityData(zpp);

    expect(locationPath).toHaveBeenCalledWith(path);
  });

  it('should refresh distributions list', function() {
    expect(sharedDistribution.update).toHaveBeenCalled();
  })

  it('should set distributions in scope', function() {
    expect(scope.sharedDistributions).toBe(sharedDistribution);
  })
});