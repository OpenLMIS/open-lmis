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

  var scope, $httpBackend, ctrl, navigateBackService, location, messageService;
  beforeEach(module('openlmis'));

  beforeEach(inject(function ($rootScope, _$httpBackend_, $controller, _navigateBackService_, $location, _messageService_) {
    scope = $rootScope.$new();
    $httpBackend = _$httpBackend_;
    scope.query = "Nod";
    navigateBackService = _navigateBackService_;
    navigateBackService.query = '';
    location = $location;
    ctrl = $controller;
    messageService = _messageService_;
    ctrl('SupervisoryNodeSearchController', {$scope: scope, messageService: messageService});
  }));

  it('should get all supervisory nodes in a page depending on search criteria when three characters are entered in search', function () {
    var supervisoryNode = {"code": "N1", "name": "Node 1", "parent": 2};
    var pagination = {"page" : 1, "pageSize" : 10, "numberOfPages" : 10, "totalRecords" : 100};
    var response = {"supervisoryNodes": [supervisoryNode], "pagination" : pagination};
    scope.query = "Nod";
    scope.selectedSearchOption.value = 'parent';
    scope.currentPage = 1;
    $httpBackend.when('GET', '/search-supervisory-nodes.json?page=1&param=' + scope.query + '&parent=true' ).respond(response);
    scope.search(1);
    $httpBackend.flush();

    expect(scope.supervisoryNodeList).toEqual([supervisoryNode]);
    expect(scope.pagination).toEqual(pagination);
  });

/*  it('should filter supervisory nodes when more than 3 characters are entered for search with first 3 characters matching previous search', function () {
    scope.previousQuery = "Nod";
    scope.query = "Node";
    var supervisoryNode = {"code": "N1", "name": "Node 1", "parent": 2};
    scope.supervisoryNodeList = [supervisoryNode];

    scope.showSupervisoryNodeSearchResults();

    expect(scope.filteredNodes).toEqual([supervisoryNode]);
  });*/
});