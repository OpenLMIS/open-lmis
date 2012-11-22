describe('Requisition header controllers', function () {

  describe('InitiateRnrController', function () {

    var scope, ctrl, $httpBackend, location,facilities,http;

    beforeEach(module('openlmis.services'));
    beforeEach(inject(function ($rootScope,_$httpBackend_,$controller,$location,$http) {
      scope = $rootScope.$new();
      $httpBackend=_$httpBackend_;
      location=$location;
      http = $http;
      expect(scope.facilities).toBeUndefined();
      facilities = [{"code":"10134","name":"National Warehouse","description":null}];
      $httpBackend.expectGET('/logistics/facilities.json').respond({"facilityList":[{"code":"10134","name":"National Warehouse","description":null}]});
      ctrl = $controller(InitiateRnrController, {$scope:scope, $location:location});
    }));

    it('should make call for facilities', function() {
      $httpBackend.flush();
      expect(scope.facilities).toEqual(facilities);
    });

    it('should load programs for a facility', function() {
      scope.facility="10134";
      var programsForFacility = [{"code":"HIV","name":"HIV","description":"HIV","active":true}];
      $httpBackend.expectGET('/logistics/facility/10134/programs.json').respond({"programList":[{"code":"HIV","name":"HIV","description":"HIV","active":true}]});
      scope.loadPrograms();

      $httpBackend.flush();
      expect(scope.programsForFacility).toEqual(programsForFacility);
    });

    it('should set error message if program not defined', function() {
      scope.getRnrHeader();
      expect(scope.error).toEqual("Please select Facility and program for facility to proceed");
    });


  });

  describe('CreateRnrController',function() {

    var scope, ctrl, $httpBackend, location,requisitionHeader;

    beforeEach(module('openlmis.services'));
    beforeEach(inject(function ($rootScope,_$httpBackend_,$controller,$location) {
      scope = $rootScope.$new();
      $httpBackend=_$httpBackend_;
      location=$location;
      requisitionHeader = {"requisitionHeader":{"facilityName":"National Warehouse",
        "facilityCode":"10134","facilityType":"Warehouse","facilityOperatedBy":"MoH","maximumStockLevel":3,"emergencyOrderPoint":0.5,
        "zone":{"label":"state","value":"Arusha"},"parentZone":{"label":"state","value":"Arusha"}}};
      scope.$parent.facility = "10134";
      scope.$parent.program={code:"programCode"};

      $httpBackend.expectGET('/logistics/facility/10134/requisition-header.json').respond(requisitionHeader);
      $httpBackend.expectGET('/logistics/rnr/programCode/columns.json').respond({"rnrColumnList":{"testField":"test"}});
      ctrl = $controller(CreateRnrController, {$scope:scope, $location:location});
    }));

    it('should get header data', function () {
      $httpBackend.flush();
      expect(scope.header).toEqual({"facilityName":"National Warehouse",
        "facilityCode":"10134","facilityType":"Warehouse","facilityOperatedBy":"MoH","maximumStockLevel":3,"emergencyOrderPoint":0.5,
        "zone":{"label":"state","value":"Arusha"},"parentZone":{"label":"state","value":"Arusha"}});
    });

    it('should get list of Rnr Columns for program', function () {
      $httpBackend.flush();
      expect(scope.programRnRColumnList).toEqual({"testField":"test"});
    });

  });

});