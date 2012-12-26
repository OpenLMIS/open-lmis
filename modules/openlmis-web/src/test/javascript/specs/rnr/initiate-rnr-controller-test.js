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

  it('should initiate rnr if facility and program chosen are correct',function () {
    scope.$parent.program = {"code" : "hiv"};
    scope.$parent.facility = 1;
    scope.initRnr();
    expect(location.path()).toEqual("/create-rnr/1/hiv");
    expect(scope.error).toEqual("");
  });



//    it('should reset program if facility set to null and attempt to load programs is made', function () {
//      scope.$parent.facility = {"code" : "hiv"};
//      $httpBackend.expectPOST('/logistics/rnr/undefined/hiv/init.json').respond(404);
//      scope.getRnrHeader();
//      $httpBackend.flush();
//      expect(scope.error).toEqual("Rnr initialization failed!");
//    });

});