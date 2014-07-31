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

  it('should get all facilities in a page depending on search criteria', function () {
    var facilitiesList = [{"code": "F1", "name": "FAC1"},{"code": "F2", "name": "FAC2"}];
    var pagination = {"page": 1, "pageSize": 10, "numberOfPages": 10, "totalRecords": 100};
    var response = {"facilities": facilitiesList, "pagination": pagination};
    scope.query = "F";
    scope.selectedSearchOption = {"value": 'facility'};
    $httpBackend.when('GET', '/facilities.json?columnName=facility&page=1&searchParam=' + scope.query).respond(response);
    scope.loadFacilities(1);
    $httpBackend.flush();

    expect(scope.facilityList).toEqual(facilitiesList);
    expect(scope.pagination).toEqual(pagination);
    expect(scope.currentPage).toEqual(1);
    expect(scope.showResults).toEqual(true);
    expect(scope.totalItems).toEqual(100);
  });

  it('should get all facilities in a page depending on last query', function () {
    var facilitiesList = [{"code": "F1", "name": "FAC1"},{"code": "F2", "name": "FAC2"}];
    var pagination = {"page": 1, "pageSize": 10, "numberOfPages": 10, "totalRecords": 100};
    var response = {"facilities": facilitiesList, "pagination": pagination};
    scope.query = "F";
    var lastQuery = "fac";
    scope.selectedSearchOption = {"value": 'facility'};
    $httpBackend.when('GET', '/facilities.json?columnName=facility&page=1&searchParam=' + lastQuery).respond(response);
    scope.loadFacilities(1,lastQuery);
    $httpBackend.flush();

    expect(scope.facilityList).toEqual(facilitiesList);
    expect(scope.pagination).toEqual(pagination);
    expect(scope.currentPage).toEqual(1);
    expect(scope.showResults).toEqual(true);
    expect(scope.totalItems).toEqual(100);
  });

  it('should return if query is null', function () {
    scope.query = "";
    var httpBackendSpy = spyOn($httpBackend, 'expectGET');

    scope.loadFacilities(1);

    expect(httpBackendSpy).not.toHaveBeenCalled();
  });

  it('should clear search param and result list', function () {
    var facilitiesList = [{"code": "F1", "name": "FAC1"},{"code": "F2", "name": "FAC2"}];
    scope.query = "F";
    scope.totalItems = 100;
    scope.facilityList = facilitiesList;
    scope.showResults = true;

    scope.clearSearch();

    expect(scope.showResults).toEqual(false);
    expect(scope.query).toEqual("");
    expect(scope.totalItems).toEqual(0);
    expect(scope.facilityList).toEqual([]);
  });

  it('should trigger search on enter key', function () {
    var event = {"keyCode": 13};
    var searchSpy = spyOn(scope, 'loadFacilities');

    scope.triggerSearch(event);

    expect(searchSpy).toHaveBeenCalledWith(1);
  });

  it('should set selected search option', function () {
    var searchOption = "search_option";

    scope.selectSearchType(searchOption);

    expect(scope.selectedSearchOption).toEqual(searchOption)
  });

  it('should set query according to navigate back service', function () {
    scope.query = '';
    navigateBackService.query = 'query';

    scope.$emit('$viewContentLoaded');

    expect(scope.query).toEqual("query");
  });

  it('should get results according to specified page', function () {
    scope.currentPage = 5;
    scope.searchedQuery = "fac";
    var searchSpy = spyOn(scope, 'loadFacilities');

    scope.$apply(function () {
      scope.currentPage = 6;
    });

    expect(searchSpy).toHaveBeenCalledWith(6,scope.searchedQuery);
  });

  it("should save query into shared service on clicking edit link",function(){
    spyOn(navigateBackService, 'setData');
    spyOn(location, 'path');
    scope.query = "f1";
    scope.selectedSearchOption = "facility";

    scope.edit(1);

    expect(navigateBackService.setData).toHaveBeenCalledWith({query: "f1", selectedSearchOption: "facility" });
    expect(location.path).toHaveBeenCalledWith('edit/1');
  });
});