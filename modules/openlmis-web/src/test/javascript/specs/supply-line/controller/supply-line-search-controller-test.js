/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

describe("Supply Line Search Controller", function () {

  var scope, $httpBackend, ctrl, navigateBackService, location;
  beforeEach(module('openlmis'));

  beforeEach(inject(function ($rootScope, _$httpBackend_, $controller, _navigateBackService_, $location) {
    scope = $rootScope.$new();
    $httpBackend = _$httpBackend_;
    location = $location;
    scope.query = "Nod";
    navigateBackService = _navigateBackService_;
    navigateBackService.query = '';
    ctrl = $controller;
    ctrl('SupplyLineSearchController', {$scope: scope});
  }));

  it('should get all supply Lines in a page depending on search criteria', function () {
    var supplyLine = {program: {name: "P1"}, supplyingFacility: {name: "Fac 1"}, supervisoryNode: {name: "Node 1"}, description: "desc"};
    var pagination = {"page": 1, "pageSize": 10, "numberOfPages": 10, "totalRecords": 100};
    var response = {"supplyLines": [supplyLine], "pagination": pagination};
    scope.query = "Nod";
    scope.selectedSearchOption.value = 'supervisoryNode';
    $httpBackend.when('GET', '/supplyLines/search.json?column=supervisoryNode&page=1&searchParam=' + scope.query).respond(response);
    scope.search(1);
    $httpBackend.flush();

    expect(scope.supplyLines).toEqual([supplyLine]);
    expect(scope.pagination).toEqual(pagination);
    expect(scope.currentPage).toEqual(1);
    expect(scope.showResults).toEqual(true);
    expect(scope.totalItems).toEqual(100);
  });

  it('should get all supply Lines in a page depending on last query', function () {
    var supplyLine = {program: {name: "P1"}, supplyingFacility: {name: "Fac 1"}, supervisoryNode: {name: "Node 1"}, description: "desc"};
    var pagination = {"page": 1, "pageSize": 10, "numberOfPages": 10, "totalRecords": 100};
    var response = {"supplyLines": [supplyLine], "pagination": pagination};
    scope.query = "Nod";
    var lastQuery = "node";
    scope.selectedSearchOption.value = 'supervisoryNode';
    $httpBackend.when('GET', '/supplyLines/search.json?column=supervisoryNode&page=1&searchParam=' + lastQuery).respond(response);
    scope.search(1, lastQuery);
    $httpBackend.flush();

    expect(scope.supplyLines).toEqual([supplyLine]);
    expect(scope.pagination).toEqual(pagination);
    expect(scope.currentPage).toEqual(1);
    expect(scope.showResults).toEqual(true);
    expect(scope.totalItems).toEqual(100);
  });

  it('should not get supply lines if query is undefined ', function () {
    spyOn($httpBackend, "expectGET");
    scope.query = "";

    scope.search(1);

    expect($httpBackend.expectGET).not.toHaveBeenCalledWith('/supplyLines/search.json?column=supervisoryNode&page=1&searchParam=Nod');
  });

  it('should clear search param and result list', function () {
    var supplyLine = {program: {name: "P1"}, supplyingFacility: {name: "Fac 1"}, supervisoryNode: {name: "Node 1"}, description: "desc"};
    scope.query = "query";
    scope.totalItems = 100;
    scope.supplyLines = [supplyLine];
    scope.showResults = true;

    scope.clearSearch();

    expect(scope.showResults).toEqual(false);
    expect(scope.query).toEqual("");
    expect(scope.totalItems).toEqual(0);
    expect(scope.supplyLines).toEqual([]);
  });

  it('should trigger search on enter key', function () {
    var event = {"keyCode": 13};
    var searchSpy = spyOn(scope, 'search');

    scope.triggerSearch(event);

    expect(searchSpy).toHaveBeenCalledWith(1);
  });

  it('should not trigger search on keys apart from enter', function () {
    var event = {"keyCode": 56};
    var searchSpy = spyOn(scope, 'search');

    scope.triggerSearch(event);

    expect(searchSpy).not.toHaveBeenCalledWith(1);
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
    scope.searchedQuery = "Nod";
    var searchSpy = spyOn(scope, 'search');

    scope.$apply(function () {
      scope.currentPage = 6;
    });

    expect(searchSpy).toHaveBeenCalledWith(6, scope.searchedQuery);
  });

  it('should not search records when currentPage changed to 0', function () {
    scope.currentPage = 5;
    var searchSpy = spyOn(scope, 'search');

    scope.$apply(function () {
      scope.currentPage = 0;
    });

    expect(searchSpy).not.toHaveBeenCalledWith(0);
  });

  it("should save query into shared service on clicking edit link",function(){
    spyOn(navigateBackService, 'setData');
    spyOn(location, 'path');
    scope.query = "node";
    scope.selectedSearchOption = "supervisory";

    scope.edit(1);

    expect(navigateBackService.setData).toHaveBeenCalledWith({query: "node", selectedSearchOption: "supervisory" });
    expect(location.path).toHaveBeenCalledWith('edit/1');
  });
});