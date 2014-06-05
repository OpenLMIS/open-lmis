/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

describe("Supply Line Controller", function () {

  var scope, ctrl, supplyLine, $httpBackend, location, programs;
  beforeEach(module('openlmis'));

  beforeEach(inject(function ($rootScope, _$httpBackend_, $controller, $location) {
    scope = $rootScope.$new();
    $httpBackend = _$httpBackend_;
    location = $location;
    scope.query = "Nod";
    supplyLine = {"program": {"name": "TB"}, "supplyingFacility": {"name": "supplying"}};
    programs = [
      {"name": "TB", "id": 1},
      {"name": "MALARIA", "id": 2}
    ];
    ctrl = $controller('SupplyLineController', {$scope: scope, supplyLine: supplyLine, programs: programs});
  }));

  it('should set supply Line in scope', function () {
    expect(scope.supplyLine).toBe(supplyLine);
  });

  it('should get all top level nodes in scope if query length is greater than 3 in case of add', function () {
    scope.query = "Nod";
    var node1 = {"id": 1, "code": "N1", "name": "Node 1"};
    var node2 = {"id": 2, "code": "N2", "name": "Node 2"};
    var node3 = {"id": 3, "code": "N3", "name": "Node 3"};
    var response = {"supervisoryNodeList": [node1, node2, node3]};

    $httpBackend.when('GET', '/topLevelSupervisoryNodes.json?searchParam=' + scope.query).respond(response);
    scope.showTopLevelNodeResults();
    $httpBackend.flush();

    expect(scope.topLevelNodes).toEqual([node1, node2, node3]);
    expect(scope.filteredNodes).toEqual([node1, node2, node3]);
    expect(scope.previousQuery).toEqual("Nod");
    expect(scope.nodeResultCount).toEqual(3);
  });

  it('should do client side filtering when previous query and current query are same', function () {
    spyOn($httpBackend, "expectGET");
    scope.query = "Node";
    scope.previousQuery = "Nod";
    var node2 = {"id": 2, "code": "N2", "name": "Node 2"};
    var node3 = {"id": 3, "code": "N3", "name": "Node 3"};
    var node4 = {"id": 3, "code": "N3", "name": "Nodde 4"};
    scope.topLevelNodes = [node2, node3, node4];

    scope.showTopLevelNodeResults();

    expect($httpBackend.expectGET).not.toHaveBeenCalledWith('/topLevelSupervisoryNodes.json?searchParam=' + scope.query);
    expect(scope.filteredNodes).toEqual([node2, node3]);
    expect(scope.previousQuery).toEqual("Nod");
    expect(scope.nodeResultCount).toEqual(2);
  });

  it('should not search results if query is undefined', function () {
    spyOn($httpBackend, "expectGET");
    scope.query = undefined;

    scope.showTopLevelNodeResults();

    expect($httpBackend.expectGET).not.toHaveBeenCalledWith('/topLevelSupervisoryNodes.json?searchParam=' + scope.query);
  });

  it('should not search results if query length is less than 3', function () {
    spyOn($httpBackend, "expectGET");
    scope.query = "No";

    scope.showTopLevelNodeResults();

    expect($httpBackend.expectGET).not.toHaveBeenCalledWith('/topLevelSupervisoryNodes.json?searchParam=' + scope.query);
  });

  it('should select supervisory node and clear scope fields', function () {
    var node = {code: "SN2", name: "Node 2"};
    scope.setSupervisoryNode(node);
    expect(scope.supplyLine.supervisoryNode).toBe(node);
    expect(scope.query).toBeUndefined();
    expect(scope.nodeResultCount).toBeUndefined();
    expect(scope.filteredFacilities).toBeUndefined();
    expect(scope.previousQuery).toBeUndefined();
  });

  it('should delete supervisory node', function () {
    scope.supplyLine.parent = {"Code": "N1", "Name": "Node 1"};

    scope.deleteSupervisoryNode();

    expect(scope.supplyLine.supervisoryNode).toBeUndefined();
  });

  it('should take to search page on cancel', function () {
    scope.cancel();
    expect(scope.$parent.message).toEqual("");
    expect(scope.$parent.supplyLineId).toBeUndefined();
    expect(location.path()).toEqual('/#/search');
  });

  it('should not save supply Line if invalid', function () {
    scope.supplyLineForm = {"$error": {"required": true}};

    scope.save();

    expect(scope.error).toEqual("form.error");
    expect(scope.showError).toBeTruthy();
  });

  it('should not save supply Line if supplying facility missing', function () {
    scope.supplyLineForm = {"$error": {"required": false}};
    scope.supplyLine = {supplyingFacility: undefined};

    scope.save();

    expect(scope.error).toEqual("form.error");
    expect(scope.showError).toBeTruthy();
  });

  it('should not save supply Line if supervisory node missing', function () {
    scope.supplyLineForm = {"$error": {"required": false}};
    scope.supplyLine = {supervisoryNode: undefined};

    scope.save();

    expect(scope.error).toEqual("form.error");
    expect(scope.showError).toBeTruthy();
  });

  it('should insert supply Line', function () {
    scope.supplyLine = {"program": {"name": "HIV"},
      "supplyingFacility": {"code": "F10", "name": "village dispensary"},
      "supervisoryNode": {"code": "F10", "name": "village dispensary"}};
    scope.supplyLineForm = {"$error": {"required": false}};

    $httpBackend.expectPOST('/supplyLines.json', scope.supplyLine).respond(200, {"success": "Saved successfully", "supplyLine": scope.supplyLine});
    scope.save();
    $httpBackend.flush();

    expect(scope.error).toEqual("");
    expect(scope.showError).toBeFalsy();
    expect(scope.$parent.message).toEqual("Saved successfully");
    expect(scope.$parent.supplyLineId).toEqual(supplyLine.id);
  });

  it('should update supply Line', function () {
    scope.supplyLineForm = {"$error": {"required": false}};
    supplyLine.id = 4;
    supplyLine.supplyingFacility = {"code": "F10", "name": "village dispensary"};
    supplyLine.supervisoryNode = {"code": "F10", "name": "village dispensary"};

    $httpBackend.expectPUT('/supplyLines/' + supplyLine.id + '.json', supplyLine).respond(200, {"success": "Saved successfully", "supplyLineId": supplyLine.id});
    scope.save();
    $httpBackend.flush();

    expect(scope.error).toEqual("");
    expect(scope.showError).toBeFalsy();
    expect(scope.$parent.message).toEqual("Saved successfully");
    expect(scope.$parent.supplyLineId).toEqual(supplyLine.id);
  });

});
