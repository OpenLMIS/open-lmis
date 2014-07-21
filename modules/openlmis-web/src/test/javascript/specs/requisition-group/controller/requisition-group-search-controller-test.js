/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

describe("Requisition Group Search Controller", function () {

  var scope, $httpBackend, ctrl, navigateBackService, location;
  beforeEach(module('openlmis'));

  beforeEach(inject(function ($rootScope, _$httpBackend_, $controller, _navigateBackService_, $location) {
    scope = $rootScope.$new();
    $httpBackend = _$httpBackend_;
    scope.query = "Nod";
    navigateBackService = _navigateBackService_;
    navigateBackService.query = '';
    location = $location;
    ctrl = $controller;
    ctrl('RequisitionGroupSearchController', {$scope: scope});
  }));

  it('should get all requisition Groups in a page depending on search criteria', function () {
    var requisitionGroup = {"code": "N1", "name": "Node 1", "supervisoryNode": {"name": "NodeName"}};
    var pagination = {"page": 1, "pageSize": 10, "numberOfPages": 10, "totalRecords": 100};
    var response = {"requisitionGroupList": [requisitionGroup], "pagination": pagination};
    scope.query = "Nod";
    scope.selectedSearchOption.value = 'requisitionGroup';
    $httpBackend.when('GET', '/requisitionGroups.json?columnName=requisitionGroup&page=1&searchParam=' + scope.query).respond(response);
    scope.search(1);
    $httpBackend.flush();

    expect(scope.requisitionGroupList).toEqual([requisitionGroup]);
    expect(scope.pagination).toEqual(pagination);
    expect(scope.currentPage).toEqual(1);
    expect(scope.showResults).toEqual(true);
    expect(scope.totalItems).toEqual(100);
  });

  it('should get all requisition Groups in a page depending on last query', function () {
    var requisitionGroup = {"code": "N1", "name": "Node 1", "supervisoryNode": {"name": "NodeName"}};
    var pagination = {"page": 1, "pageSize": 10, "numberOfPages": 10, "totalRecords": 100};
    var response = {"requisitionGroupList": [requisitionGroup], "pagination": pagination};
    scope.query = "Nod";
    var lastQuery = "Req";
    scope.selectedSearchOption.value = 'requisitionGroup';
    $httpBackend.when('GET', '/requisitionGroups.json?columnName=requisitionGroup&page=1&searchParam=' + lastQuery).respond(response);
    scope.search(1, lastQuery);
    $httpBackend.flush();

    expect(scope.requisitionGroupList).toEqual([requisitionGroup]);
    expect(scope.pagination).toEqual(pagination);
    expect(scope.currentPage).toEqual(1);
    expect(scope.showResults).toEqual(true);
    expect(scope.totalItems).toEqual(100);
  });

  it('should clear search param and result list', function () {
    var requisitionGroup = {"code": "N1", "name": "Node 1"};
    scope.query = "query";
    scope.totalItems = 100;
    scope.requisitionGroupList = [requisitionGroup];
    scope.showResults = true;

    scope.clearSearch();

    expect(scope.showResults).toEqual(false);
    expect(scope.query).toEqual("");
    expect(scope.totalItems).toEqual(0);
    expect(scope.requisitionGroupList).toEqual([]);
  });

  it('should trigger search on enter key', function () {
    var event = {"keyCode": 13};
    var searchSpy = spyOn(scope, 'search');

    scope.triggerSearch(event);

    expect(searchSpy).toHaveBeenCalledWith(1);
  });

  it('should set selected search option', function () {
    var searchOption = "search_option";

    scope.selectSearchType(searchOption)

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
    var searchSpy = spyOn(scope, 'search');
    scope.searchedQuery = "nod";

    scope.$apply(function () {
      scope.currentPage = 6;
    });

    expect(searchSpy).toHaveBeenCalledWith(6, scope.searchedQuery);
  });

  it('should return if query is null', function () {
    scope.query = "";
    var httpBackendSpy = spyOn($httpBackend, 'expectGET');

    scope.search(1);

    expect(httpBackendSpy).not.toHaveBeenCalled();
  });

  it("should save query into shared service on clicking edit link",function(){
    spyOn(navigateBackService, 'setData');
    spyOn(location, 'path');
    scope.query = "r1";
    scope.selectedSearchOption = "req";

    scope.edit(1);

    expect(navigateBackService.setData).toHaveBeenCalledWith({query: "r1", selectedSearchOption: "req" });
    expect(location.path).toHaveBeenCalledWith('edit/1');
  });

});