describe('InitiateRnrController', function () {

  var scope, ctrl, $httpBackend, location, facilities, programs, rootScope;

  beforeEach(module('openlmis.services'));
  beforeEach(inject(function ($rootScope, _$httpBackend_, $controller, $location, $http) {
    scope = $rootScope.$new();
    rootScope = $rootScope;
    rootScope.hasPermission = function (permission) {
      return true;
    };
    $httpBackend = _$httpBackend_;
    location = $location;
    facilities = [
      {"code": "10134", "name": "National Warehouse", "description": null}
    ];
    programs = [
      {"code": "HIV", "id": 1}
    ];

    $httpBackend.expectGET('/logistics/user/facilities.json').respond(200, {"facilityList": facilities});
    $httpBackend.expectGET('/create/requisition/supervised/programs.json').respond(200, {"programList": programs});
    ctrl = $controller(InitiateRnrController, {$scope: scope, facilities: facilities, programs: programs, $rootScope: rootScope});
  }));

  it('should set facilities and programs in scope', function () {
    $httpBackend.flush();
    expect(scope.facilities).toEqual(facilities);
    expect(scope.programs).toEqual(programs);
  });

  it('should load user supported programs for selected facility for create R&R', function () {
    scope.$parent.facility = "10134";
    var programsForFacility = [
      {"code": "HIV", "name": "HIV", "description": "HIV", "active": true}
    ];
    $httpBackend.expectGET('/logistics/facility/10134/user/programs.json').respond({"programList": [
      {"code": "HIV", "name": "HIV", "description": "HIV", "active": true}
    ]});
    scope.loadPrograms();

    $httpBackend.flush();
    expect(scope.programs).toEqual(programsForFacility);
  });

  it('should set error message if program not defined', function () {
    scope.initRnr();
    expect(scope.error).toEqual("Please select Facility and program for facility to proceed");
  });

  it('should get existing rnr if already initiated', function () {
    scope.$parent.program = {"code": "hiv", "id": 1};
    scope.$parent.facility = 1;
    $httpBackend.expectGET('/requisitions.json?facilityId=1&programId=1').respond({"rnr": {"id": 1, status: "INITIATED"}});
    scope.initRnr();
    $httpBackend.flush();
    expect(location.path()).toEqual("/create-rnr/1/1");
    expect(scope.error).toEqual("");
    expect(scope.$parent.rnr).toEqual({"id": 1, status: 'INITIATED'});
  });
  it('should give error if user has authorize only access and an rnr is not submitted yet', function () {
    scope.$parent.program = {"code": "hiv", "id": 1};
    scope.$parent.facility = 1;
    spyOn(rootScope, 'hasPermission').andReturn(false);
    $httpBackend.expectGET('/requisitions.json?facilityId=1&programId=1').respond({"rnr": {"id": 1, status: "INITIATED"}});
    scope.initRnr();
    $httpBackend.flush();
    expect(scope.error).toEqual("An R&R has not been submitted yet");
  });

  it('should create a rnr if rnr not already initiated', function () {
    scope.$parent.program = {"code": "hiv", "id": 1};
    scope.$parent.facility = 1;
    $httpBackend.expectGET('/requisitions.json?facilityId=1&programId=1').respond(null);
    $httpBackend.expectPOST('/requisitions.json?facilityId=1&programId=1').respond({"rnr": {"id": 1, status: "INITIATED"}});

    scope.initRnr();
    $httpBackend.flush();
    expect(location.path()).toEqual("/create-rnr/1/1");
    expect(scope.error).toEqual("");
    expect(scope.$parent.rnr).toEqual({"id": 1, status: 'INITIATED'});
  });
});