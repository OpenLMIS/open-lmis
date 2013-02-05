describe("Facility Search Controller", function () {

  var scope,$httpBackend, ctrl;
  beforeEach(module('openlmis.services'));

  beforeEach(inject(function ($rootScope, _$httpBackend_, $controller) {
    scope = $rootScope.$new();
    $httpBackend = _$httpBackend_;
    var facilityResponse = {"facilityList":[{"id":1}]};
    $httpBackend.when('GET', '/facilities.json').respond(facilityResponse);

    ctrl = $controller('FacilitySearchController',{$scope:scope});
  }));

  it('should get all facilities',function() {
    $httpBackend.flush();
    expect(scope.facilityList).toEqual([{"id":1}]);
  });

});