/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

describe('InitiateMySupervisedFacilitiesRnrController', function () {
  var scope, ctrl, $httpBackend, facilities, programs;

  beforeEach(module('openlmis.services'));
  beforeEach(inject(function ($rootScope, _$httpBackend_, $controller) {
    scope = $rootScope.$new();
    $httpBackend = _$httpBackend_;
    facilities = [
      {"id": "10134", "name": "National Warehouse", "description": null}
    ];
    programs = [
      {"id": 10, "code": "HIV", "name": "HIV", "description": "HIV", "active": true}
    ];

    $httpBackend.expectGET('/create/requisition/supervised/programs.json').respond(200, {"programList": programs});
    ctrl = $controller(InitiateMySupervisedFacilitiesRnrController, {$scope: scope});
  }));

  it('should set user supervised programs in scope', function () {
    $httpBackend.flush();

    expect(scope.$parent.programs).toEqual(programs);
  });

  it('should load user supervised facilities for selected program for create R&R', function () {
    scope.$parent.selectedProgram = programs[0];
    $httpBackend.expectGET('/create/requisition/supervised/10/facilities.json').respond(200, {"facilities": facilities});

    scope.loadFacilities();
    $httpBackend.flush();

    expect(scope.$parent.facilities).toEqual(facilities);
  });

  it('should not load user supervised facilities if there is no selected program for create R&R', function () {
    scope.$parent.selectedProgram = null;

    scope.loadFacilities();
    $httpBackend.flush();

    expect(scope.$parent.facilities).toEqual(null);
    expect(scope.$parent.selectedFacilityId).toEqual(null);
  });
});