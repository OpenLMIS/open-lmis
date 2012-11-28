describe("Facility Controller", function () {


  var scope, $httpBackend, ctrl;

  beforeEach(module('openlmis.services'));
  beforeEach(inject(function ($rootScope, _$httpBackend_, $controller, $location) {
    scope = $rootScope.$new();
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

    ctrl = $controller(FacilityController, {$scope:scope});
  }));

  it('should make call for facilities', function () {
    $httpBackend.flush();
    expect(scope.facilityTypes).toEqual([{"type":"warehouse"}]);
    expect(scope.facilityOperators).toEqual([{"operatorCode":"testCode"}]);
    expect(scope.geographicZones).toEqual([{"zoneId":"testId"}]);
    expect(scope.programs).toEqual([{"code":"programCode"}]);
  });


});