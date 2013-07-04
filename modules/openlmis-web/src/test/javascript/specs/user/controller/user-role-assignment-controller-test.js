/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

describe("User", function () {

  beforeEach(module('openlmis.services'));
  beforeEach(module('ui.bootstrap.dialog'));
  beforeEach(module('openlmis.localStorage'));

  describe("User Role Assignment Controller", function () {

    var scope, $httpBackend, ctrl,messageService;

    beforeEach(inject(function ($rootScope, _$httpBackend_, $controller,_messageService_) {

      messageService = _messageService_;

      spyOn(messageService, 'get').andCallFake(function (value) {
        if (value == 'label.active') return "Active"
        if (value == 'label.inactive') return "Inactive"
      })
      scope = $rootScope.$new();
      $httpBackend = _$httpBackend_;
      ctrl = $controller(UserRoleAssignmentController, {$scope:scope});
    }));

    it('should display only available programs in add dropdown', function () {
      scope.$parent = {allSupportedPrograms:[
        {"program":{id:1, name:'p1', active:true}},
        {"program":{id:2, name:'p2', active:true}},
        {"program":{id:3, name:'p3', active:true}},
        {"program":{id:4, name:'p4', active:false}}
      ]};

      var existingProgramsMappedForUser = [
        {programId:2},
        {programId:3}
      ];
      scope.user = {homeFacilityRoles:existingProgramsMappedForUser};

      var availablePrograms = scope.availableSupportedProgramsWithStatus();

      expect(availablePrograms).toEqual([
        {"program":{id:1, name:'p1', status:"Active", active:true}},
        {"program":{id:4, name:'p4', status:"Inactive", active:false}}
      ]);
    });

    it("should display home facility role assignment options if user and facility not null", function () {
      scope.user = {"id":123, "userName":"User420", "facilityId":1};
      expect(scope.showHomeFacilityRoleAssignmentOptions()).toEqual(true);
    });

    it("should add home facility role if facility program and role is selected", function () {
      scope.user = {"id":123, "userName":"User420", "facilityId":1};
      scope.programSelected = 1;
      scope.selectedRoleIds = [1, 2];
      scope.addHomeFacilityRole();
      expect(scope.user.homeFacilityRoles).toEqual([
        {"roleIds":[1, 2], "programId":1}
      ]);
      expect(scope.showHomeFacilityRoleMappingError).toEqual(false);
    });

    it("should not add home facility role if any of program and role is not selected", function () {
      scope.user = {"id":123, "userName":"User420", "facilityId":1};
      scope.programSelected = null;
      scope.selectedRoleIds = [1, 2];
      scope.addHomeFacilityRole();
      expect(scope.user.homeFacilityRoles).toEqual(undefined);
      expect(scope.showHomeFacilityRoleMappingError).toEqual(true);
    });

    it("should add supervisor role if program, supervisory node and role is selected", function () {
      scope.user = {"id":123, "userName":"User420", "facilityId":1};
      scope.selectedSupervisoryNodeIdToSupervise = 1;
      scope.selectedProgramIdToSupervise = 1;
      scope.selectedRoleIdsToSupervise = [1, 2];
      scope.addSupervisoryRole();
      expect(scope.user.supervisorRoles).toEqual([
        {"roleIds":[1, 2], "programId":1, "supervisoryNode":{"id":1}}
      ]);
      expect(scope.showSupervisorRoleMappingError).toEqual(false);
    });

    it("should not add home facility role if any of program, supervisory node and role is not selected", function () {
      scope.user = {"id":123, "userName":"User420", "facilityId":1};

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
        {"id":1, "name":"hiv"}
      ]};
      expect(scope.getProgramName(1)).toEqual("hiv");
    });

    it("should get supervisory node name for given id from all supervisory nodes list", function () {
      scope.supervisoryNodes = [
        {"id":1, "name":"SN1"}
      ];
      expect(scope.getSupervisoryNodeName(1)).toEqual("SN1");
    });

    it("should delete home facility roles from the list", function () {
      scope.rowNum = 1;
      scope.deleteRoleList = "homeFacilityRoles";
      scope.user = {"homeFacilityRoles": [{"roleIds":[1]},{"roleIds":[1,2]},{"roleIds":[3]}]};
      scope.deleteRole(true);
      expect(scope.user.homeFacilityRoles).toEqual([{"roleIds":[1]},{"roleIds":[3]}])
    });

    it("should delete supervisory roles from the list", function () {
      scope.rowNum = 1;
      scope.deleteRoleList = "supervisorRoles";
      scope.user = {"supervisorRoles": [{"roleIds":[1]},{"roleIds":[1,2]},{"roleIds":[3]}]};
      scope.deleteRole(true);
      expect(scope.user.supervisorRoles).toEqual([{"roleIds":[1]},{"roleIds":[3]}])
    });

  });

});