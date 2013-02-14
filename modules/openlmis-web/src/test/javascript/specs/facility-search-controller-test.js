describe("Facility Search Controller", function () {

  var scope, $httpBackend, ctrl;
  beforeEach(module('openlmis.services'));

  beforeEach(inject(function ($rootScope, _$httpBackend_, $controller) {
    scope = $rootScope.$new();
    $httpBackend = _$httpBackend_;
    ctrl = $controller('FacilitySearchController', {$scope:scope});
  }));

//  it('should get all facilities',function() {
//    $httpBackend.flush();
//    expect(scope.facilityList).toEqual([{"id":1}]);
//  });

  it("should get filtered facility list when three characters are entered for search", function () {
    scope.query = "lok";
    var facilityResponse = {"facilityList":[
      {"id":1,"code":"F11","name":"lokesh"}
    ]};
    $httpBackend.when('GET', '/facilities.json?searchParam=' +scope.query.substring(0,3)).respond(facilityResponse);
    scope.updateFilteredQueryList();
    $httpBackend.flush();
    expect(scope.facilityList).toEqual([{"id":1,"code":"F11","name":"lokesh"}]);
  });

  it("should filter facilities when more than three characters are entered for search and first three characters of previous query are same as current query",function(){
    scope.previousQuery='lok';
    scope.query = "loke";
    scope.filteredFacilities=[ {"id":1,"code":"F11","name":"lokesh"},
      {"id":2,"code":"F10","name":"lokash"},
      {"id":3,"code":"F12","name":"LOKERE"}];
    scope.facilityList=[ {"id":1,"code":"F11","name":"lokesh"},
      {"id":2,"code":"F10","name":"lokash"},
      {"id":3,"code":"F12","name":"LOKERE"}];
    scope.updateFilteredQueryList();
    expect(scope.facilityList).toEqual([{"id":1,"code":"F11","name":"lokesh"},{"id":3,"code":"F12","name":"LOKERE"}]);
    expect(scope.facilityList.length).toEqual(2);
  })

  it("should filter facilities when more than three characters are entered for search and first three characters of previous query are NOT same as current query",function(){
    scope.previousQuery='abc';
    scope.query = "loke";
    var facilityResponse = {"facilityList":[
      {"id":1,"code":"F11","name":"lokesh"},
      {"id":2,"code":"F10","name":"lokash"}
    ]};
    $httpBackend.when('GET', '/facilities.json?searchParam=' +scope.query.substring(0,3)).respond(facilityResponse);
    scope.updateFilteredQueryList();
    $httpBackend.flush();
    expect(scope.facilityList).toEqual([{"id":1,"code":"F11","name":"lokesh"}]);
    expect(scope.facilityList.length).toEqual(1);
  })

  it("should return filtered facilities when query contains space",function(){
    scope.query="lok ";
    var facilityResponse = {"facilityList":[
      {"id":1,"code":"F11","name":"lokesh"}
    ]};
    $httpBackend.when('GET', '/facilities.json?searchParam=' +scope.query.substring(0,3)).respond(facilityResponse);
    scope.updateFilteredQueryList();
    $httpBackend.flush();
    expect(scope.facilityList).toEqual([{"id":1,"code":"F11","name":"lokesh"}]);
  })

});