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
  var scope, controller, httpBackend, messageService, distributionService, rootScope;

  beforeEach(module('openlmis'));
  beforeEach(module('openlmis.localStorage'));
  beforeEach(module('distribution'));
  beforeEach(module(function ($provide) {
    $provide.value('IndexedDB', {put: function () {
    }});
  }));

  beforeEach(inject(function ($rootScope, $controller, $httpBackend, _messageService_, _distributionService_) {
    messageService = _messageService_;
    rootScope = $rootScope;
    scope = $rootScope.$new();
    controller = $controller;
    httpBackend = $httpBackend;

    distributionService = _distributionService_;
    spyOn(distributionService, 'save');

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

  it('should not initiate and cache the distribution if errored response', function () {
    scope.selectedZone = {id: 1, name: 'zone1'};
    scope.selectedProgram = {id: 1, name: 'program1'};
    scope.selectedPeriod = {id: 1, name: 'period1'};
    spyOn(distributionService, 'isCached').andCallFake(function () {
      return false;
    });
    var postResponse = {"data" :{"error" : "no facilities in delivery zone"}};
    httpBackend.expect('POST', '/distributions.json').respond(412, postResponse);

    scope.initiateDistribution();

    expect(scope.message).toEqual(postResponse.error);
  });


  it('should confirm caching of already initiated distribution', function () {
    scope.selectedZone = {id: 4, name: 'zone1'};
    scope.selectedProgram = {id: 4, name: 'program1'};
    scope.selectedPeriod = {id: 4, name: 'period1'};

    var postResponse = {"success": "Data has been downloaded", message:"already initiated", "distribution": {facilityDistributions: {}, deliveryZone: {id: 1, name: 'zone1'}, program: {id: 1, name: 'program1'}, period: {id: 1, name: 'period1'}}};
    httpBackend.expect('POST', '/distributions.json').respond(200, postResponse);

    scope.initiateDistribution();

    httpBackend.flush();
    expect(OpenLmisDialog.newDialog.calls[0].args[0].body).toEqual(postResponse.message);
    OpenLmisDialog.newDialog.calls[0].args[1](true);

    expect(distributionService.save).toHaveBeenCalledWith(postResponse.distribution);
    expect(scope.message).toEqual(postResponse.success);
  });
});
