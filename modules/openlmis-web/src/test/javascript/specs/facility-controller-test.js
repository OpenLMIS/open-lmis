describe("Facility", function () {
  beforeEach(module('openlmis.services'));


  describe("Facility Controller", function () {

    var scope, $httpBackend, ctrl, routeParams, facility;

    beforeEach(inject(function ($rootScope, _$httpBackend_, $controller, $routeParams) {
      scope = $rootScope.$new();
      routeParams = $routeParams;
      $httpBackend = _$httpBackend_;
      $httpBackend.expectGET('/admin/facility/reference-data.json').respond({"facilityTypes":[
        {"type":"warehouse"}
      ], "programs":[
        {"code":"programCode"}
      ], "geographicZones":[
        {"zoneId":"testId"}
      ], "facilityOperators":[
        {"operatorCode":"testCode"}
      ]});
      facility = {"facility":{"supportedPrograms":[]}};
      ctrl = $controller(FacilityController, {$scope:scope, $routeParams:routeParams});
      scope.facilityForm = {$error:{ pattern:"" }};
    }));

    it('should set facility reference data', function () {
      $httpBackend.flush();
      expect(scope.facilityTypes).toEqual([
        {"type":"warehouse"}
      ]);
      expect(scope.facilityOperators).toEqual([
        {"operatorCode":"testCode"}
      ]);
      expect(scope.geographicZones).toEqual([
        {"zoneId":"testId"}
      ]);
      expect(scope.programs).toEqual([
        {"code":"programCode"}
      ]);
    });

    it('should give success message if save successful', function () {
      $httpBackend.flush();
      $httpBackend.expectPOST('/admin/facility.json').respond(200, {"success":"Saved successfully"});
      scope.saveFacility();
      $httpBackend.flush();
      expect("Saved successfully").toEqual(scope.message);
      expect("").toEqual(scope.error);
    });

    it('should give error if save failed', function () {
      $httpBackend.flush();
      $httpBackend.expectPOST('/admin/facility.json').respond(404, {"error":"Save failed"});
      scope.saveFacility();
      $httpBackend.flush();
      expect("Save failed").toEqual(scope.error);
      expect("").toEqual(scope.message);
    });

    it('should give field validation error message if form has pattern errors', function () {
      scope.facilityForm.$error.pattern = "{}";
      scope.saveFacility();
      expect("There are some errors in the form. Please resolve them.").toEqual(scope.error);
      expect("").toEqual(scope.message);
      expect("true").toEqual(scope.showError);
    });

    it('should give field validation error message if form has required errors', function () {
      scope.facilityForm.$error.required = "{}";
      scope.saveFacility();
      expect("There are some errors in the form. Please resolve them.").toEqual(scope.error);
      expect("").toEqual(scope.message);
      expect("true").toEqual(scope.showError);
    });

  });

  describe("Facility Edt Controller", function () {

    beforeEach(inject(function ($rootScope, _$httpBackend_, $controller, $routeParams) {
      scope = $rootScope.$new();
      $httpBackend = _$httpBackend_;
      routeParams = $routeParams;
      routeParams.facilityId = "1";
      $httpBackend.expectGET('/admin/facility/reference-data.json').respond({"facilityTypes":[
        {"type":"warehouse"}
      ], "programs":[
        {"code":"programCode"}
      ], "geographicZones":[
        {"zoneId":"testId"}
      ], "facilityOperators":[
        {"operatorCode":"testCode"}
      ]});
      facility = {"id":1, "code":"F1756", "name":"Village Dispensary", "description":"IT department", "gln":"G7645", "mainPhone":"9876234981", "fax":"fax", "address1":"A", "address2":"B", "geographicZone":1, "facilityTypeCode":"warehouse", "catchmentPopulation":333, "latitude":22.1, "longitude":1.2, "altitude":3.3, "operatedBy":"NGO", "coldStorageGrossCapacity":9.9, "coldStorageNetCapacity":6.6, "suppliesOthers":true, "sdp":true, "hasElectricity":true, "online":true, "hasElectronicScc":true, "hasElectronicDar":null, "active":true, "goLiveDate":1352572200000, "goDownDate":-2592106200000, "satellite":true, "satelliteParentCode":null, "comment":"fc", "dataReportable":true, "supportedPrograms":[
        {"code":"ARV", "name":"ARV", "description":"ARV", "active":true},
        {"code":"HIV", "name":"HIV", "description":"HIV", "active":true}
      ], "modifiedBy":null, "modifiedDate":null};
      $httpBackend.expectGET('/admin/facility/1.json').respond({"facility":facility});
      ctrl = $controller(FacilityController, {$scope:scope, $routeParams:routeParams});
      scope.facilityForm = {$error:{ pattern:"" }};

    }));

    it('should get facility if defined', function () {
      $httpBackend.flush();
      expect([]).toEqual(scope.facility.supportedPrograms);
    });

  });

});