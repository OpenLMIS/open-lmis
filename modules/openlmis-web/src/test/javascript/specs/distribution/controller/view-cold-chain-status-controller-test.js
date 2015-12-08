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
  var scope, controller, httpBackend, program1, facilities, fridges;

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
          {id: 'F10', name: 'Village Dispensary', geographicZone: {id: 1, name: 'Ngrogoro', level: {name: 'City 1' }, parent: { name: 'Province 1' }}, catchmentPopulation: 200,
            supportedPrograms: [
              {program: program1, programProducts: programProducts}
            ]},
          {id: 'F11', name: 'Central Hospital', geographicZone: {id: 1, name: 'District 1', level: {name: 'City 2' }, parent: { name: 'Province 2' }}, catchmentPopulation: 150,
            supportedPrograms: [
              {program: program1, programProducts: programProducts}
            ]}
        ],
    fridges = {
        fridges: [
            { Status: 1 }, { Status: 2 }, { Status: 3 }, { Status: 4 }
        ]
    };

    controller(ViewColdChainStatusController, {$scope: scope, facilities: facilities, period: {id: 1, name: 'period 1'}, deliveryZone: {id: 1}, fridges : fridges});
  }));

  it('should set no records found message if no facilities are found', function () {
    controller(ViewColdChainStatusController, {$scope: scope, facilities: [], period: {}, deliveryZone: {}, fridges : {}});
    expect(scope.message).toEqual("msg.delivery.zone.no.record");
  });

  it('should set api error message if fridges is undefined', function () {
    controller(ViewColdChainStatusController, {$scope: scope, facilities: facilities, period: {}, deliveryZone: {}, fridges : undefined});
    expect(scope.apimessage).toEqual("message.api.error");
    expect(scope.message).toEqual("");
    expect(scope.apiError).toEqual(true);
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

  it('should find facility by id', function () {
    var i;

    for (i = 0; i < facilities.length; i += 1) {
        expect(scope.getFacilityById(facilities[i].id)).toEqual(facilities[i]);
    }
  });

  it('should find facility name by id', function () {
    var i;

    for (i = 0; i < facilities.length; i += 1) {
        expect(scope.getFacilityNameById(facilities[i].id)).toEqual(facilities[i].name);
    }
  });

  it('should find district name by facility id', function () {
    var i;

    for (i = 0; i < facilities.length; i += 1) {
        expect(scope.getDistrictNameByFacilityId(facilities[i].id)).toEqual(facilities[i].geographicZone.name);
    }
  });

  it('should find province name by facility id', function () {
    var i;

    for (i = 0; i < facilities.length; i += 1) {
        expect(scope.getProvinceNameByFacilityId(facilities[i].id)).toEqual(facilities[i].geographicZone.parent.name);
    }
  });

  it('should split fridges by status', function () {
    expect(scope.failedRefrigerators.length).toEqual(1);
    expect(scope.failedRefrigerators[0]).toEqual({ Status: 1 });

    expect(scope.followUpRefrigerators.length).toEqual(1);
    expect(scope.followUpRefrigerators[0]).toEqual({ Status: 2 });

    expect(scope.workingRefrigerators.length).toEqual(1);
    expect(scope.workingRefrigerators[0]).toEqual({ Status: 3 });

    expect(scope.noDataRefrigerators.length).toEqual(1);
    expect(scope.noDataRefrigerators[0]).toEqual({ Status: 4 });
  });

});
