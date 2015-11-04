/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe('ViewRnrListController', function () {
  var scope, httpBackend, controller, facilities, rnrList, location, messageService, navigateBackService;

  beforeEach(module('openlmis'));
  beforeEach(inject(function ($httpBackend, $rootScope, $controller, $location, _messageService_, _navigateBackService_) {
    location = $location;
    scope = $rootScope.$new();
    httpBackend = $httpBackend;
    controller = $controller;
    messageService = _messageService_;
    navigateBackService = _navigateBackService_;
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
    scope.startDate = "01-01-2001";
    scope.endDate = "02-01-2001";
    scope.selectedProgramId = 1;
    var expectedUrl = '/requisitions.json?dateRangeEnd=02-01-2001&dateRangeStart=01-01-2001&facilityId=1&programId=1';
    loadRequisitions(expectedUrl, rnrList);
  });

  it('should get requisitions without program id if all', function () {
    scope.viewRequisitionForm = {$invalid: false};
    scope.selectedFacilityId = 1;
    scope.startDate = "01-01-2001";
    scope.endDate = "02-01-2001";
    var urlWithoutProgramId = '/requisitions.json?dateRangeEnd=02-01-2001&dateRangeStart=01-01-2001&facilityId=1';
    loadRequisitions(urlWithoutProgramId, rnrList);
  });


  it('should get requisitions with program id if selected, set requisitions and filteredRequisitions', function () {
    scope.viewRequisitionForm = {$invalid: false};
    scope.selectedFacilityId = 1;
    scope.startDate = "01-01-2001";
    scope.endDate = "02-01-2001";
    scope.selectedProgramId = 1;
    var expectedUrl = '/requisitions.json?dateRangeEnd=02-01-2001&dateRangeStart=01-01-2001&facilityId=1&programId=1';
    loadRequisitions(expectedUrl, rnrList);
    scope.requisitions = rnrList;
    scope.filteredRequisitions = rnrList;
    scope.requisitionFoundMessage = "No R&Rs found";
  });

  it('should get requisitions with program id if selected, set requisitions and filteredRequisitions', function () {
    scope.viewRequisitionForm = {$invalid: false};
    scope.selectedFacilityId = 1;
    scope.startDate = "01-01-2001";
    scope.endDate = "02-01-2001";
    scope.selectedProgramId = 1;
    var expectedUrl = '/requisitions.json?dateRangeEnd=02-01-2001&dateRangeStart=01-01-2001&facilityId=1&programId=1';
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
      {'requisitionStatus': "abcd"}
    ];
    scope.query = "first";

    scope.filterRequisitions();

    expect(scope.filteredRequisitions.length).toEqual(0);
  });

  it('should filter requisitions against program name and return results', function () {
    scope.requisitions = [
      {'requisitionStatus': "first requisition"},
      {'requisitionStatus': "abcd"}
    ];
    scope.query = "first";

    scope.filterRequisitions();

    expect(scope.filteredRequisitions.length).toEqual(1);
    expect(scope.filteredRequisitions[0]).toEqual(scope.requisitions[0]);
  });

  it('should set end date offset based on start date', function () {
    scope.startDate = "03-01-2012";

    spyOn(Date, 'now').andCallFake(function () {
      return new Date(2012, 0, 1, 0, 0).getTime();
    });

    scope.setEndDateOffset();

    expect(scope.endDateOffset).toBeGreaterThan(0);
  });


  it('should clear end date if less than start date', function () {
    scope.startDate = "03-01-2012";

    spyOn(Date, 'now').andCallFake(function () {
      return new Date(2012, 0, 1, 0, 0).getTime();
    });

    scope.endDate = "02-01-2012";

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
    //scope.openRequisition();
    openRequisition("/reference-data/toggle/new.rnr.view.json", {key:false});
    expect(location.url()).toEqual("/requisition/1/2?supplyType=fullSupply&page=1");
  });

  it('should open a via requisition view', function () {
    scope.selectedItems = [
      {'id': 1, 'programId': 2,'programCode':'ESS_MEDS'}
    ];
    openRequisition("/reference-data/toggle/new.rnr.view.json", {key:true});
    expect(location.url()).toEqual("/view-requisition-via/1/2?supplyType=fullSupply&page=1");
  });

  it('should open a mmia requisition view', function () {
    scope.selectedItems = [
      {'id': 1, 'programId': 2,'programCode':'MMIA'}
    ];
    openRequisition("/reference-data/toggle/new.rnr.view.json", {key:true});
    expect(location.url()).toEqual("/view-requisition-mmia/1/2?supplyType=fullSupply&page=1");
  });

  it('should open a requisition with id 1 and for program 2 and full-supply and set data in navigateBackService', function () {
    scope.selectedItems = [
      {'id': 1, 'programId': 2}
    ];

    scope.selectedFacilityId = 1;
    scope.startDate = "10/10/2004";
    scope.endDate = "10/10/2014";
    scope.programs = [
      {id: 1}
    ];
    scope.selectedProgramId = 2;
    //scope.openRequisition();
    openRequisition("/reference-data/toggle/new.rnr.view.json", {key:false});
    expect(location.url()).toEqual("/requisition/1/2?supplyType=fullSupply&page=1");
    expect(navigateBackService.facilityId).toEqual(1);
    expect(navigateBackService.dateRangeStart).toEqual("10/10/2004");
    expect(navigateBackService.dateRangeEnd).toEqual("10/10/2014");
    expect(navigateBackService.programId).toEqual(2);
    expect(navigateBackService.programs).toEqual([
      {id: 1}
    ]);
  });

  it('should set data in scope from previous search', function () {
    var data = {
      facilityId: 1,
      dateRangeStart: "10/10/2004",
      dateRangeEnd: "10/10/2014",
      programId: 2,
      programs: [
        {id: 1}
      ]
    };
    navigateBackService.setData(data);
    controller(ViewRnrListController, {$scope: scope, facilities: facilities, $location: location});
    expect(scope.selectedFacilityId).toEqual(1);
    expect(scope.startDate).toEqual("10/10/2004");
    expect(scope.endDate).toEqual("10/10/2014");
    expect(scope.programs).toEqual([
      {id: 1}
    ]);
    expect(scope.selectedProgramId).toEqual(2);
  });

  function  loadRequisitions(expectedUrl, respondWith) {
    httpBackend.expect('GET', expectedUrl).respond(200, respondWith);
    scope.loadRequisitions();
    httpBackend.flush();
  }

  function  openRequisition(expectedUrl, respondWith) {
    httpBackend.expect('GET', expectedUrl).respond(200, respondWith);
    scope.openRequisition();
    httpBackend.flush();
  }

});