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
    var programProducts = [
          {product: {id: 1, name: 'polio10', productGroup: {name: 'polio'}}},
          {product: {id: 2, name: 'polio20', productGroup: {name: 'polio'}}},
          {product: {id: 3, name: 'penta1', productGroup: {name: 'penta'}}},
          {product: {id: 4, name: 'blank', productGroup: {name: ''}}}
        ];
    facilities = [
          {id: 'F10', name: 'Village Dispensary', geographicZone: {id: 1, name: 'Ngrogoro', level: {name: 'City' }}, catchmentPopulation: 200,
            supportedPrograms: [
              {program: program1, programProducts: programProducts}
            ]},
          {id: 'F11', name: 'Central Hospital', geographicZone: {id: 1, name: 'District 1', level: {name: 'City' }}, catchmentPopulation: 150,
            supportedPrograms: [
              {program: program1, programProducts: programProducts}
            ]}
        ];

    controller(ViewColdChainStatusController, {$scope: scope, facilities: facilities, period: {id: 1, name: 'period 1'}, deliveryZone: {id: 1}, fridges : {}});
  }));

  it('should set no records found message if no facilities are found', function () {
    controller(ViewColdChainStatusController, {$scope: scope, facilities: [], period: {}, deliveryZone: {}, fridges : {}});
    expect(scope.message).toEqual("msg.delivery.zone.no.record");
  });

  it('should set no records found message if no facilities are undefined', function () {
    controller(ViewColdChainStatusController, {$scope: scope, facilities: undefined, period: {}, deliveryZone: {}, fridges : {}});
    expect(scope.message).toEqual("msg.delivery.zone.no.record");
  });

  it('should set no cold chain status information available message if no data', function () {
    controller(ViewColdChainStatusController, {$scope: scope, facilities: facilities, period: {}, deliveryZone: {}, fridges : { fridges: [] }});
    expect(scope.message).toEqual("label.no.cold.chain.status.information");
  });

  it('should set no cold chain status information available message if undefined data', function () {
    controller(ViewColdChainStatusController, {$scope: scope, facilities: facilities, period: {}, deliveryZone: {}, fridges : { fridges: undefined }});
    expect(scope.message).toEqual("label.no.cold.chain.status.information");
  });

  it('should set program name', function () {
    expect(scope.program).toEqual(program1);
  });

  it('should set period', function () {
    expect(scope.period).toEqual({id: 1, name: 'period 1'});
  });

});
