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

    var program1 = {program:{id : 1, name : 'Vaccine'}};
    var facilities = [{id: 'F10', name : 'Village Dispensary' , geographicZone :{id: 1, level : {name : 'City' }},
      supportedPrograms :[{program : program1}]}];
    controller(ViewLoadAmountController, {$scope:scope, facilities:facilities, period :{id: 1, name : 'period 1'}, deliveryZone: {}});
  }));

  it('should set no records found message if no facilities are found', function () {
    controller(ViewLoadAmountController, {$scope:scope, facilities:[], period :{}, deliveryZone: {}});
    expect(scope.message).toEqual("msg.delivery.zone.no.record");
  });

  it('should set Geographic zone level name', function () {
    expect(scope.geoZoneLevelName).toEqual('City');
  });

  it('should set program name', function () {
    expect(scope.program).toEqual({program:{id : 1, name : 'Vaccine'}});
  });

  it('should set period', function () {
    expect(scope.period).toEqual({id: 1, name : 'period 1'});
  });

});
