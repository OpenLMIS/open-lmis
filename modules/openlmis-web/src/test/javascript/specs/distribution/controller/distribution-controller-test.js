/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe('DistributionController', function () {
  var scope, controller, httpBackend, messageService, distributionService;

  beforeEach(module('openlmis.services'));
  beforeEach(module('openlmis.localStorage'));
  beforeEach(module('distribution'));
  beforeEach(module(function ($provide) {
    $provide.value('IndexedDB', {put: function () {
    }});
  }));

  beforeEach(inject(function ($rootScope, $controller, $httpBackend, _messageService_, _distributionService_) {
    messageService = _messageService_;
    scope = $rootScope.$new();
    controller = $controller;
    httpBackend = $httpBackend;

    distributionService = _distributionService_;
    spyOn(distributionService, 'put');

    spyOn(OpenLmisDialog, 'newDialog');

    controller(DistributionController, {$scope: scope, deliveryZones: [], messageService: messageService});
  }));

  it('should load programs', function () {
    scope.selectedZone = {id: 1};
    var programs = {deliveryZonePrograms: [
      {id: 1}
    ]};
    httpBackend.expect('GET', '/deliveryZones/1/activePrograms.json').respond(200, programs);

    scope.loadPrograms();
    httpBackend.flush();

    expect(scope.programs).toEqual(programs.deliveryZonePrograms);
  });

  it('should load periods and set only top 13 periods in scope', function () {
    scope.selectedProgram = {id: 2};
    scope.selectedZone = {id: 1};
    var periods = {periods: [
      {id: 1},
      {id: 1},
      {id: 1},
      {id: 1},
      {id: 1},
      {id: 1},
      {id: 1},
      {id: 1},
      {id: 1},
      {id: 1},
      {id: 1},
      {id: 1},
      {id: 1},
      {id: 1},
      {id: 1},
      {id: 1}
    ]};
    httpBackend.expect('GET', '/deliveryZones/1/programs/2/periods.json').respond(200, periods);

    scope.loadPeriods();
    httpBackend.flush();

    expect(scope.periods.length).toEqual(13);
    expect(scope.periods).toEqual(periods.periods.slice(0, 13));
  });

  it('should not initiate the distribution if already cached', function () {
    spyOn(messageService, 'get');
    scope.selectedZone = {id: 1, name: 'zone1'};
    scope.selectedProgram = {id: 1, name: 'program1'};
    scope.selectedPeriod = {id: 1, name: 'period1'};
    spyOn(distributionService, 'isCached').andCallFake(function () {
      return true;
    });
    scope.initiateDistribution();
    expect(messageService.get).toHaveBeenCalledWith("message.distribution.already.cached", 'zone1', 'program1', 'period1');
  });

  it('should get reference data for a distribution if distribution not initiated', function () {
    spyOn(distributionService, 'isCached').andCallFake(function () {
      return false;
    });
    scope.selectedZone = {id: 4, name: 'zone1'};
    scope.selectedProgram = {id: 4, name: 'program1'};
    scope.selectedPeriod = {id: 3, name: 'period1'};
    var facilities = [
      {id: 2, name: "F1"}
    ];
    httpBackend.expect('POST', '/distributions.json').respond(200, {"success": "Data has been downloaded", distribution: {deliveryZone: {id: 1, name: 'zone1'}, program: {id: 1, name: 'program1'}, period: {id: 1, name: 'period1'}}});
    httpBackend.expect('GET', '/deliveryZones/4/programs/4/facilities.json').respond(200, {"facilities": [
      {'id': '23'}
    ]});
    httpBackend.expect('GET', '/deliveryZone/4/program/4/refrigerators.json').respond(200, {"refrigerators": [
      {'id': '1'}
    ]});

    scope.initiateDistribution();

    httpBackend.flush();

    setTimeout(function () {
      expect(distributionService.put).toHaveBeenCalled();
    });
  });

  it('should not initiate the distribution already initiated', function () {
    scope.selectedZone = {id: 4, name: 'zone1'};
    scope.selectedProgram = {id: 4, name: 'program1'};
    scope.selectedPeriod = {id: 4, name: 'period1'};

    httpBackend.expect('POST', '/distributions.json').respond(200, {"success": "Data has been downloaded", "distribution": {deliveryZone: {id: 1, name: 'zone1'}, program: {id: 1, name: 'program1'}, period: {id: 1, name: 'period1'}}});
    httpBackend.expect('GET', '/deliveryZones/4/programs/4/facilities.json').respond(200, {"facilities": [
      {'id': '23'}
    ]});
    httpBackend.expect('GET', '/deliveryZone/4/program/4/refrigerators.json').respond(200, {"refrigerators": [
      {'id': '1'}
    ]});

    scope.initiateDistribution();

    httpBackend.flush();
  });
});
