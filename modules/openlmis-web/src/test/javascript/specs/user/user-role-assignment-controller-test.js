describe("User", function () {

  beforeEach(module('openlmis.services'));

  describe("User Role Assignment Controller", function () {

    var scope, $httpBackend, ctrl;

    beforeEach(inject(function ($rootScope, _$httpBackend_, $controller) {
      scope = $rootScope.$new();
      $httpBackend = _$httpBackend_;
      ctrl = $controller(UserRoleAssignmentController, {$scope:scope});
    }));

    it('should display only available programs in add dropdown', function () {
      scope.$parent = {allSupportedPrograms:[
        {"program":{id:1, name:'p1'}},
        {"program":{id:2, name:'p2'}},
        {"program":{id:3, name:'p3'}},
        {"program":{id:4, name:'p4'}}
      ]};

      var existingProgramsMappedForUser = [
        {programId:2},
        {programId:3}
      ];
      scope.user = {roleAssignments:existingProgramsMappedForUser};

      var availablePrograms = scope.availablePrograms();

      expect(availablePrograms).toEqual([
        {"program":{id:1, name:'p1'}},
        {"program":{id:4, name:'p4'}}
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
      expect(scope.user.roleAssignments).toEqual([
        {"roleIds":[1, 2], "programId":1}
      ]);
      expect(scope.showHomeFacilityRoleMappingError).toEqual(false);
    });

    it("should not add home facility role if any of program and role is not selected", function () {
      scope.user = {"id":123, "userName":"User420", "facilityId":1};
      scope.programSelected = null;
      scope.selectedRoleIds = [1, 2];
      scope.addHomeFacilityRole();
      expect(scope.user.roleAssignments).toEqual(undefined);
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
      scope.programs = [
        {"id":1, "name":"hiv"}
      ];
      expect(scope.getProgramName(1)).toEqual("hiv");
    });

    it("should get supervisory node name for given id from all supervisory nodes list", function () {
      scope.supervisoryNodes = [
        {"id":1, "name":"SN1"}
      ];
      expect(scope.getSupervisoryNodeName(1)).toEqual("SN1");
    });


  });

});