describe("Facility Search Controller", function () {

  var scope,$httpBackend, ctrl;
  beforeEach(module('openlmis.services'));
  beforeEach(inject(function ($rootScope, _$httpBackend_, $controller) {
    scope = $rootScope.$new();
    $httpBackend = _$httpBackend_;
    $httpBackend.expectGET('/admin/facilities.json').respond({"facilityList":"fac1"})
    ctrl = $controller('FacilitySearchController',{$scope:scope});
  }));

  it('should get all facilities',function() {
    $httpBackend.flush();
    expect(scope.facilityList).toEqual("fac1");
  });

});