/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe("Facility Search Controller", function () {

  var scope, $httpBackend, ctrl, navigateBackService, location;
  beforeEach(module('openlmis'));

  beforeEach(inject(function ($rootScope, _$httpBackend_, $controller, _navigateBackService_, $location) {
    scope = $rootScope.$new();
    $httpBackend = _$httpBackend_;
    navigateBackService = _navigateBackService_;
    location = $location;
    ctrl = $controller('FacilitySearchController', {$scope:scope, $location:location});
  }));

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