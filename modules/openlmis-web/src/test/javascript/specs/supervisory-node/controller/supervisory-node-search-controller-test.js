/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe("Supervisory Node Search Controller", function () {

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
    ctrl('SupervisoryNodeSearchController', {$scope: scope});
  }));

  it('should return if query is null', function () {
    scope.query = "";
    var httpBackendSpy = spyOn($httpBackend, 'expectGET');

    scope.search(1);

    expect(httpBackendSpy).not.toHaveBeenCalled();
  });

  it('should get all supervisory nodes in a page depending on search criteria', function () {
    var supervisoryNode = {"code": "N1", "name": "Node 1", "parent": 2};
    var pagination = {"page": 1, "pageSize": 10, "numberOfPages": 10, "totalRecords": 100};
    var response = {"supervisoryNodes": [supervisoryNode], "pagination": pagination};
    scope.query = "Nod";
    scope.selectedSearchOption.value = 'parent';
    $httpBackend.when('GET', '/paged-search-supervisory-nodes.json?page=1&param=' + scope.query + '&parent=true').respond(response);
    scope.search(1);
    $httpBackend.flush();

    expect(scope.supervisoryNodeList).toEqual([supervisoryNode]);
    expect(scope.pagination).toEqual(pagination);
    expect(scope.currentPage).toEqual(1);
    expect(scope.showResults).toEqual(true);
    expect(scope.totalItems).toEqual(100);
  });

  it('should get all supervisory nodes in a page depending on last query', function () {
    var supervisoryNode = {"code": "N1", "name": "Node 1", "parent": 2};
    var pagination = {"page": 1, "pageSize": 10, "numberOfPages": 10, "totalRecords": 100};
    var response = {"supervisoryNodes": [supervisoryNode], "pagination": pagination};
    scope.query = "Nod";
    var lastQuery = "Super";
    scope.selectedSearchOption.value = 'parent';
    $httpBackend.when('GET', '/paged-search-supervisory-nodes.json?page=1&param=' + lastQuery + '&parent=true').respond(response);
    scope.search(1, lastQuery);
    $httpBackend.flush();

    expect(scope.supervisoryNodeList).toEqual([supervisoryNode]);
    expect(scope.pagination).toEqual(pagination);
    expect(scope.currentPage).toEqual(1);
    expect(scope.showResults).toEqual(true);
    expect(scope.totalItems).toEqual(100);
  });

  it('should clear search param and result list', function () {
    var supervisoryNode = {"code": "N1", "name": "Node 1", "parent": 2};
    scope.query = "query";
    scope.totalItems = 100;
    scope.supervisoryNodeList = [supervisoryNode];
    scope.showResults = true;

    scope.clearSearch();

    expect(scope.showResults).toEqual(false);
    expect(scope.query).toEqual("");
    expect(scope.totalItems).toEqual(0);
    expect(scope.supervisoryNodeList).toEqual([]);
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
    scope.searchedQuery = "no";
    var searchSpy = spyOn(scope, 'search');

    scope.$apply(function () {
      scope.currentPage = 6;
    });

    expect(searchSpy).toHaveBeenCalledWith(6, scope.searchedQuery);
  });

  it("should take to edit page of the supervisory node", function () {
    spyOn(location, "path");
    spyOn(navigateBackService, "setData");
    scope.edit(1);
    expect(location.path).toHaveBeenCalledWith('edit/1');
    expect(navigateBackService.setData).toHaveBeenCalledWith({query: scope.query, selectedSearchOption: scope.selectedSearchOption});
  });

});