/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

describe('ViewRnrListController', function () {
  var scope, httpBackend, controller, facilities, rnrList, location, messageService;

  beforeEach(module('openlmis.services'));
  beforeEach(module('openlmis.localStorage'));
  beforeEach(inject(function ($httpBackend, $rootScope, $controller, $location, _messageService_) {
    location = $location;
    scope = $rootScope.$new();
    httpBackend = $httpBackend;
    controller = $controller;
    messageService = _messageService_;
    facilities = [
      {"id": 1}
    ];
    rnrList = {'rnr_list': []};
    controller(ViewRnrListController, {$scope: scope, facilities: facilities, $location: location});
  }));

  it('should initialize facilities', function () {
    expect(facilities).toEqual(scope.facilities);
  });

  it('should set facility label', function () {
    spyOn(messageService, 'get').andCallFake(function (arg) {
      return "--None Assigned--";
    });
    controller(ViewRnrListController, {$scope: scope, facilities: []});

    expect(messageService.get).toHaveBeenCalledWith("label.none.assigned");
    expect("--None Assigned--").toEqual(scope.facilityLabel);
  });

  it('should load should raise error and return if if form invalid', function () {
    scope.viewRequisitionForm = {$invalid: true};
    scope.loadRequisitions();
    expect(scope.errorShown).toBeTruthy();
  });

  it('should get requisitions with program id if selected ', function () {
    scope.viewRequisitionForm = {$invalid: false};
    scope.selectedFacilityId = 1;
    scope.startDate = 'startDate';
    scope.endDate = 'endDate';
    scope.selectedProgramId = 1;
    var expectedUrl = '/requisitions-list.json?dateRangeEnd=endDate&dateRangeStart=startDate&facilityId=1&programId=1';
    loadRequisitions(expectedUrl, rnrList);
  });

  it('should get requisitions without program id if all', function () {
    scope.viewRequisitionForm = {$invalid: false};
    scope.selectedFacilityId = 1;
    scope.startDate = 'startDate';
    scope.endDate = 'endDate';
    var urlWithoutProgramId = '/requisitions-list.json?dateRangeEnd=endDate&dateRangeStart=startDate&facilityId=1';
    loadRequisitions(urlWithoutProgramId, rnrList);
  });


  it('should get requisitions with program id if selected, set requisitions and filteredRequisitions', function () {
    scope.viewRequisitionForm = {$invalid: false};
    scope.selectedFacilityId = 1;
    scope.startDate = 'startDate';
    scope.endDate = 'endDate';
    scope.selectedProgramId = 1;
    var expectedUrl = '/requisitions-list.json?dateRangeEnd=endDate&dateRangeStart=startDate&facilityId=1&programId=1';
    loadRequisitions(expectedUrl, rnrList);
    scope.requisitions = rnrList;
    scope.filteredRequisitions = rnrList;
    scope.requisitionFoundMessage = "No R&Rs found";
  });

  it('should get requisitions with program id if selected, set requisitions and filteredRequisitions', function () {
    scope.viewRequisitionForm = {$invalid: false};
    scope.selectedFacilityId = 1;
    scope.startDate = 'startDate';
    scope.endDate = 'endDate';
    scope.selectedProgramId = 1;
    var expectedUrl = '/requisitions-list.json?dateRangeEnd=endDate&dateRangeStart=startDate&facilityId=1&programId=1';
    var rnrList = {'rnr_list': [
      {'id': 1}
    ]};

    loadRequisitions(expectedUrl, rnrList);

    scope.requisitions = rnrList;
    scope.filteredRequisitions = rnrList;
    scope.requisitionFoundMessage = "";
  });

  it('should filter requisitions against program name and return no results if not found', function () {
    scope.requisitions = [
      {'status': "abcd"}
    ];
    scope.query = "first";

    scope.filterRequisitions();

    expect(scope.filteredRequisitions.length).toEqual(0);
  });

  it('should filter requisitions against program name and return results', function () {
    scope.requisitions = [
      {'status': "first requisition"},
      {'status': "abcd"}
    ];
    scope.query = "first";

    scope.filterRequisitions();

    expect(scope.filteredRequisitions.length).toEqual(1);
    expect(scope.filteredRequisitions[0]).toEqual(scope.requisitions[0]);
  });

  it('should set end date offset based on start date', function () {
    scope.startDate = new Date();

    scope.setEndDateOffset();

    expect(scope.endDateOffset).toBeGreaterThan(0);
  });


  it('should clear end date if less than start date', function () {
    scope.startDate = new Date();

    scope.endDate = new Date(scope.startDate);
    scope.endDate.setDate(scope.startDate.getDate() - 1);

    scope.setEndDateOffset();

    expect(scope.endDateOffset).toBeGreaterThan(0);
    expect(scope.endDate).toBeUndefined();
  });

  it('should create grid with filtered requisitions', function () {
    expect('filteredRequisitions').toEqual(scope.rnrListGrid.data);
  });

  it('should open a requisition with id 1 and for program 2 and full-supply', function () {
    scope.selectedItems = [
      {'id': 1, 'programId': 2}
    ];
    scope.openRequisition();
    expect(location.url()).toEqual("/requisition/1/2?supplyType=full-supply&page=1");
  });

  function loadRequisitions(expectedUrl, respondWith) {
    httpBackend.expect('GET', expectedUrl).respond(200, respondWith);
    scope.loadRequisitions();
    httpBackend.flush();
  }
});