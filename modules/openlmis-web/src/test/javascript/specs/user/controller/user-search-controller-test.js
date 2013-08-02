/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

describe("User Search Controller", function () {

  var scope, $httpBackend, ctrl, navigateBackService, location;
  beforeEach(module('openlmis.services'));
  var searchTextId = 'searchTextId';

  beforeEach(inject(function ($rootScope, _$httpBackend_, $controller, _navigateBackService_, $location) {
    scope = $rootScope.$new();
    $httpBackend = _$httpBackend_;
    scope.query = "joh";
    navigateBackService = _navigateBackService_;
    navigateBackService.query = '';
    location = $location;
    ctrl = $controller;
    ctrl('UserSearchController', {$scope: scope});
  }));

  it('should get all users depending on search criteria when three characters are entered in search', function () {
    var user = {"id": 1, "firstName": "john", "lastName": "Doe", "email": "john_doe@gmail.com"};
    var userResponse = {"userList": [user]};
    scope.query = "joh";

    $httpBackend.when('GET', '/users.json?param=' + scope.query).respond(userResponse);
    scope.showUserSearchResults();
    $httpBackend.flush();

    expect(scope.userList).toEqual([user]);
    expect(scope.resultCount).toEqual(1);
  });

  it('should filter users when more than 3 characters are entered for search with first 3 characters matching previous search', function () {
    scope.previousQuery = "joh";
    scope.query = "john_d";
    var user = {"id": 1, "firstName": "john", "lastName": "Doe", "email": "john_doe@gmail.com"};
    scope.userList = [user];

    scope.showUserSearchResults();

    expect(scope.filteredUsers).toEqual([user]);
    expect(scope.resultCount).toEqual(1);
  });

  it("should get and filter users when more than 3 characters are pasted for search and first 3 chars does not match " +
      "with previous query's first three chars", function () {
    scope.previousQuery = "abcd";
    scope.query = "lokesh";

    var user1 = {"id": 2, "userName": "lok", "firstName": "lokesh", "lastName": "Doe", "email": "lokesh_doe@gmail.com"};
    var user2 = {"id": 2, "userName": "loke", "firstName": "lokaaahh", "lastName": "Doe", "email": "lokaaahh_doe@gmail.com"};
    var userResponse = {"userList": [user1, user2]};
    $httpBackend.when('GET', '/users.json?param=lok').respond(userResponse);

    scope.showUserSearchResults(searchTextId);
    $httpBackend.flush();

    expect(scope.userList).toEqual([user1, user2]);
    expect(scope.filteredUsers).toEqual([user1]);
    expect(scope.resultCount).toEqual(1);
  });

  it("should save query into shared service on clicking edit link", function () {
    spyOn(navigateBackService, 'setData');
    spyOn(location, 'path');
    scope.query = "lokesh";
    scope.editUser(2);
    expect(navigateBackService.setData).toHaveBeenCalledWith({query: "lokesh"});
    expect(location.path).toHaveBeenCalledWith('edit/2');
  });

  it("should retain previous query value and update filtered query list when dom is loaded", function () {
    var query = "lok";
    navigateBackService.setData({query: query});
    $httpBackend.expect('GET', '/users.json?param=lok').respond(200, {});

    ctrl('UserSearchController', {$scope: scope});

    $httpBackend.flush();
    expect(query).toEqual(scope.query);
  });

});