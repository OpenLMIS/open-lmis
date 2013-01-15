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
      {"id": "10134", "name": "National Warehouse", "description": null}
    ];
    programs = [
      {"code": "HIV", "id": 1}
    ];

    ctrl = $controller(InitiateRnrController, {$scope: scope, $rootScope: rootScope});
  }));

  it('should set error message if program not defined', function () {
    scope.initRnr();
    expect(scope.error).toEqual("Please select Facility and program for facility to proceed");
  });

  it('should get existing rnr if already initiated', function () {
    scope.selectedProgram = {"code": "hiv", "id": 1};
    scope.selectedFacilityId = 1;
    $httpBackend.expectGET('/requisitions.json?facilityId=1&programId=1').respond({"rnr": {"id": 1, status: "INITIATED"}});

    scope.initRnr();
    $httpBackend.flush();

    expect(location.path()).toEqual("/create-rnr/1/1");
    expect(scope.error).toEqual("");
    expect(scope.$parent.program).toEqual(scope.selectedProgram);
    expect(scope.$parent.rnr).toEqual({"id": 1, status: 'INITIATED'});
  });

  it('should give error if user has authorize only access and an rnr is not submitted yet', function () {
    scope.selectedProgram = {"code": "hiv", "id": 1};
    scope.selectedFacilityId = 1;
    spyOn(rootScope, 'hasPermission').andReturn(false);
    $httpBackend.expectGET('/requisitions.json?facilityId=1&programId=1').respond({"rnr": {"id": 1, status: "INITIATED"}});
    scope.initRnr();
    $httpBackend.flush();
    expect(scope.error).toEqual("An R&R has not been submitted yet");
  });

  it('should create a rnr if rnr not already initiated', function () {
    scope.selectedProgram = {"code": "hiv", "id": 1};
    scope.selectedFacilityId = 1;
    $httpBackend.expectGET('/requisitions.json?facilityId=1&programId=1').respond(null);
    $httpBackend.expectPOST('/requisitions.json?facilityId=1&programId=1').respond({"rnr": {"id": 1, status: "INITIATED"}});

    scope.initRnr();
    $httpBackend.flush();
    expect(location.path()).toEqual("/create-rnr/1/1");
    expect(scope.$parent.program).toEqual(scope.selectedProgram);
    expect(scope.error).toEqual("");
    expect(scope.$parent.rnr).toEqual({"id": 1, status: 'INITIATED'});
  });

//  describe('periods', function() {
//    beforeEach(function() {
//      scope.$parent.program = {"code": "hiv", "id": 10};
//      scope.$parent.facility = 20;
//    });
//
//    it('should load periods for selected facility and program', function () {
//      var periods = [
//        {"name": "First Month", "description": "First Month Description"}
//      ];
//      $httpBackend.expectGET('/logistics/facility/20/program/10/periods.json').respond({"periodList": periods});
//
//      expect(scope.periodOptionMsg).toEqual('--none assigned--');
//
//      scope.loadPeriods();
//      $httpBackend.flush();
//
//      expect(scope.periods).toEqual(periods);
//      expect(scope.periodOptionMsg).toEqual('--choose period--');
//    });
//
//    it('should set appropriate message when no periods found for facility and program', function () {
//      $httpBackend.expectGET('/logistics/facility/20/program/10/periods.json').respond(null);
//
//      scope.loadPeriods();
//      $httpBackend.flush();
//
//      expect(scope.periods).toEqual(null);
//      expect(scope.periodOptionMsg).toEqual('--none assigned--');
//    });
//
//    it('should not load periods if program not selected', function () {
//      scope.$parent.program = null;
//
//      scope.loadPeriods();
//
//      expect(scope.periods).toEqual(null);
//      expect(scope.periodOptionMsg).toEqual('--none assigned--');
//    });
//  });
});