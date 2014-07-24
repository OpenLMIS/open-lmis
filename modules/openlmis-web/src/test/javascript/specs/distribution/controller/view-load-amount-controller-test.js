/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe('ViewLoadAmountController', function () {
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
    controller(ViewLoadAmountController, {$scope: scope, facilities: facilities, period: {id: 1, name: 'period 1'}, deliveryZone: {id: 1}});

  }));

  it('should set no records found message if no facilities are found', function () {
    controller(ViewLoadAmountController, {$scope: scope, facilities: [], period: {}, deliveryZone: {}});
    expect(scope.message).toEqual("msg.no.records.found");
  });

  it('should set no records found message if no facilities are undefined', function () {
    controller(ViewLoadAmountController, {$scope: scope, facilities: undefined, period: {}, deliveryZone: {}});
    expect(scope.message).toEqual("msg.no.records.found");
  });

  it('should set Geographic zone level name', function () {
    expect(scope.geoZoneLevelName).toEqual('City');
  });

  it('should set program name', function () {
    expect(scope.program).toEqual(program1);
  });

  it('should set period', function () {
    expect(scope.period).toEqual({id: 1, name: 'period 1'});
  });

  it('should group program products of each facility by product group name', function () {
    expect(scope.facilityMap['Ngrogoro'][0].supportedPrograms[0].programProductMap['polio'].length).toEqual(2);
    expect(scope.facilityMap['Ngrogoro'][0].supportedPrograms[0].programProductMap['penta'].length).toEqual(1);
  });

  it('should sort program products of each facility by product group name', function () {
    expect(scope.facilityMap['Ngrogoro'][0].supportedPrograms[0].sortedProductGroup).toEqual(['penta', 'polio', '']);
  });

  it('should sort facilities geo zones on the basis of geo zone name', function () {
    expect(scope.sortedGeoZoneKeys).toEqual(['District 1', 'Ngrogoro']);
  });

  it('should get all program products of all product groups in facility in order by product group name as an array and push blank to last', function () {
    var programProducts = scope.getProgramProducts(facilities[0]);
    expect(programProducts.length).toEqual(4);
    expect(programProducts[0].product.name).toEqual('penta1');
    expect(programProducts[1].product.name).toEqual('polio10');
    expect(programProducts[2].product.name).toEqual('polio20');
    expect(programProducts[3].product.name).toEqual('blank');
  });


  it('should calculate total population of all geo zones', function () {
    expect(scope.zonesTotal['totalPopulation']).toEqual(350);
  });

  it('should calculate total population by each geo zone for all geo zones', function () {
    expect(scope.aggregateMap['Ngrogoro']['totalPopulation']).toEqual(200);
    expect(scope.aggregateMap['District 1']['totalPopulation']).toEqual(150);
  });

  it('should get program products for total of zones', function () {
    var product1 = {
      name: "product1",
      productGroup: {
        name: 'group1'
      }
    };
    scope.zonesTotal = {
      totalProgramProductsMap: {
        group1: [
          {
            product: product1
          },
          {
            product: {
              productGroup: {
                name: 'group1'
              }
            }
          }
        ],
        group2: [
          {
            product: {
              productGroup: {
                name: 'group2'
              }
            }
          }
        ],
        group3: [
          {
            product: {
              productGroup: {
                name: 'group3'
              }
            }
          }
        ]
      }
    };
    scope.sortedGeoZoneKeys = ['zone1', 'zone2'];
    scope.aggregateMap = {zone1: {
      sortedProductGroup: [ "group1", "group2", "group3"]
    }};

    var programProducts = scope.getProgramProductsForAggregateRow(null, scope.zonesTotal);

    expect(programProducts.length).toEqual(4);
    expect(programProducts[0]).toEqual({product: product1});
  });
});

describe("View load amount resolve", function () {
  var $httpBackend, ctrl, $timeout, $route, $q;
  var deferredObject;
  beforeEach(module('openlmis'));
  beforeEach(inject(function (_$httpBackend_, $controller, _$timeout_, _$route_) {
    $httpBackend = _$httpBackend_;
    deferredObject = {promise: {id: 1}, resolve: function () {
    }};
    spyOn(deferredObject, 'resolve');
    $q = {defer: function () {
      return deferredObject
    }};
    $timeout = _$timeout_;
    ctrl = $controller;
    $route = _$route_;
  }));

  it('should get all facilities with their Isa amount in delivery zone', function () {
    $route = {current: {params: {deliveryZoneId: 1, programId: 2}}};
    $httpBackend.expect('GET', '/deliveryZones/1/programs/2/facilities.json').respond(200);

    ctrl(ViewLoadAmountController.resolve.facilities, {$q: $q, $route: $route});

    $timeout.flush();
    $httpBackend.flush();
    expect(deferredObject.resolve).toHaveBeenCalled();
  });

  it('should get period reference data', function () {
    $route = {current: {params: {periodId: 2}}};
    $httpBackend.expect('GET', '/periods/2.json').respond(200);
    ctrl(ViewLoadAmountController.resolve.period, {$q: $q, $route: $route});
    $timeout.flush();
    $httpBackend.flush();
    expect(deferredObject.resolve).toHaveBeenCalled();
  });

  it('should get period reference data', function () {
    $route = {current: {params: {deliveryZoneId: 2}}};
    $httpBackend.expect('GET', '/deliveryZones/2.json').respond(200);
    ctrl(ViewLoadAmountController.resolve.deliveryZone, {$q: $q, $route: $route});
    $timeout.flush();
    $httpBackend.flush();
    expect(deferredObject.resolve).toHaveBeenCalled();
  });


});
