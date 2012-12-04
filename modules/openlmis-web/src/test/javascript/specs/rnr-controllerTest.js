describe('Requisition controllers', function () {

  describe('InitiateRnrController', function () {

    var scope, ctrl, $httpBackend, location,facilities;

    beforeEach(module('openlmis.services'));
    beforeEach(inject(function ($rootScope,_$httpBackend_,$controller,$location,$http) {
      scope = $rootScope.$new();
      $httpBackend=_$httpBackend_;
      location=$location;
      expect(scope.facilities).toBeUndefined();
      facilities = [{"code":"10134","name":"National Warehouse","description":null}];
      $httpBackend.expectGET('/logistics/user/facilities.json').respond({"facilityList":[{"code":"10134","name":"National Warehouse","description":null}]});
      ctrl = $controller(InitiateRnrController, {$scope:scope, $location:location});
    }));

    it('should make call for facilities', function() {
      $httpBackend.flush();
      expect(scope.facilities).toEqual(facilities);
    });

    it('should load user supported programs for selected facility for create R&R', function() {
      scope.$parent.facility="10134";
      var programsForFacility = [{"code":"HIV","name":"HIV","description":"HIV","active":true}];
      $httpBackend.expectGET('/logistics/facility/10134/user/programs.json').respond({"programList":[{"code":"HIV","name":"HIV","description":"HIV","active":true}]});
      scope.loadPrograms();

      $httpBackend.flush();
      expect(scope.programsForFacility).toEqual(programsForFacility);
    });

    it('should set error message if program not defined', function() {
      scope.getRnrHeader();
      expect(scope.error).toEqual("Please select Facility and program for facility to proceed");
    });

    it('should initiate rnr if facility and program chosen are correct',function () {
      scope.$parent.program = {"code" : "hiv"};
      $httpBackend.expectPOST('/logistics/rnr/undefined/hiv/init.json').respond({"rnr":{"test":"test"}});
      scope.getRnrHeader();
      $httpBackend.flush();
      expect(scope.$parent.rnr).toEqual({"test":"test"});
      expect(location.path()).toEqual("/create-rnr");
      expect(scope.error).toEqual("");
    });

    it('should set error message if post fails for initiate rnr', function () {
      scope.$parent.program = {"code" : "hiv"};
      $httpBackend.expectPOST('/logistics/rnr/undefined/hiv/init.json').respond(404);
      scope.getRnrHeader();
      $httpBackend.flush();
      expect(scope.error).toEqual("Rnr initialization failed!");
    });


    it('should save work in progress for rnr', function(){
        scope.$parent.rnr = {"id":"rnrId"};
        $httpBackend.expectPOST('/logistics/rnr/rnrId/save.json').respond(200);
        scope.saveRnr();
        $httpBackend.flush();
        expect(scope.message).toEqual("Rnr saved successfully!");
    });

//    it('should reset program if facility set to null and attempt to load programs is made', function () {
//      scope.$parent.facility = {"code" : "hiv"};
//      $httpBackend.expectPOST('/logistics/rnr/undefined/hiv/init.json').respond(404);
//      scope.getRnrHeader();
//      $httpBackend.flush();
//      expect(scope.error).toEqual("Rnr initialization failed!");
//    });

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
      $httpBackend.expectGET('/logistics/rnr/programCode/columns.json').respond({"rnrColumnList":[{"testField":"test"}]});
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
      expect(scope.programRnRColumnList).toEqual([{"testField":"test"}]);
    });

  });

});