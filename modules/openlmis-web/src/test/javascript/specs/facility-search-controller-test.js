describe("Facility Search Controller", function () {

  var scope,$httpBackend, ctrl;
  beforeEach(module('openlmis.services'));
  beforeEach(inject(function ($rootScope, _$httpBackend_, $controller) {
    scope = $rootScope.$new();
    $httpBackend = _$httpBackend_;
    var facilityResponse = "fac1";
    $httpBackend.expectGET('/admin/facilities.json').respond(facilityResponse)
    ctrl = $controller('FacilitySearchController',{$scope:scope, facilities:facilityResponse});
  }));

  it('should get all facilities',function() {
    expect(scope.facilityList).toEqual("fac1");
  });

});