/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

describe("Supervisory Node Controller", function () {

  var scope, ctrl, supervisoryNode, element, $httpBackend;
  beforeEach(module('openlmis'));

  beforeEach(inject(function ($rootScope, _$httpBackend_, $controller) {
    scope = $rootScope.$new();
    $httpBackend = _$httpBackend_;
    scope.query = "Nod";
    supervisoryNode = {code: "N1", name: "Node 1"};
    ctrl = $controller('SupervisoryNodeController', {$scope: scope, supervisoryNode: supervisoryNode});
  }));

  it('should set supervisory nodes in scope', function() {
    expect(scope.supervisoryNode).toBe(supervisoryNode);
  });

  it('should get all parent nodes in scope if query length is greater than 3 in case of add', function () {
    scope.query = "Nod";
    var node1 = {"id": 1, "code": "N1", "name": "Node 1"};
    var node2 = {"id": 2, "code": "N2", "name": "Node 2"};
    var node3 = {"id": 3, "code": "N3", "name": "Node 3"};
    var response = {"supervisoryNodeList":[node1, node2, node3]};

    $httpBackend.when('GET', '/parent-supervisory-nodes.json?searchParam=' + scope.query).respond(response);
    scope.showParentNodeSearchResults();
    $httpBackend.flush();

    expect(scope.parentNodes).toEqual([node1, node2, node3]);
    expect(scope.filteredNodes).toEqual([node1, node2, node3]);
    expect(scope.previousQuery).toEqual("Nod");
    expect(scope.resultCount).toEqual(3);
  });

  it('should get filtered parent nodes in scope if query length is greater than 3 in case of edit', function () {
    scope.query = "Nod";
    scope.supervisoryNode.id = 1;
    var node1 = {"id": 1, "code": "N1", "name": "Node 1"};
    var node2 = {"id": 2, "code": "N2", "name": "Node 2"};
    var node3 = {"id": 3, "code": "N3", "name": "Node 3"};
    var response = {"supervisoryNodeList":[node1, node2, node3]};

    $httpBackend.when('GET', '/parent-supervisory-nodes.json?searchParam=' + scope.query).respond(response);
    scope.showParentNodeSearchResults();
    $httpBackend.flush();

    expect(scope.parentNodes).toEqual([node2, node3]);
    expect(scope.filteredNodes).toEqual([node2, node3]);
    expect(scope.previousQuery).toEqual("Nod");
    expect(scope.resultCount).toEqual(2);
  });

  it('should do client side filtering when previous query and current query are same', function () {
    spyOn($httpBackend,"expectGET");
    scope.query = "Node";
    scope.previousQuery = "Nod";
    var node2 = {"id": 2, "code": "N2", "name": "Node 2"};
    var node3 = {"id": 3, "code": "N3", "name": "Node 3"};
    var node4 = {"id": 3, "code": "N3", "name": "Nodde 4"};
    scope.parentNodes = [node2, node3, node4];

    scope.showParentNodeSearchResults();

    expect($httpBackend.expectGET).not.toHaveBeenCalledWith('/parent-supervisory-nodes.json?searchParam=' + scope.query);
    expect(scope.filteredNodes).toEqual([node2, node3]);
    expect(scope.previousQuery).toEqual("Nod");
    expect(scope.resultCount).toEqual(2);
  });

  it('should not search results if query is undefined', function () {
    spyOn($httpBackend,"expectGET");
    scope.query = undefined;

    scope.showParentNodeSearchResults();

    expect($httpBackend.expectGET).not.toHaveBeenCalledWith('/parent-supervisory-nodes.json?searchParam=' + scope.query);
  });

  it('should not search results if query length is less than 3', function () {
    spyOn($httpBackend,"expectGET");
    scope.query = "No";

    scope.showParentNodeSearchResults();

    expect($httpBackend.expectGET).not.toHaveBeenCalledWith('/parent-supervisory-nodes.json?searchParam=' + scope.query);
  });

  it('should trim left and right whitespaces in query if length greater than 3', function () {
    scope.query = "  Node  ";
    $httpBackend.expectGET('/parent-supervisory-nodes.json?searchParam=' + "Node").respond(null);

    scope.showParentNodeSearchResults();

    $httpBackend.flush();
    expect(scope.query).toBe("Node");
  });

  it('should select parent node and clear scope fields', function() {
    var node = {code: "SN2", name: "Node 2"};
    scope.setSelectedParentNode(node);
    expect(scope.supervisoryNode.parent).toBe(node);
    expect(scope.query).toBeUndefined();
    expect(scope.resultCount).toBeUndefined();
    expect(scope.filteredFacilities).toBeUndefined();
    expect(scope.previousQuery).toBeUndefined();
  });

  it('should delete parent node', function() {
    scope.supervisoryNode.parent = {"Code":"N1", "Name": "Node 1"};

    scope.deleteParentNode();

    expect(scope.supervisoryNode.parent).toBeUndefined();
  });
});
