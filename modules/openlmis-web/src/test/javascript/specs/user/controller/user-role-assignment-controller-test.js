/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe("User", function () {

  beforeEach(module('openlmis'));
  beforeEach(module('ui.bootstrap.dialog'));

  describe("User Role Assignment Controller", function () {

    var validDZRole;
    var scope, $httpBackend, ctrl, messageService;

    beforeEach(inject(function ($rootScope, _$httpBackend_, $controller, _messageService_) {
      validDZRole = {deliveryZone: {id: 1}, programId: 1, roleIds: [
        {id: 1}
      ]};

      messageService = _messageService_;

      spyOn(messageService, 'get').andCallFake(function (value) {
        if (value == 'label.active') return "Active"
        if (value == 'label.inactive') return "Inactive"
      })
      scope = $rootScope.$new();
      $httpBackend = _$httpBackend_;
      scope.user = {active: true};
      ctrl = $controller(UserRoleAssignmentController, {$scope: scope});
    }));

    it('should display only available programs in add dropdown', function () {
      scope.$parent = {allSupportedPrograms: [
        {"program": {id: 1, name: 'p1', active: true}},
        {"program": {id: 2, name: 'p2', active: true}},
        {"program": {id: 3, name: 'p3', active: true}},
        {"program": {id: 4, name: 'p4', active: false}}
      ]};

      var existingProgramsMappedForUser = [
        {programId: 2},
        {programId: 3}
      ];
      scope.user = {homeFacilityRoles: existingProgramsMappedForUser};

      var availablePrograms = scope.availableSupportedProgramsWithStatus();

      expect(availablePrograms).toEqual([
        {"program": {id: 1, name: 'p1', status: "Active", active: true}},
        {"program": {id: 4, name: 'p4', status: "Inactive", active: false}}
      ]);
    });

    it("should display home facility role assignment options if user and facility not null", function () {
      scope.user = {"id": 123, "userName": "User420", "facilityId": 1};
      expect(scope.showHomeFacilityRoleAssignmentOptions()).toEqual(true);
    });

    it("should add home facility role if facility program and role is selected", function () {
      scope.user = {"id": 123, "userName": "User420", "facilityId": 1};
      scope.programSelected = 1;
      scope.selectedRoleIds = [1, 2];
      scope.addHomeFacilityRole();
      expect(scope.user.homeFacilityRoles).toEqual([
        {"roleIds": [1, 2], "programId": 1}
      ]);
      expect(scope.showHomeFacilityRoleMappingError).toEqual(false);
    });

    it("should not add home facility role if any of program and role is not selected", function () {
      scope.user = {"id": 123, "userName": "User420", "facilityId": 1};
      scope.programSelected = null;
      scope.selectedRoleIds = [1, 2];
      scope.addHomeFacilityRole();
      expect(scope.user.homeFacilityRoles).toEqual(undefined);
      expect(scope.showHomeFacilityRoleMappingError).toEqual(true);
    });

    it("should add supervisor role if program, supervisory node and role is selected", function () {
      scope.user = {"id": 123, "userName": "User420", "facilityId": 1};
      scope.selectedSupervisoryNodeIdToSupervise = 1;
      scope.selectedProgramIdToSupervise = 1;
      scope.selectedRoleIdsToSupervise = [1, 2];
      scope.addSupervisoryRole();
      expect(scope.user.supervisorRoles).toEqual([
        {"roleIds": [1, 2], "programId": 1, "supervisoryNode": {"id": 1}}
      ]);
      expect(scope.showSupervisorRoleMappingError).toEqual(false);
    });

    it("should not add home facility role if any of program, supervisory node and role is not selected", function () {
      scope.user = {"id": 123, "userName": "User420", "facilityId": 1};

      scope.selectedSupervisoryNodeIdToSupervise = null;
      scope.selectedProgramIdToSupervise = 1;
      scope.selectedRoleIdsToSupervise = [1, 2];

      scope.addSupervisoryRole();
      expect(scope.user.supervisorRoles).toEqual(undefined);
      expect(scope.showSupervisorRoleMappingError).toEqual(true);

      scope.selectedSupervisoryNodeIdToSupervise = 1;
      scope.selectedProgramIdToSupervise = null;
      scope.selectedRoleIdsToSupervise = [1, 2];

      scope.addSupervisoryRole();
      expect(scope.user.supervisorRoles).toEqual(undefined);
      expect(scope.showSupervisorRoleMappingError).toEqual(true);

      scope.selectedSupervisoryNodeIdToSupervise = 1;
      scope.selectedProgramIdToSupervise = 1;
      scope.selectedRoleIdsToSupervise = null;

      scope.addSupervisoryRole();
      expect(scope.user.supervisorRoles).toEqual(undefined);
      expect(scope.showSupervisorRoleMappingError).toEqual(true);
    });

    it("should get program name for given id from all programs list", function () {
      scope.programsMap = {pull: [
        {"id": 1, "name": "hiv"}
      ]};
      expect(scope.getProgramName(1)).toEqual("hiv");
    });

    it("should get supervisory node name for given id from all supervisory nodes list", function () {
      scope.supervisoryNodes = [
        {"id": 1, "name": "SN1"}
      ];
      expect(scope.getSupervisoryNodeName(1)).toEqual("SN1");
    });

    it("should delete home facility roles from the list", function () {
      scope.rowNum = 1;
      scope.deleteRoleList = "homeFacilityRoles";
      scope.user = {"homeFacilityRoles": [
        {"roleIds": [1]},
        {"roleIds": [1, 2]},
        {"roleIds": [3]}
      ]};
      scope.deleteRole(true);
      expect(scope.user.homeFacilityRoles).toEqual([
        {"roleIds": [1]},
        {"roleIds": [3]}
      ])
    });

    it("should delete supervisory roles from the list", function () {
      scope.rowNum = 1;
      scope.deleteRoleList = "supervisorRoles";
      scope.user = {"supervisorRoles": [
        {"roleIds": [1]},
        {"roleIds": [1, 2]},
        {"roleIds": [3]}
      ]};
      scope.deleteRole(true);
      expect(scope.user.supervisorRoles).toEqual([
        {"roleIds": [1]},
        {"roleIds": [3]}
      ])
    });

    it('should not load programs if delivery zone is undefined', function () {
      spyOn(scope, 'checkDeliveryZoneAndProgramDuplicity');
      scope.deliveryZoneRole = {deliveryZone: {id: undefined}};
      scope.loadProgramsForDeliveryZone();
      scope.$apply();
      $httpBackend.verifyNoOutstandingRequest();
      expect(scope.checkDeliveryZoneAndProgramDuplicity).toHaveBeenCalled();
    });

    it('should load programs for delivery zone', function () {
      scope.deliveryZoneRole = {deliveryZone: {id: 1}};
      var programs = [
        {id: 1, active: false}
      ];
      spyOn(scope, 'checkDeliveryZoneAndProgramDuplicity');
      $httpBackend.expect('GET', '/deliveryZones/1/programs.json').respond({deliveryZonePrograms: programs});
      scope.loadProgramsForDeliveryZone();
      scope.$apply();
      $httpBackend.flush();
      expect(scope.deliveryZonePrograms).toEqual([
        {id: 1, active: false, status: 'Inactive'}
      ]);
      expect(scope.checkDeliveryZoneAndProgramDuplicity).toHaveBeenCalled();
    });

    it('should not add allocation role without program id', function () {
      scope.user = {allocationRoles: []};
      validDZRole.programId = undefined;
      scope.deliveryZoneRole = validDZRole;
      scope.addAllocationRole();
      expect(scope.user.allocationRoles.length).toEqual(0);
    });

    it('should not add allocation role without delivery zone', function () {
      scope.user = {allocationRoles: []};
      validDZRole.deliveryZone.id = undefined;
      scope.deliveryZoneRole = validDZRole;
      scope.addAllocationRole();
      expect(scope.user.allocationRoles.length).toEqual(0);
    });

    it('should not add allocation role without role ids', function () {
      scope.user = {allocationRoles: []};
      validDZRole.roleIds = [];
      scope.deliveryZoneRole = validDZRole;
      scope.addAllocationRole();
      expect(scope.user.allocationRoles.length).toEqual(0);
    });

    it('should not add allocation role if role ids are undefined', function () {
      scope.user = {allocationRoles: []};
      validDZRole.roleIds = undefined;
      scope.deliveryZoneRole = validDZRole;
      scope.addAllocationRole();
      expect(scope.user.allocationRoles.length).toEqual(0);
    });

    it('should not add allocation role if duplicate', function () {
      scope.user = {allocationRoles: [
        {deliveryZone: {id: 1}, programId: 2}
      ]};
      validDZRole.deliveryZone.id = 1;
      validDZRole.programId = 2;
      scope.deliveryZoneRole = validDZRole;
      scope.addAllocationRole();
      expect(scope.user.allocationRoles.length).toEqual(1);
      expect(scope.duplicateAllocationRoleError).toEqual('error.delivery.zone.program.combination');
    });

    it('should add a valid allocation role', function () {
      scope.user = {allocationRoles: []};
      scope.deliveryZoneRole = validDZRole;
      scope.addAllocationRole();
      expect(scope.user.allocationRoles.length).toEqual(1);
      expect(scope.duplicateAllocationRoleError).toBeUndefined();
      expect(scope.deliveryZoneRole).toBeUndefined();
    });

    it('should return list of warehouses which are not already assigned', function () {
      scope.user = { fulfillmentRoles: [
        {
          facilityId: 1,
          roleIds: [1, 24, 6]
        },
        {
          facilityId: 2,
          roleIds: [1, 24, 6]
        }

      ]};

      scope.warehouses = [
        {id: 1},
        {id: 2},
        {id: 3}
      ];

      expect(scope.availableWarehouses()).toEqual([
        {id: 3}
      ]);
    });

    it('should add shipment role if all fields present', function () {
      scope.user.fulfillmentRoles = [
        {
          facilityId: 1,
          roleIds: [1, 24, 6]
        }
      ];
      scope.warehouseRole = {facilityId: 111, roleIds: [1, 2, 3]};
      scope.addFulfillmentRole();

      expect(scope.user.fulfillmentRoles.length).toEqual(2);
      expect(scope.user.fulfillmentRoles[1]).toEqual({facilityId: 111, roleIds: [1, 2, 3]});
    });

    it('should not add shipment role if warehouse not present', function () {
      scope.user.fulfillmentRoles = [
        {
          facilityId: 1,
          roleIds: [1, 24, 6]
        }
      ];
      scope.warehouseRole = {roleIds: [1, 2, 3]};
      scope.addFulfillmentRole();

      expect(scope.user.fulfillmentRoles.length).toEqual(1);
      expect(scope.warehouseRoleMappingError).toBeTruthy();
    });

    it('should not add shipment role if roles not present', function () {
      scope.user.fulfillmentRoles = [
        {
          facilityId: 1,
          roleIds: [1, 24, 6]
        }
      ];
      scope.warehouseRole = {facilityId: 111};
      scope.addFulfillmentRole();

      expect(scope.user.fulfillmentRoles.length).toEqual(1);
      expect(scope.warehouseRoleMappingError).toBeTruthy();
    });

    it('it should check presence of a error for a field', function(){
      expect(scope.hasMappingError(true, undefined)).toBeTruthy();
      expect(scope.hasMappingError(true, null)).toBeTruthy();
      expect(scope.hasMappingError(true, "")).toBeTruthy();
      expect(scope.hasMappingError(true, [])).toBeTruthy();

      var some_random_varible = "blah blah";
      expect(scope.hasMappingError(true, some_random_varible)).toBeFalsy();
    });

  });

});