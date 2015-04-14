/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

describe("Product Search Controller", function () {

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
    ctrl('ProductSearchController', {$scope: scope});
  }));

  it('should get all programs in a page depending on search criteria', function () {
    var programProduct = {"program": {"code": "pg1", "name": "prog1"}, "product": {"code": "pd1", "name": "prod1"}};
    var pagination = {"page": 1, "pageSize": 10, "numberOfPages": 10, "totalRecords": 100};
    var response = {"programProductList": [programProduct], "pagination": pagination};
    scope.query = "pro";
    scope.selectedSearchOption = {"value": 'program'};
    $httpBackend.when('GET', '/season-rationing/adjustmentProducts.json?').respond({});
    $httpBackend.when('GET', '/programProducts/search.json?column=program&page=1&searchParam=' + scope.query).respond(response);
    scope.loadProducts(1);
    $httpBackend.flush();

    expect(scope.programProducts).toEqual([programProduct]);
    expect(scope.pagination).toEqual(pagination);
    expect(scope.currentPage).toEqual(1);
    expect(scope.showResults).toEqual(true);
    expect(scope.totalItems).toEqual(100);
  });

  it('should get all programs in a page depending on last query', function () {
    var programProduct = {"program": {"code": "pg1", "name": "prog1"}, "product": {"code": "pd1", "name": "prod1"}};
    var pagination = {"page": 1, "pageSize": 10, "numberOfPages": 10, "totalRecords": 100};
    var response = {"programProductList": [programProduct], "pagination": pagination};
    scope.query = "pro";
    var lastQuery = "essential";
    scope.selectedSearchOption = {"value": 'program'};
    $httpBackend.when('GET', '/season-rationing/adjustmentProducts.json?').respond({});
    $httpBackend.when('GET', '/programProducts/search.json?column=program&page=1&searchParam=' + lastQuery).respond(response);
    scope.loadProducts(1, lastQuery);
    $httpBackend.flush();

    expect(scope.programProducts).toEqual([programProduct]);
    expect(scope.pagination).toEqual(pagination);
    expect(scope.currentPage).toEqual(1);
    expect(scope.showResults).toEqual(true);
    expect(scope.totalItems).toEqual(100);
  });

  it('should clear search param and result list', function () {
    var programProduct = {"program": {"code": "pg1", "name": "prog1"}, "product": {"code": "pd1", "name": "prod1"}};
    scope.query = "query";
    scope.totalItems = 100;
    scope.programProducts = [programProduct];
    scope.showResults = true;

    scope.clearSearch();

    expect(scope.showResults).toEqual(false);
    expect(scope.query).toEqual("");
    expect(scope.totalItems).toEqual(0);
    expect(scope.programProducts).toEqual([]);
  });

  it('should trigger search on enter key', function () {
    var event = {"keyCode": 13};
    var searchSpy = spyOn(scope, 'loadProducts');

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
    scope.searchedQuery = "ess";
    var searchSpy = spyOn(scope, 'loadProducts');

    scope.$apply(function () {
      scope.currentPage = 6;
    });

    expect(searchSpy).toHaveBeenCalledWith(6, scope.searchedQuery);
  });

  it("should save query into shared service on clicking edit link", function () {
    spyOn(navigateBackService, 'setData');
    spyOn(location, 'path');
    scope.query = "p1";
    scope.selectedSearchOption = "product";

    scope.edit(1);

    expect(navigateBackService.setData).toHaveBeenCalledWith({query: "p1", selectedSearchOption: "product" });
    expect(location.path).toHaveBeenCalledWith('edit/1');
  });

  it('should return if query is null', function () {
    scope.query = "";
    var httpBackendSpy = spyOn($httpBackend, 'expectGET');

    scope.loadProducts(1);

    expect(httpBackendSpy).not.toHaveBeenCalled();
  });

  it('should show category if index is zero and product category is there', function () {
    scope.programProducts = [
      {"productCategory": "Analgesics"}
    ];
    expect(scope.showCategory(0)).toBeTruthy();
  });

  it('should not show category if current and before category is same', function () {
    scope.programProducts = [
      {"productCategory": {name: "Analgesics"}},
      {"productCategory": {name: "Analgesics"}}
    ];
    expect(scope.showCategory(1)).toBeFalsy();
  });

  it('should not show category if current and before category do not have a category', function () {
    scope.programProducts = [
      { name: "essential"},
      { name: "essential"}
    ];
    expect(scope.showCategory(1)).toBeFalsy();
  });

  it('should show category if current category is null but not before', function () {
    scope.programProducts = [
      { name: "essential", "productCategory": {name: "Analgesics"}},
      { name: "essential"}
    ];
    expect(scope.showCategory(1)).toBeTruthy();
  });
});