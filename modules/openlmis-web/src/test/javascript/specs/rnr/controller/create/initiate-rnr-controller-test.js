/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe('InitiateRnrController', function () {

  var scope, ctrl, $httpBackend, location, facilities, programs, rootScope, messageService, navigateBackService;

  beforeEach(module('openlmis'));
  beforeEach(inject(function ($rootScope, _$httpBackend_, $controller,
                              $location, _messageService_,
                              _navigateBackService_) {
    scope = $rootScope.$new();
    rootScope = $rootScope;
    rootScope.hasPermission = function () {
      return true;
    };

    $httpBackend = _$httpBackend_;
    location = $location;
    messageService = _messageService_;
    navigateBackService = _navigateBackService_;
    facilities = [
      {"id": "10134", "name": "National Warehouse", "description": null}
    ];
    programs = [
      {"code": "HIV", "id": 1}
    ];

    ctrl = $controller(InitiateRnrController, {$scope: scope, $rootScope: rootScope});
  }));

  it("should not reset rnr data when navigated back from create rnr form", function () {
    var testData = {selectedType: "2", selectedProgram: {"code": "HIV", "id": 1}, selectedFacilityId: 1, isNavigatedBack: true};
    navigateBackService.setData(testData);
    spyOn(scope, 'loadFacilitiesForProgram');
    scope.$broadcast('$viewContentLoaded');
    expect(scope.selectedProgram).not.toEqual(null);
    expect(scope.selectedFacilityId).not.toEqual(null);
    expect(scope.loadFacilitiesForProgram).toHaveBeenCalled();
  });

  it("should reset rnr data when selectedType is changed", function () {
    var testData = {selectedType: "2", selectedProgram: {"code": "HIV", "id": 1}, selectedFacilityId: 1, isNavigatedBack: false};
    navigateBackService.setData(testData);
    scope.$broadcast('$viewContentLoaded');
    expect(scope.selectedProgram).toEqual(null);
    expect(scope.selectedFacilityId).toEqual(null);
  });

  it("should retain all the previous values when navigated back from create rnr screen", function () {
    var testData = {selectedType: "2", selectedProgram: {"code": "HIV", "id": 1}, selectedFacilityId: 1, isNavigatedBack: true};
    navigateBackService.setData(testData);
    scope.programs = programs;
    spyOn(scope, 'loadFacilitiesForProgram');
    spyOn(scope, 'loadFacilityData');
    spyOn(scope, 'loadPeriods');
    scope.$broadcast('$viewContentLoaded');
    scope.$digest();
    expect(scope.selectedType).toEqual(testData.selectedType);
    expect(scope.selectedProgram).toEqual(testData.selectedProgram);
    expect(scope.selectedFacilityId).toEqual(testData.selectedFacilityId);
    expect(scope.loadFacilityData).toHaveBeenCalledWith(scope.selectedType);
    expect(scope.loadFacilitiesForProgram).toHaveBeenCalled();
    expect(scope.loadPeriods).toHaveBeenCalled();
  });

  it("should return facilities if selected type is 0 and facility list is not empty", function () {
    var selectedType = "0";
    var UserFacilityListResponse = {"facilityList": [
      {"id": 1, "code": "F11", "name": "lokesh"}
    ]};
    var RequisitionResponse = {"programList": [
      {"code": "HIV", "id": 1}
    ]};

    $httpBackend.when('GET', '/user/facilities.json').respond(UserFacilityListResponse);
    $httpBackend.when('GET', '/create/requisition/programs.json?facilityId=1').respond(RequisitionResponse);
    scope.loadFacilityData(selectedType);
    $httpBackend.flush();
    expect(scope.facilityDisplayName).toEqual('F11-lokesh');
    expect(scope.selectedFacilityId).toEqual(1);
    expect(scope.programs).toEqual([
      {"code": "HIV", "id": 1}
    ]);
  });

  it("should not return facilities if selected type is 0 and facility list is empty", function () {
    var selectedType = "0";
    var UserFacilityListResponse = {"facilityList": []};

    spyOn(messageService, 'get').andCallFake(function (arg) {
      return "--none assigned--";
    });
    $httpBackend.when('GET', '/user/facilities.json').respond(UserFacilityListResponse);
    scope.loadFacilityData(selectedType);
    $httpBackend.flush();
    expect(scope.facilityDisplayName).toEqual('--none assigned--');
    expect(scope.program).toEqual(null);
    expect(scope.selectedProgram).toEqual(null);
  });

  it("should return program list if selected type is 1", function () {
    var selectedType = "1";
    var RequisitionResponse = {"programList": [
      {"code": "HIV", "id": 1}
    ]};

    $httpBackend.when('GET', '/create/requisition/programs.json').respond(RequisitionResponse);
    scope.loadFacilityData(selectedType);
    $httpBackend.flush();
    expect(scope.programs).toEqual([
      {"code": "HIV", "id": 1}
    ]);
  });

  it('should get existing rnr if already initiated', function () {
    scope.selectedProgram = {"code": "hiv", "id": 2};
    scope.selectedFacilityId = 1;
    $httpBackend.expectGET('/facility/1/program/2/rights.json').respond({rights: [
      {name: 'CREATE_REQUISITION'}
    ]});
    $httpBackend.expectGET('/requisitions/1.json').respond({"rnr": {"id": 1, status: "INITIATED"}});

    scope.initRnr({"id": 3, rnrId: 1});
    $httpBackend.flush();

    expect(location.url()).toEqual("/create-rnr/1/1/2?supplyType=fullSupply&page=1");
    expect(scope.error).toEqual("");
    expect(scope.$parent.rnrData.rnr).toEqual({"id": 1, status: 'INITIATED'});
  });

  it('should give error if user has authorize only access and an rnr is not submitted yet', function () {
    scope.selectedProgram = {"code": "hiv", "id": 2};
    scope.selectedFacilityId = 1;
    spyOn(rootScope, 'hasPermission').andReturn(false);
    $httpBackend.expectGET('/facility/1/program/2/rights.json').respond({rights: [
      {name: 'AUTHORIZE_REQUISITION'}
    ]});
    $httpBackend.expectGET('/requisitions/1.json').respond({"rnr": {"id": 1, status: "INITIATED"}});

    spyOn(messageService, 'get').andCallFake(function (arg) {
      return "Requisition not submitted yet";
    });

    scope.initRnr({"id": 3, rnrId: 1});
    $httpBackend.flush();

    expect(messageService.get).toHaveBeenCalledWith('error.requisition.not.submitted');
    expect(scope.error).toEqual("Requisition not submitted yet");
  });

  it('should give error if rnr template has not been defined yet and user has create requisition permission', function () {
    scope.selectedProgram = {"code": "hiv", "id": 2};
    scope.selectedFacilityId = 1;
    spyOn(rootScope, 'hasPermission').andReturn(true);
    $httpBackend.expectGET('/facility/1/program/2/rights.json').respond({rights: [
      {name: 'CREATE_REQUISITION'}
    ]});

    $httpBackend.expectPOST('/requisitions.json?emergency=false&facilityId=1&periodId=3&programId=2').respond(400, {"error": "errorMessage"});

    scope.initRnr({"id": 3});
    $httpBackend.flush();

    expect(scope.error).toEqual("errorMessage");
  });

  it('should create a rnr if rnr not already initiated', function () {
    scope.selectedProgram = {"code": "hiv", "id": 2};
    scope.selectedFacilityId = 1;
    $httpBackend.expectGET('/facility/1/program/2/rights.json').respond({rights: [
      {name: 'CREATE_REQUISITION'}
    ]});

    $httpBackend.expectPOST('/requisitions.json?emergency=false&facilityId=1&periodId=3&programId=2').respond({"rnr": {"id": 1, status: "INITIATED"}});

    scope.initRnr({"id": 3});
    $httpBackend.flush();

    expect(location.url()).toEqual("/create-rnr/1/1/2?supplyType=fullSupply&page=1");
    expect(scope.error).toEqual("");
    expect(scope.$parent.rnrData.rnr).toEqual({"id": 1, status: 'INITIATED'});
  });

  it('should not create a rnr if rnr not already initiated and user does not have create requisition permission', function () {
    scope.selectedProgram = {"code": "hiv", "id": 2};
    scope.selectedFacilityId = 1;
    $httpBackend.expectGET('/facility/1/program/2/rights.json').respond({rights: [
      {name: 'AUTHORIZE_REQUISITION'}
    ]});

    spyOn(rootScope, 'hasPermission').andReturn(false);

    spyOn(messageService, 'get').andCallFake(function (arg) {
      return "Requisition not initiated yet";
    });

    scope.initRnr({"id": 3});
    $httpBackend.flush();
    expect(messageService.get).toHaveBeenCalledWith('error.requisition.not.initiated');
    expect(scope.error).toEqual("Requisition not initiated yet");
  });

  it('should save the selected data in the navigateBackService', function () {
    scope.selectedType = "0";
    scope.selectedProgram = {"code": "hiv", "id": 2};
    scope.selectedFacilityId = 1;
    var testData = {selectedType: scope.selectedType, selectedProgram: scope.selectedProgram, selectedFacilityId: scope.selectedFacilityId, isNavigatedBack: true};
    spyOn(navigateBackService, 'setData');
    $httpBackend.expectGET('/facility/1/program/2/rights.json').respond({rights: [
      {name: 'CREATE_REQUISITION'}
    ]});
    $httpBackend.expectGET('/requisitions/1.json').respond({"rnr": {"id": 1, status: "INITIATED"}});
    scope.initRnr({"id": 3, rnrId: 1});
    $httpBackend.flush();
    expect(navigateBackService.setData).toHaveBeenCalledWith(testData);
  });

  it('should set appropriate message for facility', function () {
    scope.facilities = null;
    spyOn(messageService, 'get').andCallFake(function (arg) {
      if (arg == 'label.none.assigned') {
        return "--None Assigned--";
      }
      else {
        return "--Select Facility--";
      }
    });
    expect(scope.facilityOptionMessage()).toEqual('--None Assigned--');
    expect(messageService.get).toHaveBeenCalledWith('label.none.assigned');

    scope.facilities = facilities;
    expect(scope.facilityOptionMessage()).toEqual('--Select Facility--');
    expect(messageService.get).toHaveBeenCalledWith('label.select.facility');
  });

  describe('periods', function () {
    beforeEach(function () {
      scope.selectedProgram = {"code": "hiv", "id": 10};
      scope.selectedFacilityId = 20;
    });

    it('should load periods for selected facility and program for regular rnr', function () {
      var periods = [
        {"id": 1, "name": "First Month", "description": "First Month Description"},
        {"id": 2, "name": "Second Month", "description": "Second Month Description"},
        {"id": 3, "name": "Third Month", "description": "Third Month Description"}
      ];
      var rnr = {"id": 1, "status": "INITIATED", "period": {"id": 1}};
      $httpBackend.expectGET('/logistics/periods.json?emergency=false&facilityId=20&programId=10').respond({"periods": periods, "rnr_list": [rnr]});

      spyOn(messageService, 'get').andCallFake(function (arg) {
        return "Previous R&R pending";
      });

      scope.loadPeriods();
      $httpBackend.flush();

      expect(scope.periodGridData).toEqual([
        {"id": 1, "name": "First Month", "description": "First Month Description", "rnrId": 1, "rnrStatus": "INITIATED", "activeForRnr": true},
        {"id": 2, "name": "Second Month", "description": "Second Month Description", "rnrStatus": "Previous R&R pending"},
        {"id": 3, "name": "Third Month", "description": "Third Month Description", "rnrStatus": "Previous R&R pending"}
      ]);

      expect(messageService.get).toHaveBeenCalledWith('msg.rnr.previous.pending');
      expect(scope.error).toEqual('');
    });

    it('should load periods for selected facility and program for emergency rnr', function () {
      scope.selectedRnrType = {"name": "Emergency", "emergency": true};
      var periods = [
        {"id": 1, "name": "First Month", "description": "First Month Description"}
      ];
      var rnr = {"id": 1, "status": "INITIATED", "period": {"id": 1}};
      $httpBackend.expectGET('/logistics/periods.json?emergency=true&facilityId=20&programId=10').respond({"periods": periods, "rnr_list": [rnr]});

      spyOn(messageService, 'get').andCallFake(function (arg) {
        return "Previous R&R pending";
      });

      scope.loadPeriods();
      $httpBackend.flush();

      expect(scope.periodGridData).toEqual([
        {"id": 1, "name": "First Month", "description": "First Month Description", "rnrId": 1, "rnrStatus": "INITIATED", "activeForRnr": true}
      ]);

      expect(messageService.get).toHaveBeenCalledWith('msg.rnr.previous.pending');
      expect(scope.error).toEqual('');
    });

    it('should display appropriate message if no periods found for selected facility and program', function () {
      $httpBackend.expectGET('/logistics/periods.json?emergency=false&facilityId=20&programId=10').respond({"periods": [], "rnr": undefined});

      spyOn(messageService, 'get').andCallFake(function (arg) {
        return "No period(s) available";
      });
      scope.loadPeriods();
      $httpBackend.flush();

      expect(scope.error).toEqual("No period(s) available");
      expect(messageService.get).toHaveBeenCalledWith('msg.no.period.available');
      expect(scope.selectedPeriod).toEqual(null);
    });

    it('should not load periods if facility selected but program not selected', function () {
      scope.selectedFacilityId = 1;
      scope.selectedProgram = null;

      scope.loadPeriods();

      expect(scope.periodGridData).toEqual([]);
      expect(scope.selectedPeriod).toEqual(null);
    });

    it('should not load periods if program selected but facility not selected', function () {
      scope.selectedFacilityId = null;
      scope.selectedProgram = {"id": 1};

      scope.loadPeriods();

      expect(scope.periodGridData).toEqual([]);
      expect(scope.selectedPeriod).toEqual(null);
    });

    it('should not load periods if both program and facility not selected', function () {
      scope.selectedFacilityId = null;
      scope.selectedProgram = null;

      scope.loadPeriods();

      expect(scope.periodGridData).toEqual([]);
      expect(scope.selectedPeriod).toEqual(null);
    });

    it('should not load periods and set already submitted error if selected type is my facility', function () {
      scope.selectedType = "0";



      $httpBackend.expectGET('/logistics/periods.json?emergency=false&facilityId=20&programId=10').respond(400, {"error": "error.current.rnr.already.post.submit"});

      scope.loadPeriods();
      $httpBackend.flush();

      expect(scope.error).toEqual("msg.rnr.current.period.already.submitted");
    });

    it('should not load periods and set already submitted error if selected type is my supervised facility', function () {
      scope.selectedType = "1";

      $httpBackend.expectGET('/logistics/periods.json?emergency=false&facilityId=20&programId=10').respond(400, {"error": "error.current.rnr.already.post.submit"});

      scope.loadPeriods();
      $httpBackend.flush();

      expect(scope.error).toEqual("msg.no.rnr.awaiting.authorization");
    });

  });
});