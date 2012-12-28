describe('InitiateRnrController', function () {

  var scope, ctrl, $httpBackend, location,facilities, programs;

  beforeEach(module('openlmis.services'));
  beforeEach(inject(function ($rootScope,_$httpBackend_,$controller,$location,$http) {
    scope = $rootScope.$new();
    $httpBackend=_$httpBackend_;
    location=$location;
    expect(scope.facilities).toBeUndefined();
    facilities = [{"code":"10134","name":"National Warehouse","description":null}];
    programs = [{"code":"HIV","id":1}];
    ctrl = $controller(InitiateRnrController, {$scope:scope, facilities: facilities, programs: programs});
  }));

  it('should set facilities in scope', function() {
    expect(scope.facilities).toEqual(facilities);
  });

  it('should load user supported programs for selected facility for create R&R', function() {
    scope.$parent.facility="10134";
    var programsForFacility = [{"code":"HIV","name":"HIV","description":"HIV","active":true}];
    $httpBackend.expectGET('/logistics/facility/10134/user/programs.json').respond({"programList":[{"code":"HIV","name":"HIV","description":"HIV","active":true}]});
    scope.loadPrograms();

    $httpBackend.flush();
    expect(scope.programs).toEqual(programsForFacility);
  });

  it('should set error message if program not defined', function() {
    scope.initRnr();
    expect(scope.error).toEqual("Please select Facility and program for facility to proceed");
  });

  it('should get existing rnr if already initiated',function () {
    scope.$parent.program = {"code" : "hiv", "id":1};
    scope.$parent.facility = 1;
    $httpBackend.expectGET('/logistics/rnr/facility/1/program/1.json').respond({"rnr":{"id":1}});
    scope.initRnr();
    $httpBackend.flush();
    expect(location.path()).toEqual("/create-rnr/1/1");
    expect(scope.error).toEqual("");
    expect(scope.$parent.rnr).toEqual({"id":1});
  });

it('should create a rnr if rnr not already initiated',function () {
    scope.$parent.program = {"code" : "hiv", "id":1};
    scope.$parent.facility = 1;
    $httpBackend.expectGET('/logistics/rnr/facility/1/program/1.json').respond(404);
    $httpBackend.expectPOST('/logistics/rnr/facility/1/program/1.json').respond({"rnr":{"id":1}});
    scope.initRnr();
    $httpBackend.flush();
    expect(location.path()).toEqual("/create-rnr/1/1");
    expect(scope.error).toEqual("");
    expect(scope.$parent.rnr).toEqual({"id":1});
  });
});