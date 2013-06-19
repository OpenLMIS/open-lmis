/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

describe("ISA Controller", function () {
  beforeEach(module('openlmis.services'));
  beforeEach(module('ui.bootstrap.dialog'));
  beforeEach(module('openlmis.localStorage'));

  describe("Override ISA", function () {
    var scope, httpBackend, ctrl, routeParams;

    beforeEach(inject(function ($rootScope, _$httpBackend_, $controller, $routeParams) {
      scope = $rootScope.$new();
      scope.$parent = $rootScope.$new();
      scope.$parent.allocationProgramProductsList = []
      routeParams = $routeParams;
      httpBackend = _$httpBackend_;
      $rootScope.fixToolBar = function () {
      };
      ctrl = $controller(IsaModalController, {$scope: scope, facilityProgramProducts: null, $routeParams: routeParams});
    }));

    it('should update ISA from current products', function() {
      scope.currentProgramProducts = [{programProductId: 1, facilityId: 1, overriddenIsa: 34},
        {programProductId: 2, facilityId: 1, overriddenIsa: 45}];
      scope.currentProgram = {id : 1};
      httpBackend.when('GET', '/facility/1/program/1/isa.json').respond(scope.currentProgramProducts);
      scope.updateISA();
      expect(scope.$parent.allocationProgramProductsList[scope.currentProgram.id].length).toEqual(2);
    });

  });
});