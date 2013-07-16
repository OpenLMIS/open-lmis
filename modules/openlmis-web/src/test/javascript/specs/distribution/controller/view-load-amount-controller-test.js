/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

describe('ViewLoadAmountController', function () {
  var scope, controller, httpBackend, program1, facilities;

  beforeEach(module('openlmis.services'));
  beforeEach(module('openlmis.localStorage'));
  beforeEach(inject(function ($rootScope, $controller, $httpBackend) {
    scope = $rootScope.$new();
    controller = $controller;
    httpBackend = $httpBackend;
    program1 = {id:1, name:'Vaccine'};
    var programProducts = [
      {product:{id:1, name:'polio10', productGroup:{name:'polio'}}},
      {product:{id:2, name:'polio20', productGroup:{name:'polio'}}},
      {product:{id:3, name:'penta1', productGroup:{name:'penta'}}}
    ];
    facilities = [
      {id:'F10', name:'Village Dispensary', geographicZone:{id:1, name:'Ngrogoro', level:{name:'City' }}, catchmentPopulation: 200,
        supportedPrograms:[
          {program:program1, programProducts:programProducts}
        ]},
      {id:'F11', name:'Central Hospital', geographicZone:{id:1, name:'District 1', level:{name:'City' }}, catchmentPopulation: 150,
        supportedPrograms:[
          {program:program1, programProducts:programProducts}
        ]}
    ];
    controller(ViewLoadAmountController, {$scope:scope, facilities:facilities, period:{id:1, name:'period 1'}, deliveryZone:{id:1}});

  }));

  it('should set no records found message if no facilities are found', function () {
    controller(ViewLoadAmountController, {$scope:scope, facilities:[], period:{}, deliveryZone:{}});
    expect(scope.message).toEqual("msg.delivery.zone.no.record");
  });

  it('should set Geographic zone level name', function () {
    expect(scope.geoZoneLevelName).toEqual('City');
  });

  it('should set program name', function () {
    expect(scope.program).toEqual(program1);
  });

  it('should set period', function () {
    expect(scope.period).toEqual({id:1, name:'period 1'});
  });

  it('should group program products of each facility by product group name', function () {
    expect(scope.facilityMap['Ngrogoro'][0].supportedPrograms[0].programProductMap['polio'].length).toEqual(2);
    expect(scope.facilityMap['Ngrogoro'][0].supportedPrograms[0].programProductMap['penta'].length).toEqual(1);
  });

  it('should sort program products of each facility by product group name', function () {
    expect(scope.facilityMap['Ngrogoro'][0].supportedPrograms[0].sortedProductGroup).toEqual(['penta', 'polio']);
  });

  it('should sort facilities geo zones on the basis of geo zone name', function () {
    expect(scope.sortedGeoZoneKeys).toEqual(['District 1', 'Ngrogoro']);
  });

  it('should get all program products of all product groups in facility in order by product group name as an array', function () {
    var programProducts = scope.getProgramProducts(facilities[0]);
    expect(programProducts.length).toEqual(3);
    expect(programProducts[0].product.name).toEqual('penta1');
    expect(programProducts[1].product.name).toEqual('polio10');
    expect(programProducts[2].product.name).toEqual('polio20');
  });

  it('should get all program products of all product groups in facility in order by product group name as an array', function () {
    var programProducts = scope.getProgramProducts(facilities[0]);
    expect(programProducts.length).toEqual(3);
    expect(programProducts[0].product.name).toEqual('penta1');
    expect(programProducts[1].product.name).toEqual('polio10');
    expect(programProducts[2].product.name).toEqual('polio20');
  });

  it('should calculate total population of all geo zones', function(){
    expect(scope.zonesTotal['totalPopulation']).toEqual(350);
  });

  it('should calculate total population by each geo zone for all geo zones', function(){
    expect(scope.aggregateMap['Ngrogoro']['totalPopulation']).toEqual(200);
    expect(scope.aggregateMap['District 1']['totalPopulation']).toEqual(150);
  });
});

describe("View load amount resolve", function () {
  var $httpBackend, ctrl, $timeout, $route, $q;
  var deferredObject;
  beforeEach(module('openlmis.services'));
  beforeEach(inject(function (_$httpBackend_, $controller, _$timeout_, _$route_) {
    $httpBackend = _$httpBackend_;
    deferredObject = {promise:{id:1}, resolve:function () {
    }};
    spyOn(deferredObject, 'resolve');
    $q = {defer:function () {
      return deferredObject
    }};
    $timeout = _$timeout_;
    ctrl = $controller;
    $route = _$route_;
  }));

  it('should get all facilities with their Isa amount in delivery zone', function () {
    $route = {current:{params:{deliveryZoneId:1, programId:2}}};
    $httpBackend.expect('GET', '/deliveryZones/1/programs/2/facilities.json').respond([]);
    ctrl(ViewLoadAmountController.resolve.facilities, {$q:$q, $route : $route});
    $timeout.flush();
    $httpBackend.flush();
    expect(deferredObject.resolve).toHaveBeenCalled();
  });

  it('should get period reference data', function () {
    $route = {current:{params:{periodId:2}}};
    $httpBackend.expect('GET', '/periods/2.json').respond([]);
    ctrl(ViewLoadAmountController.resolve.period, {$q:$q, $route : $route});
    $timeout.flush();
    $httpBackend.flush();
    expect(deferredObject.resolve).toHaveBeenCalled();
  });


  it('should get period reference data', function () {
    $route = {current:{params:{deliveryZoneId:2}}};
    $httpBackend.expect('GET', '/deliveryZones/2.json').respond([]);
    ctrl(ViewLoadAmountController.resolve.deliveryZone, {$q:$q, $route : $route});
    $timeout.flush();
    $httpBackend.flush();
    expect(deferredObject.resolve).toHaveBeenCalled();
  });


});
