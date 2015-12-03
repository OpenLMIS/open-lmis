/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe("User Search Controller", function () {

  var scope, $httpBackend, ctrl, navigateBackService, location, messageService;
  beforeEach(module('openlmis'));
  var searchTextId = 'searchTextId';

  beforeEach(inject(function ($rootScope, _$httpBackend_, $controller, _navigateBackService_, $location, _messageService_) {
    scope = $rootScope.$new();
    $httpBackend = _$httpBackend_;
    scope.query = "joh";
    navigateBackService = _navigateBackService_;
    navigateBackService.query = '';
    location = $location;
    ctrl = $controller;
    messageService = _messageService_;
    ctrl('UserSearchController', {$scope: scope, messageService: messageService});
  }));

  it('should get all users in a page depending on search criteria', function () {
    var user = {"id": 1, "firstName": "john", "lastName": "Doe", "email": "john_doe@gmail.com"};
    var pagination = {"page": 1, "pageSize": 10, "numberOfPages": 10, "totalRecords": 100};
    var response = {"userList": [user], "pagination": pagination};
    scope.query = "j";
    $httpBackend.when('GET', '/users.json?page=1&searchParam=' + scope.query).respond(response);

    scope.loadUsers(1);
    $httpBackend.flush();

    expect(scope.userList).toEqual([user]);
    expect(scope.pagination).toEqual(pagination);
    expect(scope.currentPage).toEqual(1);
    expect(scope.showResults).toEqual(true);
    expect(scope.totalItems).toEqual(100);
  });

  it('should get all users in a page depending on search criteria and lastQuery', function () {
    var user = {"id": 1, "firstName": "john", "lastName": "Doe", "email": "john_doe@gmail.com"};
    var pagination = {"page": 1, "pageSize": 10, "numberOfPages": 10, "totalRecords": 100};
    var response = {"userList": [user], "pagination": pagination};
    scope.query = "j";
    var lastQuery = "anotherQuery";
    $httpBackend.when('GET', '/users.json?page=1&searchParam=' + lastQuery).respond(response);

    scope.loadUsers(1, "anotherQuery");
    $httpBackend.flush();

    expect(scope.userList).toEqual([user]);
    expect(scope.pagination).toEqual(pagination);
    expect(scope.currentPage).toEqual(1);
    expect(scope.showResults).toEqual(true);
    expect(scope.totalItems).toEqual(100);
  });

  it('should return if query is null', function () {
    scope.query = "";
    var httpBackendSpy = spyOn($httpBackend, 'expectGET');

    scope.loadUsers(1);

    expect(httpBackendSpy).not.toHaveBeenCalled();
  });

  it('should clear search param and result list', function () {
    var userList = [{"id": 1, "firstName": "john", "lastName": "Doe", "email": "john_doe@gmail.com"}];

    scope.query = "j";
    scope.totalItems = 100;
    scope.userList = userList;
    scope.showResults = true;

    scope.clearSearch();

    expect(scope.showResults).toEqual(false);
    expect(scope.query).toEqual("");
    expect(scope.totalItems).toEqual(0);
    expect(scope.userList).toEqual([]);
  });

  it('should set query according to navigate back service', function () {
    scope.query = '';
    navigateBackService.query = 'query';

    scope.$emit('$viewContentLoaded');

    expect(scope.query).toEqual("query");
  });

  it('should trigger search on enter key', function () {
    var event = {"keyCode": 13};
    var searchSpy = spyOn(scope, 'loadUsers');

    scope.triggerSearch(event);

    expect(searchSpy).toHaveBeenCalledWith(1);
  });

  it('should get results according to specified page', function () {
    scope.currentPage = 5;
    scope.searchedQuery = "query";
    var searchSpy = spyOn(scope, 'loadUsers');

    scope.$apply(function () {
      scope.currentPage = 6;
    });

    expect(searchSpy).toHaveBeenCalledWith(6,scope.searchedQuery);
  });

  it("should save query into shared service on clicking edit link",function(){
    spyOn(navigateBackService, 'setData');
    spyOn(location, 'path');
    scope.query = "john";

    scope.edit(1);

    expect(navigateBackService.setData).toHaveBeenCalledWith({query: "john"});
    expect(location.path).toHaveBeenCalledWith('edit/1');
  });
});