/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

describe('DistributionController', function () {
  var scope, controller, httpBackend;

  beforeEach(module('openlmis.services'));
  beforeEach(module('openlmis.localStorage'));
  beforeEach(inject(function ($rootScope, $controller, $httpBackend) {
    scope = $rootScope.$new();
    controller = $controller;
    httpBackend = $httpBackend;

    controller(DistributionController, {$scope:scope, deliveryZones:[]});
  }));

  it('should load programs', function () {
    scope.selectedZone = {id:1};
    var programs = {deliveryZonePrograms:[
      {id:1}
    ]};
    httpBackend.expect('GET', '/deliveryZones/1/programs.json').respond(200, programs);

    scope.loadPrograms();
    httpBackend.flush();

    expect(scope.programs).toEqual(programs.deliveryZonePrograms);
  });

  it('should load periods', function () {
    scope.selectedProgram = {id:2};
    scope.selectedZone = {id:1};
    var periods = {periods:[
      {id:1}
    ]};
    httpBackend.expect('GET', '/deliveryZones/1/programs/2/periods.json').respond(200, periods);

    scope.loadPeriods();
    httpBackend.flush();

    expect(scope.periods).toEqual(periods.periods);
  });
});
