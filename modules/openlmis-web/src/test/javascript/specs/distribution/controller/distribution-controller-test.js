/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

describe('DistributionController', function () {
  var scope, controller, httpBackend, messageService;

  var mockedIndexedDB = {
    transaction: function () {
    }
  }

  beforeEach(module('openlmis.services'));
  beforeEach(module('openlmis.localStorage'));
  beforeEach(module('IndexedDB'));
  beforeEach(module('distribution'))

  beforeEach(inject(function ($rootScope, $controller, $httpBackend, _messageService_) {
    messageService = _messageService_;
    scope = $rootScope.$new();
    controller = $controller;
    spyOn(mockedIndexedDB, 'transaction');
    httpBackend = $httpBackend;
    var sharedDistribution = {update: function () {
    }, distributionList: [
      {deliveryZone: {id: 1, name: 'zone1'}, program: {id: 1, name: 'program1'}, period: {id: 1, name: 'period1'}},
      {deliveryZone: {id: 2}, program: {id: 2}, period: {id: 2}}
    ]};

    controller(DistributionController, {$scope: scope, deliveryZones: [], IndexedDB: mockedIndexedDB, $dialog: {},
      messageService: messageService, SharedDistributions: sharedDistribution});
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
    scope.distributionList = [
      {deliveryZone: {id: 1, name: 'zone1'}, program: {id: 1, name: 'program1'}, period: {id: 1, name: 'period1'}},
      {deliveryZone: {id: 2}, program: {id: 2}, period: {id: 2}}
    ];
    scope.selectedZone = {id: 1, name: 'zone1'};
    scope.selectedProgram = {id: 1, name: 'program1'};
    scope.selectedPeriod = {id: 1, name: 'period1'};
    scope.initiateDistribution();
    expect(messageService.get).toHaveBeenCalledWith("message.distribution.already.cached", 'zone1', 'program1', 'period1');
  });

  it('should get reference data for a distribution if distribution not initiated', function () {
    scope.selectedZone = {id: 4, name: 'zone1'};
    scope.selectedProgram = {id: 4, name: 'program1'};
    scope.selectedPeriod = {id: 3, name: 'period1'};
    var facilities = [
      {id: 2, name: "F1"}
    ];
    httpBackend.expect('GET', '/deliveryZones/4/programs/4/facilities.json').respond(200, {"facilities": [
      {'id': '23'}
    ]});

    scope.initiateDistribution();

    httpBackend.flush();
    expect(mockedIndexedDB.transaction).toHaveBeenCalled();
  });

  it('should not initiate the distribution already initiated', function () {
    spyOn(OpenLmisDialog, 'newDialog')
    scope.selectedZone = {id: 4, name: 'zone1'};
    scope.selectedProgram = {id: 4, name: 'program1'};
    scope.selectedPeriod = {id: 4, name: 'period1'};

    httpBackend.expect('GET', '/deliveryZones/4/programs/4/facilities.json').respond(200, {"facilities": [
      {'id': '23'}
    ]});

    scope.initiateDistribution();

    httpBackend.flush();
    expect(mockedIndexedDB.transaction).toHaveBeenCalled();
  });
});
