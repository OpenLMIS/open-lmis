/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

describe('DistributionListController', function () {

  var scope, location, q, messageService;

  var sharedDistribution, indexedDB, rootScope;
  var distribution, $httpBackend;

  beforeEach(module('distribution'));
  beforeEach(module('openlmis.services'));
  beforeEach(module('openlmis.localStorage'));

  beforeEach(function () {
    module(function ($provide) {
      $provide.value('IndexedDB', {put: function () {
      }, get: function () {
      }});
    });
    inject(function ($rootScope, $location, $controller, _IndexedDB_, _$httpBackend_, $q, _messageService_) {
      rootScope = $rootScope;
      scope = rootScope.$new();
      indexedDB = _IndexedDB_;
      $httpBackend = _$httpBackend_
      messageService = _messageService_;
      spyOn(messageService, 'get');

      spyOn(indexedDB, 'get');
      spyOn(indexedDB, 'put');
      q = $q;
      location = $location;
      sharedDistribution = {update: function () {
      }};

      var facilityDistributionData = new FacilityDistributionData({'epiUse': {'productGroups': [
        {'id': 3, 'code': 'penta', 'name': 'penta', 'reading': {'stockAtFirstOfMonth': {'notRecorded': true}, 'received': {'notRecorded': true}, 'distributed': {'notRecorded': false}, 'loss': {'notRecorded': true}, 'stockAtEndOfMonth': {'notRecorded': true}, 'expirationDate': {'notRecorded': true}}, '$$hashKey': '02A'}
      ], 'status': 'is-complete'}, 'refrigerators': {'refrigeratorReadings': []}, 'facilityVisit': {'status': 'is-complete', 'observations': '212', 'verifiedBy': {'name': '12', 'title': '12'}, 'confirmedBy': {'title': '1', 'name': '2'}}});

      spyOn(facilityDistributionData, 'computeStatus');

      spyOn(sharedDistribution, 'update');
      distribution = {'id': 1, 'createdBy': 8, 'modifiedBy': 8, 'deliveryZone': {'id': 8, 'code': 'Sul', 'name': 'Sul Province'}, 'program': {'id': 5, 'code': 'VACCINES', 'name': 'VACCINES', 'description': 'VACCINES', 'active': true, 'templateConfigured': false, 'regimenTemplateConfigured': false, 'push': true}, 'period': {'id': 9, 'scheduleId': 2, 'name': 'June2013', 'description': 'June2013', 'startDate': 1370025000000, 'endDate': 1372616999000, 'numberOfMonths': 1}, 'status': 'INITIATED', 'zpp': '8_5_9',
        'facilityDistributionData': {'44': facilityDistributionData,
          '45': new FacilityDistributionData({'epiUse': {'productGroups': [
            {'id': 3, 'code': 'penta', 'name': 'penta', 'reading': {'stockAtFirstOfMonth': {'notRecorded': true}, 'received': {'notRecorded': true}, 'distributed': {'notRecorded': true}, 'loss': {'notRecorded': true}, 'stockAtEndOfMonth': {'notRecorded': true}, 'expirationDate': {'notRecorded': true}}, '$$hashKey': '0B5'}
          ], 'status': 'is-complete'}, 'refrigerators': {'refrigeratorReadings': []}, 'facilityVisit': {'status': 'is-complete', 'observations': 'e', 'verifiedBy': {'name': 'e', 'title': 'e'}, 'confirmedBy': {'name': 'e', 'title': 'e'}}})}};

      sharedDistribution.distributionList = [distribution];
      $controller(DistributionListController, {$scope: scope, $location: location, SharedDistributions: sharedDistribution, messageService: messageService});
    })
  });

  afterEach(function () {
    rootScope.$apply();
  });

  function getSyncFacilitiesFunction() {
    scope.syncDistribution(1);
    var syncFacilitiesFunction = indexedDB.get.calls[0].args[2];
    return syncFacilitiesFunction;
  }

  it('should set distributions in scope', function () {
    expect(scope.sharedDistributions).toBe(sharedDistribution);
  });

  it('should refresh shared distributions on load', function () {
    expect(sharedDistribution.update).toHaveBeenCalled();
  });
  it('should get facilities for distribution', function () {
    scope.syncDistribution(2);

    expect(indexedDB.get).toHaveBeenCalled();
    expect(indexedDB.get.calls[0].args).toEqual(['distributionReferenceData', 2, jasmine.any(Function)]);
  });

  it('should sync facility data if any complete', function () {

    $httpBackend.expect('PUT', '/distributions/1/facilities/45.json', distribution.facilityDistributionData[45]).respond(200);

    var syncFacilitiesFunction = getSyncFacilitiesFunction();
    syncFacilitiesFunction({target: {result: {facilities: [
      {id: 45, code: 'abcd', name: 'xyz'}
    ]}}});

    $httpBackend.flush();

    expect(messageService.get).toHaveBeenCalledWith("message.facility.synced.successfully", "abcd - xyz");
  });

  it('should save synced status for facility data on success', function () {
    $httpBackend.expect('PUT', '/distributions/1/facilities/45.json', distribution.facilityDistributionData[45]).respond(200);

    var syncFacilitiesFunction = getSyncFacilitiesFunction();
    syncFacilitiesFunction({target: {result: {facilities: [
      {id: 45, code: 'abcd', name: 'xyz'}
    ]}}});

    $httpBackend.flush();

    expect(indexedDB.put).toHaveBeenCalledWith('distributions', distribution, null, null, sharedDistribution.update);
  });

  it('should show message if no facility available for sync ', function () {
    distribution = {'id': 1, 'createdBy': 8, 'modifiedBy': 8, 'deliveryZone': {'id': 8, 'code': 'Sul', 'name': 'Sul Province'}, 'program': {'id': 5, 'code': 'VACCINES', 'name': 'VACCINES', 'description': 'VACCINES', 'active': true, 'templateConfigured': false, 'regimenTemplateConfigured': false, 'push': true}, 'period': {'id': 9, 'scheduleId': 2, 'name': 'June2013', 'description': 'June2013', 'startDate': 1370025000000, 'endDate': 1372616999000, 'numberOfMonths': 1}, 'status': 'INITIATED', 'zpp': '8_5_9',
      'facilityDistributionData': {'44': new FacilityDistributionData({status: 'is-synced'}),
        '45': new FacilityDistributionData({status: 'is-synced'})}};

    scope.sharedDistributions.distributionList = [distribution];

    var syncFacilitiesFunction = getSyncFacilitiesFunction();
    syncFacilitiesFunction({target: {result: {facilities: []}}});

    scope.$apply();

    expect(scope.message).toEqual("message.no.facility.synced");
  });

  it('should mark facility data as duplicate if already synced', function () {
    $httpBackend.expect('PUT', '/distributions/1/facilities/45.json', distribution.facilityDistributionData[45]).respond(409);

    var syncFacilitiesFunction = getSyncFacilitiesFunction();
    syncFacilitiesFunction({target: {result: {facilities: [
      {id: 45, code: 'abcd', name: 'xyz'}
    ]}}});

    $httpBackend.flush();

    expect(distribution.facilityDistributionData[45].status).toEqual('is-duplicate');

    scope.$apply();

    expect(scope.message).toEqual("error.facility.data.already.synced");
    expect(indexedDB.put).toHaveBeenCalledWith('distributions', distribution, null, null, sharedDistribution.update);
  });

});
