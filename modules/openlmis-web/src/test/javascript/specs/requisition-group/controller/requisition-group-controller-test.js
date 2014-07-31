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

  var scope, ctrl, requisitionGroup, messageService, $httpBackend, location, requisitionGroupData, requisitionGroupMembers, compile;
  var controller, schedules, programs, requisitionGroupProgramSchedules;
  beforeEach(module('openlmis'));

  beforeEach(inject(function ($rootScope, _$httpBackend_, $controller, $location, $compile, _messageService_) {
    scope = $rootScope.$new();
    $httpBackend = _$httpBackend_;
    messageService = _messageService_;
    compile = $compile;
    location = $location;
    scope.query = "rg";
    controller = $controller;
    requisitionGroup = {code: "RG1", name: "Group 1"};
    requisitionGroupMembers = [];
    requisitionGroupProgramSchedules = [
      {"program": {"name": "malaria", "id": 1}}
    ];
    programs = [
      {"name": "TB", "id": 1},
      {"name": "MALARIA", "id": 2},
      {"name": "ESS_MEDS", "id": 3},
      {"name": "XYZ", "id": 4},
      {"name": "ABC", "id": 5}
    ];

    schedules = [
      {"id": 1, "name": "SCH1", "code": "Monthly"},
      {"id": 2, "name": "SCH2", "code": "Yearly"}
    ];

    requisitionGroupData = {"requisitionGroup": requisitionGroup, "requisitionGroupMembers": requisitionGroupMembers, "requisitionGroupProgramSchedules": requisitionGroupProgramSchedules};
    ctrl = $controller('RequisitionGroupController', {$scope: scope, requisitionGroupData: requisitionGroupData, programs: programs, schedules: schedules});
  }));

  it('should set requisition group data in scope', function () {
    expect(scope.requisitionGroup).toEqual(requisitionGroup);
    expect(scope.requisitionGroupMembers).toEqual(requisitionGroupMembers);
    expect(scope.requisitionGroupProgramSchedules).toEqual(requisitionGroupProgramSchedules);
    expect(scope.requisitionGroupProgramSchedules[0].underEdit).toBeFalsy();
  });

  it('should set requisition group data as empty in scope when requisition group data not present', function () {
    ctrl = controller('RequisitionGroupController', {$scope: scope, requisitionGroupData: undefined, programs: programs, schedules: schedules});

    expect(scope.requisitionGroup).toEqual({});
    expect(scope.requisitionGroupMembers).toEqual([]);
    expect(scope.requisitionGroupProgramSchedules).toEqual([]);
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
    expect(scope.$parent.successMessage).toEqual("");
    expect(scope.$parent.requisitionGroupId).toBeUndefined();
    expect(location.path()).toEqual('/#/search');
  });

  it('should not save requisition group if required fields not filled', function () {
    scope.requisitionGroupForm = {"$error": {"required": true}};

    scope.save();

    expect(scope.error).toEqual("form.error");
    expect(scope.showError).toBeTruthy();
    expect(scope.successMessage).toEqual("");
  });

  it('should not save requisition group if error occurs while save', function () {
    scope.requisitionGroup = {"code": "N100", "name": "group 100", "supervisoryNode": {"facility": {"code": "F10", "name": "village dispensary"}}};
    scope.requisitionGroupForm = {"$error": {"required": false}};

    requisitionGroupData = {"requisitionGroup": scope.requisitionGroup, "requisitionGroupMembers": [], "requisitionGroupProgramSchedules": requisitionGroupProgramSchedules};

    $httpBackend.expectPOST('/requisitionGroups.json', requisitionGroupData).respond(400, {"error": "error"});
    scope.save();
    $httpBackend.flush();

    expect(scope.error).toEqual("error");
    expect(scope.showError).toBeTruthy();
    expect(scope.successMessage).toEqual("");
  });

  it('should not save requisition group if at least one program schedule is under edit', function () {
    scope.requisitionGroup = {"code": "N100", "name": "group 100", "supervisoryNode": {"facility": {"code": "F10", "name": "village dispensary"}}};
    scope.requisitionGroupForm = {"$error": {"required": false}};
    scope.requisitionGroupProgramSchedules[0].underEdit = true;

    scope.save();

    expect(scope.error).toEqual('error.program.schedules.not.done');
  });

  it('should insert requisition group', function () {
    scope.requisitionGroup = {"code": "N100", "name": "group 100", "supervisoryNode": {"facility": {"code": "F10", "name": "village dispensary"}}};
    scope.requisitionGroupForm = {"$error": {"required": false}};

    requisitionGroupData = {"requisitionGroup": scope.requisitionGroup, "requisitionGroupMembers": [], "requisitionGroupProgramSchedules": requisitionGroupProgramSchedules};

    $httpBackend.expectPOST('/requisitionGroups.json', requisitionGroupData).respond(200,
      {"success": "Saved successfully", "requisitionGroupId": scope.requisitionGroup.id});
    scope.save();
    $httpBackend.flush();

    expect(scope.error).toEqual("");
    expect(scope.showError).toBeFalsy();
    expect(scope.$parent.successMessage).toEqual("Saved successfully");
    expect(scope.$parent.requisitionGroupId).toEqual(requisitionGroup.id);
  });

  it('should update requisition group', function () {
    scope.requisitionGroupForm = {"$error": {"required": false}};
    requisitionGroup.id = 4;
    requisitionGroup.supervisoryNode = {"facility": {"code": "F10", "name": "village dispensary"}};

    requisitionGroupData = {"requisitionGroup": scope.requisitionGroup, "requisitionGroupMembers": [], "requisitionGroupProgramSchedules": requisitionGroupProgramSchedules};

    $httpBackend.expectPUT('/requisitionGroups/' + requisitionGroup.id + '.json', requisitionGroupData).respond(200,
      {"success": "Saved successfully", "requisitionGroupId": requisitionGroup.id});
    scope.save();
    $httpBackend.flush();

    expect(scope.error).toEqual("");
    expect(scope.showError).toBeFalsy();
    expect(scope.$parent.successMessage).toEqual("Saved successfully");
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
    ];
    scope.showSlider = false;
    spyOn(messageService, 'get').andReturn('facilities added sucessfully');

    ctrl = controller('RequisitionGroupController', {$scope: scope, requisitionGroupData: requisitionGroupData, programs: programs, schedules: schedules});

    var result = scope.addMembers(tempFacilities);

    expect(scope.requisitionGroupMembers.length).toEqual(2);
    expect(scope.requisitionGroupMembers[0].facility).toEqual(tempFacilities[1]);
    expect(scope.requisitionGroupMembers[1].facility).toEqual(tempFacilities[0]);
    expect(scope.showMultipleFacilitiesSlider).toBeTruthy();
    expect(scope.message).toEqual('facilities added sucessfully');
    expect(scope.duplicateFacilityName).toBeUndefined();
    expect(result).toBeTruthy();
  });

  it('should not add facilities to requisition group members if duplicate facility', function () {
    var tempFacilities = [
      {"name": "fac1", "id": 1, "code": "code2"},
      {"name": "fac2", "id": 2, "code": "code1"}
    ];
    scope.requisitionGroupMembers = [
      { facility: { name: 'fac2', id: 2, code: 'code1' }, requisitionGroup: { code: 'RG1', name: 'Group 1' } }
    ];

    var result = scope.addMembers(tempFacilities);

    expect(scope.requisitionGroupMembers.length).toEqual(1);
    expect(scope.duplicateFacilityName).toEqual("fac2");
    expect(scope.message).toBeUndefined();
    expect(result).toBeFalsy();
  });

  it('should delete duplicate facility message when slider state changes', function () {
    scope.duplicateFacilityName = "fac1";

    scope.$apply(function () {
      scope.showSlider = false;
    });

    expect(scope.duplicateFacilityName).toBeUndefined();
  });

  it('should add new row', function () {
    scope.addNew = false;

    scope.addNewRow();

    expect(scope.addNew).toBeTruthy();
  });

  it('should update processing schedule', function () {
    scope.schedules = [
      {"id": 2, "name": "SCH2", "code": "Yearly"},
      {"id": 1, "name": "SCH1", "code": "Monthly"}
    ];
    var index = 0;
    scope.requisitionGroupProgramSchedules = [
      {"processingSchedule": {"id": 1, "name": "SCH2", "code": "Yearly"}}
    ];

    scope.updateSchedule(index);

    expect(scope.requisitionGroupProgramSchedules[index].processingSchedule.id).toEqual(1);
    expect(scope.requisitionGroupProgramSchedules[index].processingSchedule.name).toEqual("SCH1");
    expect(scope.requisitionGroupProgramSchedules[index].processingSchedule.code).toEqual("Monthly");
  });

  it('should set under edit and make a copy during edit', function () {
    scope.requisitionGroupProgramSchedules = [
      {"underEdit": false}
    ];
    var index = 0;
    var copySpy = spyOn(angular, "copy");

    scope.edit(index);

    expect(scope.requisitionGroupProgramSchedules[index].underEdit).toBeTruthy();
    expect(copySpy).toHaveBeenCalledWith(scope.requisitionGroupProgramSchedules[index]);
  });

  it('should clear drop off facility', function () {
    var index = 0;
    scope.requisitionGroupProgramSchedules = [
      {"dropOffFacility": {"name": "FC1"}}
    ];

    scope.clearDropOffFacility(index);

    expect(scope.requisitionGroupProgramSchedules[index].dropOffFacility).toBeUndefined();
  });

  it('should be able to cancel edit', function () {
    var index = 0;
    scope.requisitionGroupProgramSchedules = [
      {"name": "Malaria", "underEdit": true}
    ];
    scope.edit(index);
    scope.requisitionGroupProgramSchedules[index].name = "TB";

    scope.cancelEdit(index);

    expect(scope.requisitionGroupProgramSchedules[index].name).toEqual("Malaria");
    expect(scope.requisitionGroupProgramSchedules[index].underEdit).toBeFalsy();
  });

  it('should be able to cancel add new', function () {
    scope.newProgramSchedule = {"name": "Malaria"};
    scope.addNew = true;

    scope.cancelAdd();

    expect(scope.newProgramSchedule).toEqual({});
    expect(scope.addNew).toBeFalsy();
  });

  it('should be able to find program schedule under edit', function () {
    scope.requisitionGroupProgramSchedules = [
      {"name": "Malaria", "underEdit": false},
      {"name": "Essential Medicines", "underEdit": true}
    ];

    var result = scope.findProgramScheduleUnderEdit();

    expect(result).toEqual(scope.requisitionGroupProgramSchedules[1]);
  });

  it('should be able to save editable row', function () {
    scope.requisitionGroupProgramSchedules = [
      {"name": "Malaria", "underEdit": false, "dropOffFacility": {"id": 1}},
      {"name": "Essential Medicines", "underEdit": true, "dropOffFacility": {"id": 2}}
    ];
    var index = 1;
    var programScheduleUnderEditFacility = {"name": "Fac1"};
    scope.associate(programScheduleUnderEditFacility);

    scope.saveEditableRow(1);

    expect(scope.requisitionGroupProgramSchedules[index].underEdit).toBeFalsy();
    expect(scope.requisitionGroupProgramSchedules[index].dropOffFacility).toEqual(programScheduleUnderEditFacility);
  });

  it('should not save drop off facility if no row in editable mode', function () {
    scope.requisitionGroupProgramSchedules = [
      {"name": "Malaria", "underEdit": false, "dropOffFacility": {"id": 1}},
      {"name": "Essential Medicines", "underEdit": false, "dropOffFacility": {"id": 2}}
    ];
    var index = 1;
    var programScheduleUnderEditFacility = {"name": "Fac1"};
    scope.associate(programScheduleUnderEditFacility);

    scope.saveEditableRow(1);

    expect(scope.requisitionGroupProgramSchedules[index].underEdit).toBeFalsy();
    expect(scope.requisitionGroupProgramSchedules[index].dropOffFacility).toEqual({id: 2});
  });

  it('should not save drop off facility if no drop facility selected row in editable mode', function () {
    scope.requisitionGroupProgramSchedules = [
      {"name": "Malaria", "underEdit": false, "dropOffFacility": {"id": 1}},
      {"name": "Essential Medicines", "underEdit": false, "dropOffFacility": {"id": 2}}
    ];
    var index = 1;
    scope.associate();

    scope.saveEditableRow(1);

    expect(scope.requisitionGroupProgramSchedules[index].underEdit).toBeFalsy();
    expect(scope.requisitionGroupProgramSchedules[index].dropOffFacility).toEqual({id: 2});
  });

  it('should close slider if already opened', function () {
    scope.showSlider = true;

    scope.toggleSlider();

    expect(scope.currentSlider).toBeUndefined();
    expect(scope.showSlider).toBeFalsy();
    expect(scope.extraParams.virtualFacility).toBeNull();
    expect(scope.extraParams.enabled).toBeTruthy();
  });

  it('should toggle multiple facilities slider', function () {
    scope.showMultipleFacilitiesSlider = true;

    scope.toggleMultipleFacilitiesSlider();

    expect(scope.showMultipleFacilitiesSlider).toBeFalsy();
    expect(scope.extraMultipleParams.virtualFacility).toBeNull();
    expect(scope.extraMultipleParams.enabled).toBeTruthy();
  });

  it('should open slider if already closed', function () {
    scope.showSlider = false;

    scope.toggleSlider(1);

    expect(scope.currentSlider).toEqual(1);
    expect(scope.showSlider).toBeTruthy();
    expect(scope.extraParams.virtualFacility).toBeNull();
    expect(scope.extraParams.enabled).toBeTruthy();
  });

  it('should associate facility to programSchedule Under Edit', function () {
    scope.requisitionGroupProgramSchedules = [
      {"name": "Malaria", "underEdit": false},
      {"name": "Essential Medicines", "underEdit": true}
    ];
    scope.showSlider = true;
    var facility = {"name": "Fac1"};

    scope.associate(facility);

    expect(scope.requisitionGroupProgramSchedules[1].dropOffFacility).toEqual(facility);
    expect(scope.showSlider).toBeFalsy();
  });

  it('should associate facility to new programSchedule', function () {
    scope.requisitionGroupProgramSchedules = [
      {"name": "Malaria", "underEdit": false},
      {"name": "Essential Medicines", "underEdit": false}
    ];
    scope.showSlider = true;
    var facility = {"name": "Fac1"};

    scope.associate(facility);

    expect(scope.newProgramSchedule.dropOffFacility).toEqual(facility);
    expect(scope.showSlider).toBeFalsy();
  });

  it('should add program schedules and sort remaining programs', function () {
    scope.requisitionGroupProgramSchedules = [
      {"name": "Malaria", "underEdit": false, "program": {"id": 1}},
      {"name": "Malaria", "underEdit": false, "program": {"id": 2}}
    ];
    scope.newProgramSchedule = {"program": {"id": 3}};
    scope.addNew = true;

    scope.addProgramSchedules();
    expect(scope.requisitionGroupProgramSchedules.length).toEqual(3);
    expect(scope.newProgramSchedule).toEqual({});
    expect(scope.addNew).toBeFalsy();
    expect(scope.programs.length).toEqual(2);
    expect(scope.programs[0].name).toEqual("ABC");
    expect(scope.programs[1].name).toEqual("XYZ");
  });

  it('should remove program and sort remaining programs', function () {
    scope.requisitionGroupProgramSchedules = [
      {"name": "Malaria", "underEdit": false, "program": {"id": 1}},
      {"name": "Malaria", "underEdit": false, "program": {"id": 2}}
    ];

    scope.remove(2);

    expect(scope.requisitionGroupProgramSchedules.length).toEqual(1);
    expect(scope.programs.length).toEqual(4);
    expect(scope.programs[0].name).toEqual("ABC");
    expect(scope.programs[1].name).toEqual("ESS_MEDS");
    expect(scope.programs[2].name).toEqual("MALARIA");
    expect(scope.programs[3].name).toEqual("XYZ");
  });
});

describe("Requisition Group resolve", function () {
  var $httpBackend, ctrl, $timeout, $route, $q;
  var deferredObject;
  beforeEach(module('openlmis'));

  beforeEach(inject(function (_$httpBackend_, $controller, _$timeout_, _$route_) {
    $httpBackend = _$httpBackend_;
    deferredObject = {promise: {id: 1}, resolve: function () {
    }};
    spyOn(deferredObject, 'resolve');
    $q = {defer: function () {
      return deferredObject
    }};
    $timeout = _$timeout_;
    ctrl = $controller;
    $route = _$route_;
  }));

  it('should get requisition group data', function () {
    $route = {current: {params: {id: 1}}};
    $httpBackend.expect('GET', '/requisitionGroups/1.json').respond({});
    ctrl(RequisitionGroupController.resolve.requisitionGroupData, {$q: $q, $route: $route});
    $timeout.flush();
    $httpBackend.flush();
    expect(deferredObject.resolve).toHaveBeenCalled();
  });

  it('should get programs', function () {
    $httpBackend.expect('GET', '/programs/pull.json').respond({});
    ctrl(RequisitionGroupController.resolve.programs, {$q: $q});
    $timeout.flush();
    $httpBackend.flush();
    expect(deferredObject.resolve).toHaveBeenCalled();
  });

  it('should get schedules', function () {
    $httpBackend.expect('GET', '/schedules.json').respond({});
    ctrl(RequisitionGroupController.resolve.schedules, {$q: $q});
    $timeout.flush();
    $httpBackend.flush();
    expect(deferredObject.resolve).toHaveBeenCalled();
  });
});
