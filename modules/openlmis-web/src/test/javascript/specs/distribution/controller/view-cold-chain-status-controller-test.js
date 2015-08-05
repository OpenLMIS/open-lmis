/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe('ViewColdChainStatusController', function () {
  var scope, controller, httpBackend, program1, facilities;

  beforeEach(module('openlmis'));
  beforeEach(inject(function ($rootScope, $controller, $httpBackend) {
    scope = $rootScope.$new();
    controller = $controller;
    httpBackend = $httpBackend;
    program1 = {id: 1, name: 'Vaccine'};
    facilities = [];
    ];
    controller(ViewLoadAmountController, {$scope: scope, facilities: facilities, period: {id: 1, name: 'period 1'}, deliveryZone: {id: 1}});

  }));

  it('should set no records found message if no facilities are found', function () {
    controller(ViewLoadAmountController, {$scope: scope, facilities: [], period: {}, deliveryZone: {}});
    expect(scope.message).toEqual("msg.delivery.zone.no.record");
  });

  it('should set no records found message if no facilities are undefined', function () {
    controller(ViewLoadAmountController, {$scope: scope, facilities: undefined, period: {}, deliveryZone: {}});
    expect(scope.message).toEqual("msg.delivery.zone.no.record");
  });

  it('should set program name', function () {
    expect(scope.program).toEqual(program1);
  });

  it('should set period', function () {
    expect(scope.period).toEqual({id: 1, name: 'period 1'});
  });

});
