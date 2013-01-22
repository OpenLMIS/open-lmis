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


  it('should set error message if program not defined', function () {
    scope.initRnr();
    expect(scope.error).toEqual("Please select Facility, Program and Period to proceed");
  });

  it('should get existing rnr if already initiated', function () {
    scope.selectedProgram = {"code":"hiv", "id":2};
    scope.selectedFacilityId = 1;
    scope.selectedPeriod = {"id":3};
    $httpBackend.expectGET('/requisitions.json?facilityId=1&periodId=3&programId=2').respond({"rnr":{"id":1, status:"INITIATED"}});

    scope.initRnr();
    $httpBackend.flush();

    expect(location.path()).toEqual("/create-rnr/1/2/3");
    expect(scope.error).toEqual("");
    expect(scope.$parent.program).toEqual(scope.selectedProgram);
    expect(scope.$parent.period).toEqual(scope.selectedPeriod);
    expect(scope.$parent.rnr).toEqual({"id":1, status:'INITIATED'});
  });

  it('should give error if user has authorize only access and an rnr is not submitted yet', function () {
    scope.selectedProgram = {"code":"hiv", "id":2};
    scope.selectedFacilityId = 1;
    scope.selectedPeriod = {"id":3};
    spyOn(rootScope, 'hasPermission').andReturn(false);
    $httpBackend.expectGET('/requisitions.json?facilityId=1&periodId=3&programId=2').respond({"rnr":{"id":1, status:"INITIATED"}});

    scope.initRnr();
    $httpBackend.flush();

    expect(scope.error).toEqual("An R&R has not been submitted yet");
  });

  it('should create a rnr if rnr not already initiated', function () {
    scope.selectedProgram = {"code":"hiv", "id":2};
    scope.selectedFacilityId = 1;
    scope.selectedPeriod = {"id":3};
    $httpBackend.expectGET('/requisitions.json?facilityId=1&periodId=3&programId=2').respond(null);
    $httpBackend.expectPOST('/requisitions.json?facilityId=1&periodId=3&programId=2').respond({"rnr":{"id":1, status:"INITIATED"}});

    scope.initRnr();
    $httpBackend.flush();

    expect(location.path()).toEqual("/create-rnr/1/2/3");
    expect(scope.$parent.program).toEqual(scope.selectedProgram);
    expect(scope.$parent.period).toEqual(scope.selectedPeriod);
    expect(scope.error).toEqual("");
    expect(scope.$parent.rnr).toEqual({"id":1, status:'INITIATED'});
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
        {"name":"First Month", "description":"First Month Description"}
      ];
      $httpBackend.expectGET('/logistics/facility/20/program/10/periods.json').respond({"periods":periods});

      scope.loadPeriods();
      $httpBackend.flush();

      expect(scope.periods).toEqual(periods);
      expect(scope.error).toEqual('');
    });

    it('should display error if no periods found for selected facility and program', function () {
      $httpBackend.expectGET('/logistics/facility/20/program/10/periods.json').respond({"periods":[]});

      scope.loadPeriods();
      $httpBackend.flush();

      expect(scope.periods).toEqual([]);
      expect(scope.error).toEqual('No pending R&Rs for the selected facility and program');
    });

    it('should not load periods if facility selected but program not selected', function () {
      scope.selectedFacilityId = 1;
      scope.selectedProgram = null;

      scope.loadPeriods();

      expect(scope.periods).toEqual(null);
      expect(scope.selectedPeriod).toEqual(null);
    });

    it('should not load periods if program selected but facility not selected', function () {
      scope.selectedFacilityId = null;
      scope.selectedProgram = {"id":1};

      scope.loadPeriods();

      expect(scope.periods).toEqual(null);
      expect(scope.selectedPeriod).toEqual(null);
    });

    it('should not load periods if both program and facility not selected', function () {
      scope.selectedFacilityId = null;
      scope.selectedProgram = null;

      scope.loadPeriods();

      expect(scope.periods).toEqual(null);
      expect(scope.selectedPeriod).toEqual(null);
    });

    it('should prepare period display name', function () {
      var periodDisplayName = scope.periodDisplayName({"name":"Period 1", "startDate":1358274600000, "endDate":1367260200000});
      expect(periodDisplayName).toEqual('Period 1 (16/01/2013 - 30/04/2013)');
    });
  });
});