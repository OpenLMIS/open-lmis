/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *   
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

describe("Requisition Group Controller", function () {

  var scope, ctrl, requisitionGroup, element, $httpBackend, location, requisitionGroupData, requisitionGroupMembers, compile;
  var controller;
  beforeEach(module('openlmis'));

  beforeEach(inject(function ($rootScope, _$httpBackend_, $controller, $location, $compile) {
    element = angular.element('<div class="facility-add-success" id="successMessage" style="display: none"></div>');
    scope = $rootScope.$new();
    $httpBackend = _$httpBackend_;
    compile = $compile;
    location = $location;
    scope.query = "rg";
    controller = $controller;
    requisitionGroup = {code: "RG1", name: "Group 1"};
    requisitionGroupMembers = [];
    requisitionGroupData = {"requisitionGroup": requisitionGroup, "requisitionGroupMembers": requisitionGroupMembers};
//    compile(element)(scope);
    ctrl = $controller('RequisitionGroupController', {$scope: scope, requisitionGroupData: requisitionGroupData});
  }));

  afterEach(function () {
    element.remove();
  });

  it('should set requisition group data in scope', function () {
    expect(scope.requisitionGroup).toEqual(requisitionGroup);
    expect(scope.requisitionGroupMembers).toEqual(requisitionGroupMembers);
  });

  it('should set requisition group and members undefined in scope when requisition group data not present',
    function () {
      ctrl = controller('RequisitionGroupController', {$scope: scope, requisitionGroupData: undefined});

      expect(scope.requisitionGroup).toEqual({});
      expect(scope.requisitionGroupMembers).toEqual([]);
    });

  it('should get all supervisory nodes in scope if query length is greater than 3', function () {
    scope.query = "Nod";
    var node1 = {"id": 1, "code": "N1", "name": "node 1"};
    var node2 = {"id": 2, "code": "N2", "name": "node 2"};
    var node3 = {"id": 3, "code": "N3", "name": "node 3"};
    var response = {"supervisoryNodeList": [node1, node2, node3]};

    $httpBackend.when('GET', '/search-supervisory-nodes.json?searchParam=' + scope.query).respond(response);
    scope.showSupervisoryNodeSearchResults();
    $httpBackend.flush();

    expect(scope.supervisoryNodes).toEqual([node1, node2, node3]);
    expect(scope.filteredNodeList).toEqual([node1, node2, node3]);
    expect(scope.nodeResultCount).toEqual(3);
  });

  it('should do client side filtering when previous query and current query are same', function () {
    spyOn($httpBackend, "expectGET");
    scope.previousQuery = "Nod";
    scope.query = "Nod";
    var node1 = {"id": 1, "code": "N1", "name": "node 1"};
    var node2 = {"id": 2, "code": "N2", "name": "node 2"};
    var node3 = {"id": 3, "code": "N3", "name": "node 3"};
    scope.supervisoryNodes = [node1, node2, node3];

    scope.showSupervisoryNodeSearchResults();

    expect($httpBackend.expectGET).not.toHaveBeenCalledWith('/search-supervisory-nodes.json?searchParam=' + scope.query);
    expect(scope.filteredNodeList).toEqual([node1, node2, node3]);
    expect(scope.previousQuery).toEqual("Nod");
    expect(scope.nodeResultCount).toEqual(3);
  });

  it('should not search results if query is undefined', function () {
    spyOn($httpBackend, "expectGET");
    scope.query = undefined;

    scope.showSupervisoryNodeSearchResults();

    expect($httpBackend.expectGET).not.toHaveBeenCalledWith('/search-supervisory-nodes.json?searchParam=' + scope.query);
  });

  it('should not search results if query length is less than 3', function () {
    spyOn($httpBackend, "expectGET");
    scope.query = "No";

    scope.showSupervisoryNodeSearchResults();

    expect($httpBackend.expectGET).not.toHaveBeenCalledWith('/search-supervisory-nodes.json?searchParam=' + scope.query);
  });

  it('should select parent group and clear scope fields', function () {
    var node = {code: "SN2", name: "node 2"};
    scope.setSelectedSupervisoryNode(node);
    expect(scope.requisitionGroup.supervisoryNode).toBe(node);
    expect(scope.query).toBeUndefined();
  });

  it('should delete supervisory node', function () {
    scope.requisitionGroup.parent = {"Code": "N1", "Name": "Group 1"};

    scope.clearSelectedNode();

    expect(scope.requisitionGroup.supervisoryNode).toBeUndefined();
  });

  it('should take to search page on cancel', function () {
    scope.cancel();
    expect(scope.$parent.message).toEqual("");
    expect(scope.$parent.requisitionGroupId).toBeUndefined();
    expect(location.path()).toEqual('/#/search');
  });

  it('should not save requisition group if invalid', function () {
    scope.requisitionGroupForm = {"$error": {"required": true}};

    scope.save();

    expect(scope.error).toEqual("form.error");
    expect(scope.showError).toBeTruthy();
  });

  it('should insert requisition group', function () {
    scope.requisitionGroup = {"code": "N100", "name": "group 100", "supervisoryNode": {"facility": {"code": "F10", "name": "village dispensary"}}};
    scope.requisitionGroupForm = {"$error": {"required": false}};

    requisitionGroupData = {"requisitionGroup": scope.requisitionGroup, "requisitionGroupMembers": []};

    $httpBackend.expectPOST('/requisitionGroups.json', scope.requisitionGroupData).respond(200,
      {"success": "Saved successfully", "requisitionGroupId": scope.requisitionGroup.id});
    scope.save();
    $httpBackend.flush();

    expect(scope.error).toEqual("");
    expect(scope.showError).toBeFalsy();
    expect(scope.$parent.message).toEqual("Saved successfully");
    expect(scope.$parent.requisitionGroupId).toEqual(requisitionGroup.id);
  });

  it('should update requisition group', function () {
    scope.requisitionGroupForm = {"$error": {"required": false}};
    requisitionGroup.id = 4;
    requisitionGroup.supervisoryNode = {"facility": {"code": "F10", "name": "village dispensary"}};

    requisitionGroupData = {"requisitionGroup": scope.requisitionGroup, "requisitionGroupMembers": []};

    $httpBackend.expectPUT('/requisitionGroups/' + requisitionGroup.id + '.json', requisitionGroupData).respond(200,
      {"success": "Saved successfully", "requisitionGroupId": requisitionGroup.id});
    scope.save();
    $httpBackend.flush();

    expect(scope.error).toEqual("");
    expect(scope.showError).toBeFalsy();
    expect(scope.$parent.message).toEqual("Saved successfully");
    expect(scope.$parent.requisitionGroupId).toEqual(requisitionGroup.id);
  });

  it('should remove requisition group member', function () {
    scope.requisitionGroupMembers = [
      {"facility": {"id": 1}},
      {"facility": {"id": 2}},
      {"facility": {"id": 3}}
    ];

    scope.removeMember(2);

    expect(scope.requisitionGroupMembers).toEqual([
      {"facility": {"id": 1}},
      {"facility": {"id": 3}}
    ]);
  });

  it('should add facilities to requisition group members', function () {
    var tempFacilities = [
      {"name": "fac1", "id": 1, "code": "code2"},
      {"name": "fac2", "id": 2, "code": "code1"}
    ]
    scope.showSlider = false;
    expect(element.css('display')).toEqual('none');

    var spyElement = spyOn(angular, "element").andCallFake(function (selector) {
      if (selector == '.facility-add-success') return element;
    });

    var spyFadeIn = spyOn(element, 'fadeIn').andCallThrough();
    var spyFadeOut = spyOn(element, "fadeOut").andCallThrough();

    ctrl = controller('RequisitionGroupController', {$scope: scope, requisitionGroupData: requisitionGroupData});
    expect(spyFadeOut).toHaveBeenCalledWith("fast");

    var result = scope.addMembers(tempFacilities);

    expect(scope.requisitionGroupMembers.length).toEqual(2);
    expect(scope.requisitionGroupMembers[0].facility).toEqual(tempFacilities[1]);
    expect(scope.requisitionGroupMembers[0].facility).toEqual(tempFacilities[1]);
    expect(scope.showSlider).toBeTruthy();
    expect(scope.duplicateFacilityName).toBeUndefined();
    expect(result).toBeTruthy();
    expect(spyElement).toHaveBeenCalledWith('.facility-add-success');
    expect(spyFadeIn).toHaveBeenCalledWith("slow");
  });

  it('should not add facilities to requisition group members if duplicate facility', function () {
    var tempFacilities = [
      {"name": "fac1", "id": 1, "code": "code2"},
      {"name": "fac2", "id": 2, "code": "code1"}
    ]
    scope.requisitionGroupMembers = [
      { facility: { name: 'fac2', id: 2, code: 'code1' }, requisitionGroup: { code: 'RG1', name: 'Group 1' } }
    ]

    var result = scope.addMembers(tempFacilities);

    expect(scope.requisitionGroupMembers.length).toEqual(1);
    expect(scope.duplicateFacilityName).toEqual("fac2");
    expect(scope.facilitiesAddedSuccesfully).toBeFalsy();
    expect(result).toBeFalsy();
  });

  it('should delete duplicate facility message when slider state changes', function () {
    scope.duplicateFacilityName = "fac1";

    scope.$apply(function () {
      scope.showSlider = false;
    });

    expect(scope.duplicateFacilityName).toBeUndefined();
  });
});
