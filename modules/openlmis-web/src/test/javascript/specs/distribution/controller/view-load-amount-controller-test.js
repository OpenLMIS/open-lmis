/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

describe('ViewLoadAmountController', function () {
  var scope, controller, httpBackend;

  beforeEach(module('openlmis.services'));
  beforeEach(module('openlmis.localStorage'));
  beforeEach(inject(function ($rootScope, $controller, $httpBackend) {
    scope = $rootScope.$new();
    controller = $controller;
    httpBackend = $httpBackend;

    var facilities = [{id: 'F10', name : 'Village Dispensary' , geographicZone :{id: 1, level : {name : 'City' }},
      supportedPrograms :[{program:{}}]}];
    controller(ViewLoadAmountController, {$scope:scope, facilities:facilities, period :{}, deliveryZone: {}});
  }));

  it('should set Geographic zone level name', function () {
    expect(scope.geoZoneLevelName).toEqual('City');
  });

});
