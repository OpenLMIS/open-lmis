/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

describe("Facility Search Controller", function () {

  var scope, $httpBackend, ctrl, navigateBackService, location;
  beforeEach(module('openlmis.services'));

  beforeEach(inject(function ($rootScope, _$httpBackend_, $controller, _navigateBackService_, $location) {
    scope = $rootScope.$new();
    $httpBackend = _$httpBackend_;
    navigateBackService = _navigateBackService_;
    location = $location;
    ctrl = $controller('FacilitySearchController', {$scope:scope, $location:location});
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
  });

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
  });

  it("should return filtered facilities when query contains space",function(){
    scope.query="lok ";
    var facilityResponse = {"facilityList":[
      {"id":1,"code":"F11","name":"lokesh"}
    ]};
    $httpBackend.when('GET', '/facilities.json?searchParam=' +scope.query.substring(0,3)).respond(facilityResponse);
    scope.updateFilteredQueryList();
    $httpBackend.flush();
    expect(scope.facilityList).toEqual([{"id":1,"code":"F11","name":"lokesh"}]);
  });

  it("should save query into shared service on clicking edit link",function(){
    spyOn(navigateBackService, 'setData');
    spyOn(location, 'path');
    scope.query = "lokesh";
    scope.editFacility(1);
    expect(navigateBackService.setData).toHaveBeenCalledWith({query: "lokesh"});
    expect(location.path).toHaveBeenCalledWith('edit/1');
  });

  it("should retain previous query value and update filtered query list when dom is loaded", function() {
    spyOn(scope,'updateFilteredQueryList');
    var query = "lok";
    navigateBackService.setData({query: query});
    scope.$broadcast('$viewContentLoaded');
    expect(scope.query).toEqual(query);
    expect(scope.updateFilteredQueryList).toHaveBeenCalled();
  });

});