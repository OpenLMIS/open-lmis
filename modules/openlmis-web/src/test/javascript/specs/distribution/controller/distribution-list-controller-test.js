/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe('DistributionListController', function () {

  var scope, location, q, messageService;

  var sharedDistribution, rootScope, distributionService;
  var distribution, $httpBackend, dialog, element, compile, window;

  beforeEach(module('distribution'));
  beforeEach(module('openlmis.services'));
  beforeEach(module('openlmis.localStorage'));

  beforeEach(function () {

      module(function ($provide) {
        $provide.value('IndexedDB',
          {
            put: function () {
            },
            get: function () {
            },
            delete: function () {
            }
          });
      });

      inject(function ($rootScope, $location, $controller, $compile, _$httpBackend_, $q, _messageService_, _distributionService_, _$dialog_) {

        rootScope = $rootScope;
        scope = rootScope.$new();
        $httpBackend = _$httpBackend_
        messageService = _messageService_;
        dialog = _$dialog_;
        spyOn(OpenLmisDialog, 'newDialog');
        distributionService = _distributionService_;
        spyOn(distributionService, 'deleteDistribution');
        spyOn(distributionService, 'save');
        q = $q;
        location = $location;
        window = {};

        sharedDistribution = {update: function () {
        }};

        var facilityDistribution1 = new FacilityDistribution(
          {facilityId: 44, 'epiUse': {'productGroups': [
            {'id': 3, 'code': 'penta', 'name': 'penta', 'reading': {'stockAtFirstOfMonth': {'notRecorded': true}, 'received': {'notRecorded': true}, 'distributed': {'notRecorded': false},
              'loss': {'notRecorded': true}, 'stockAtEndOfMonth': {'notRecorded': true}, 'expirationDate': {'notRecorded': true}}, '$$hashKey': '02A'}
          ], 'status': 'is-complete'}, 'refrigerators': {'refrigeratorReadings': []},
            'facilityVisit': {id: 1, 'status': 'is-complete', 'observations': '212', 'verifiedBy': {'name': '12', 'title': '12'}, 'confirmedBy': {'title': '1', 'name': '2'}}});

        var facilityDistribution2 = new FacilityDistribution(
          {facilityId: 45, status: 'is-complete', 'epiUse': {'productGroups': [
            {'id': 3, 'code': 'penta', 'name': 'penta', 'reading': {'stockAtFirstOfMonth': {'notRecorded': true}, 'received': {'notRecorded': true}, 'distributed': {'notRecorded': true}, 'loss': {'notRecorded': true}, 'stockAtEndOfMonth': {'notRecorded': true}, 'expirationDate': {'notRecorded': true}}, '$$hashKey': '0B5'}
          ], 'status': 'is-complete'}, 'refrigerators': {'refrigeratorReadings': []},
            'facilityVisit': {id: 1, 'status': 'is-complete', 'observations': 'e', 'verifiedBy': {'name': 'e', 'title': 'e'}, 'confirmedBy': {'name': 'e', 'title': 'e'}}});

        spyOn(facilityDistribution1, 'computeStatus');

        spyOn(sharedDistribution, 'update');

        distribution = {'id': 1, 'createdBy': 8, 'modifiedBy': 8,
          'deliveryZone': {'id': 8, 'code': 'Sul', 'name': 'Sul Province'},
          'program': {'id': 5, 'code': 'VACCINES', 'name': 'VACCINES', 'description': 'VACCINES', 'active': true, 'templateConfigured': false, 'regimenTemplateConfigured': false, 'push': true}, 'period': {'id': 9, 'scheduleId': 2, 'name': 'June2013', 'description': 'June2013', 'startDate': 1370025000000, 'endDate': 1372616999000, 'numberOfMonths': 1}, 'status': {name: 'INITIATED'}, 'zpp': '8_5_9',
          'facilityDistributions': {
            '44': facilityDistribution1,
            '45': facilityDistribution2
          }};

        sharedDistribution.distributionList = [distribution];

        spyOn(messageService, 'get');

        compile = $compile;
        element = angular.element('<div id="progressbar"></div>');
        compile(element)(scope);
        scope.$digest();
        $controller(DistributionListController,
          {$scope: scope, $location: location, SharedDistributions: sharedDistribution, messageService: messageService, $window: window});

        scope.distributionData = distribution;
      })
    }
  );

  afterEach(function () {
    rootScope.$apply();
  });

  function getSyncFacilitiesFunction() {
    scope.syncDistribution(1);
  }

  it('should set distributions in scope', function () {
    expect(scope.sharedDistributions).toBe(sharedDistribution);
  });

  it('should refresh shared distributions on load', function () {
    expect(sharedDistribution.update).toHaveBeenCalled();
  });

  it('should sync facility data if any complete', function () {

    $httpBackend.expect('PUT', '/distributions/1/facilities/45.json', distribution.facilityDistributions[45]).respond(200, {syncStatus: true});

    var syncFacilitiesFunction = getSyncFacilitiesFunction();

    $httpBackend.flush();

    expect(scope.syncResult[DistributionStatus.SYNCED].length).toEqual(1);
  });

  it('should update progress bar on success of a sync', function () {

    expect(scope.progressValue).toBeUndefined();

    $httpBackend.expect('PUT', '/distributions/1/facilities/45.json', distribution.facilityDistributions[45]).respond(200);

    var syncFacilitiesFunction = getSyncFacilitiesFunction();

    $httpBackend.flush();

    expect(scope.progressValue).toEqual(100);
  });

  it('should update all facilities if distribution is completely synced', function () {

    var distribution = {facilityDistributions: {44: {status: DistributionStatus.COMPLETE}, 46: {status: DistributionStatus.COMPLETE}, 45: {status: DistributionStatus.INCOMPLETE}}}

    $httpBackend.when('PUT', '/distributions/1/facilities/44.json', distribution.facilityDistributions[44]).respond(200, {syncStatus: false, distributionStatus: 'INITIATED'});
    $httpBackend.when('PUT', '/distributions/1/facilities/46.json', distribution.facilityDistributions[46]).respond(200, {syncStatus: true, distributionStatus: 'SYNCED'});

    scope.distributionData = distribution;

    var syncFacilitiesFunction = getSyncFacilitiesFunction();

    $httpBackend.flush();

    scope.$apply();

    expect(scope.syncResult[DistributionStatus.SYNCED].length).toEqual(1);
    expect(scope.syncResult[DistributionStatus.DUPLICATE].length).toEqual(2);
  });

  it('should save synced status for facility data on success', function () {
    $httpBackend.expect('PUT', '/distributions/1/facilities/45.json', distribution.facilityDistributions[45]).respond(200);

    var syncFacilitiesFunction = getSyncFacilitiesFunction();

    $httpBackend.flush();

    expect(distributionService.save).toHaveBeenCalledWith(distribution);
  });

  it('should populate synced, already synced, failed to sync facilities ', function () {
    var distribution = {facilityDistributions: {44: {status: DistributionStatus.COMPLETE}, 46: {status: DistributionStatus.COMPLETE}, 45: {status: DistributionStatus.COMPLETE}}}

    $httpBackend.when('PUT', '/distributions/1/facilities/44.json', distribution.facilityDistributions[44]).respond(200, {syncStatus: false});
    $httpBackend.when('PUT', '/distributions/1/facilities/46.json', distribution.facilityDistributions[46]).respond(500);
    $httpBackend.when('PUT', '/distributions/1/facilities/45.json', distribution.facilityDistributions[45]).respond(200, {syncStatus: true});

    scope.distributionData = distribution;

    var syncFacilitiesFunction = getSyncFacilitiesFunction();

    $httpBackend.flush();

    scope.$apply();

    expect(scope.syncResult[DistributionStatus.COMPLETE].length).toEqual(1);
    expect(scope.syncResult[DistributionStatus.SYNCED].length).toEqual(1);
    expect(scope.syncResult[DistributionStatus.DUPLICATE].length).toEqual(1);
  });

  it('should take user to root location if user logged out while syncing ', function () {
    var distribution = {facilityDistributions: {44: {status: DistributionStatus.COMPLETE}}}

    $httpBackend.when('PUT', '/distributions/1/facilities/44.json', distribution.facilityDistributions[44]).respond(401);

    scope.distributionData = distribution;

    var syncFacilitiesFunction = getSyncFacilitiesFunction();

    $httpBackend.flush();

    expect(window.location).toEqual("/");
  });

  it('should show message if no facility available for sync ', function () {
    distribution = {'id': 1, 'createdBy': 8, 'modifiedBy': 8, 'deliveryZone': {'id': 8, 'code': 'Sul', 'name': 'Sul Province'},
      'program': {'id': 5, 'code': 'VACCINES', 'name': 'VACCINES', 'description': 'VACCINES', 'active': true, 'templateConfigured': false, 'regimenTemplateConfigured': false, 'push': true},
      'period': {'id': 9, 'scheduleId': 2, 'name': 'June2013', 'description': 'June2013', 'startDate': 1370025000000, 'endDate': 1372616999000, 'numberOfMonths': 1},
      'status': 'INITIATED', 'zpp': '8_5_9',
      'facilityDistributions': {'44': new FacilityDistribution({status: DistributionStatus.SYNCED, facilityVisit: {id: 1}}),
        '45': new FacilityDistribution({status: DistributionStatus.SYNCED, facilityVisit: {id: 1}})}};

    scope.sharedDistributions.distributionList = [distribution];

    scope.showConfirmDistributionSync(1);

    scope.$apply();

    expect(scope.syncMessage).toEqual("message.no.facility.synced");
  });

  it('should mark facility data as duplicate if already synced', function () {

    $httpBackend.expect('PUT', '/distributions/1/facilities/45.json', distribution.facilityDistributions[45]).respond(200, {syncStatus: false});

    getSyncFacilitiesFunction();

    $httpBackend.flush();

    expect(distribution.facilityDistributions[45].status).toEqual(DistributionStatus.DUPLICATE);

    scope.$apply();

    expect(scope.syncResult[DistributionStatus.DUPLICATE][0]).toEqual(distribution.facilityDistributions[45]);
    expect(distributionService.save).toHaveBeenCalledWith(distribution);
  });

  it('should ask before deleting a distribution', function () {
    scope.deleteDistribution(1);

    expect(OpenLmisDialog.newDialog).toHaveBeenCalledWith(jasmine.any(Object), jasmine.any(Function), dialog);
  });

  it('should delete distribution on click of OK', function () {
    scope.deleteDistribution(1);

    OpenLmisDialog.newDialog.calls[0].args[1](true);

    expect(distributionService.deleteDistribution).toHaveBeenCalledWith(1);
  });

});
