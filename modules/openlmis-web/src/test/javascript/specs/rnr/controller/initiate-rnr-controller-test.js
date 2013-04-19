/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

describe('InitiateRnrController', function () {

  var scope, ctrl, $httpBackend, location, facilities, programs, rootScope;

  beforeEach(module('openlmis.services'));
  beforeEach(inject(function ($rootScope, _$httpBackend_, $controller, $location) {
    scope = $rootScope.$new();
    rootScope = $rootScope;
    rootScope.hasPermission = function () {
      return true;
    };
    $httpBackend = _$httpBackend_;
    location = $location;
    facilities = [
      {"id":"10134", "name":"National Warehouse", "description":null}
    ];
    programs = [
      {"code":"HIV", "id":1}
    ];

    ctrl = $controller(InitiateRnrController, {$scope:scope, $rootScope:rootScope});
  }));

  it('should set error message if program not defined', function () {
    scope.initRnr();
    expect(scope.error).toEqual("Please select Facility, Program and Period to proceed");
  });


  it('should set error message if facility not defined', function () {
    scope.initRnr();
    expect(scope.error).toEqual("Please select Facility, Program and Period to proceed");
  });

  it('should get existing rnr if already initiated', function () {
    scope.selectedProgram = {"code":"hiv", "id":2};
    scope.selectedFacilityId = 1;
    scope.selectedPeriod = {"id":3};
    $httpBackend.expectGET('/facility/1/program/2/rights.json').respond({rights:[{right:'CREATE_REQUISITION'}]});
    $httpBackend.expectGET('/requisitions.json?facilityId=1&periodId=3&programId=2').respond({"rnr":{"id":1, status:"INITIATED"}});

    scope.initRnr();
    $httpBackend.flush();

      expect(location.url()).toEqual("/create-rnr/1/1/2?supplyType=full-supply&page=1");
    expect(scope.error).toEqual("");
    expect(scope.$parent.rnr).toEqual({"id":1, status:'INITIATED'});
  });

  it('should give error if user has authorize only access and an rnr is not submitted yet', function () {
    scope.selectedProgram = {"code":"hiv", "id":2};
    scope.selectedFacilityId = 1;
    scope.selectedPeriod = {"id":3};
    spyOn(rootScope, 'hasPermission').andReturn(false);
    $httpBackend.expectGET('/facility/1/program/2/rights.json').respond({rights:[{right:'AUTHORIZE_REQUISITION'}]});
    $httpBackend.expectGET('/requisitions.json?facilityId=1&periodId=3&programId=2').respond({"rnr":{"id":1, status:"INITIATED"}});

    scope.initRnr();
    $httpBackend.flush();

    expect(scope.error).toEqual("Requisition not submitted yet");
  });

  it('should give error if rnr template has not been defined yet and user has create requisition permission', function () {
    scope.selectedProgram = {"code":"hiv", "id":2};
    scope.selectedFacilityId = 1;
    scope.selectedPeriod = {"id":3};
    spyOn(rootScope, 'hasPermission').andReturn(true);
    $httpBackend.expectGET('/facility/1/program/2/rights.json').respond({rights:[{right:'CREATE_REQUISITION'}]});

    $httpBackend.expectGET('/requisitions.json?facilityId=1&periodId=3&programId=2').respond(null);
    $httpBackend.expectPOST('/requisitions.json?facilityId=1&periodId=3&programId=2').respond(400, {"error":"errorMessage"});

    scope.initRnr();
    $httpBackend.flush();

    expect(scope.error).toEqual("errorMessage");
  });

  it('should create a rnr if rnr not already initiated', function () {
    scope.selectedProgram = {"code":"hiv", "id":2};
    scope.selectedFacilityId = 1;
    scope.selectedPeriod = {"id":3};
    $httpBackend.expectGET('/facility/1/program/2/rights.json').respond({rights:[{right:'CREATE_REQUISITION'}]});

    $httpBackend.expectGET('/requisitions.json?facilityId=1&periodId=3&programId=2').respond(null);
    $httpBackend.expectPOST('/requisitions.json?facilityId=1&periodId=3&programId=2').respond({"rnr":{"id":1, status:"INITIATED"}});

    scope.initRnr();
    $httpBackend.flush();

    expect(location.url()).toEqual("/create-rnr/1/1/2?supplyType=full-supply&page=1");
    expect(scope.error).toEqual("");
    expect(scope.$parent.rnr).toEqual({"id":1, status:'INITIATED'});
  });

  it('should not create a rnr if rnr not already initiated and user does not have create requisition permission', function () {
    scope.selectedProgram = {"code":"hiv", "id":2};
    scope.selectedFacilityId = 1;
    scope.selectedPeriod = {"id":3};
    $httpBackend.expectGET('/facility/1/program/2/rights.json').respond({rights:[{right:'AUTHORIZE_REQUISITION'}]});

    $httpBackend.expectGET('/requisitions.json?facilityId=1&periodId=3&programId=2').respond(null);
    spyOn(rootScope, 'hasPermission').andReturn(false);

    scope.initRnr();
    $httpBackend.flush();

    expect(scope.error).toEqual("Requisition not initiated yet");
  });

  it('should set appropriate message for facility', function () {
    scope.facilities = null;
    expect(scope.facilityOptionMessage()).toEqual('--none assigned--');

    scope.facilities = facilities;
    expect(scope.facilityOptionMessage()).toEqual('--choose facility--');
  });

  describe('periods', function () {
    beforeEach(function () {
      scope.selectedProgram = {"code":"hiv", "id":10};
      scope.selectedFacilityId = 20;
    });

    it('should load periods for selected facility and program', function () {
      var periods = [
        {"id":1, "name":"First Month", "description":"First Month Description"},
        {"id":2, "name":"Second Month", "description":"Second Month Description"},
        {"id":3, "name":"Third Month", "description":"Third Month Description"}
      ];
      var rnr = {"id":1, "status":"INITIATED", "period":{"id" : 1}};
      $httpBackend.expectGET('/logistics/facility/20/program/10/periods.json').respond({"periods":periods, "rnr":rnr});

      scope.loadPeriods();
      $httpBackend.flush();

      expect(scope.periodGridData).toEqual([
        {"id":1, "name":"First Month", "description":"First Month Description", "rnrId":1, "rnrStatus":"INITIATED", "activeForRnr":true},
        {"id":2, "name":"Second Month", "description":"Second Month Description", "rnrStatus":"Previous R&R pending"},
        {"id":3, "name":"Third Month", "description":"Third Month Description", "rnrStatus":"Previous R&R pending"}
      ]);
      expect(scope.selectedPeriod).toEqual(periods[0]);
      expect(scope.error).toEqual('');
    });

    it('should display appropriate message if no periods found for selected facility and program', function () {
      $httpBackend.expectGET('/logistics/facility/20/program/10/periods.json').respond({"periods":[], "rnr":undefined});

      scope.loadPeriods();
      $httpBackend.flush();

      expect(scope.periodGridData).toEqual([
        {"name":"No period(s) available"}
      ]);
      expect(scope.selectedPeriod).toEqual(null);
      expect(scope.error).toEqual('');
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
      scope.selectedProgram = {"id":1};

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
  });
});