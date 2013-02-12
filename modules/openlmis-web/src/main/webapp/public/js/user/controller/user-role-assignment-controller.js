function UserRoleAssignmentController($scope) {

  $scope.selectSuperviseProgramMessage = '--Select Program--';
  $scope.selectSupervisoryNodeMessage = '--Select Node--';
  $scope.deleteCurrentRow = function (rowNum) {
    $scope.deleteRolesModal = true;
    $scope.rowNum = rowNum;
  };

  $scope.deleteRow = function () {
    $scope.user.homeFacilityRoles.splice($scope.rowNum, 1);
    $scope.deleteRolesModal = false;
    $scope.rowNum = undefined;
  }

  $scope.availablePrograms = function () {
    var assignedProgramIds = _.map($scope.user.homeFacilityRoles, function (roleAssignment) {
      return roleAssignment.programId;
    });

    var programsToDisplay = _.reject($scope.$parent.allSupportedPrograms, function (supportedProgram) {
      return _.contains(assignedProgramIds, supportedProgram.program.id);
    });

    $scope.selectedProgramMessage = (programsToDisplay.length) ? '--Select Program--' : '--No Program Left--';

    return programsToDisplay;
  };

  $scope.showHomeFacilityRoleAssignmentOptions = function () {
    return ($scope.user != null && $scope.user.facilityId != null)
  };

  $scope.addHomeFacilityRole = function () {
    if (isPresent($scope.programSelected) && isPresent($scope.selectedRoleIds)) {
      var newRoleAssignment = {programId:$scope.programSelected, roleIds:$scope.selectedRoleIds};
      addRoleAssignment(newRoleAssignment);
      clearCurrentSelection();
    } else {
      $scope.$parent.showHomeFacilityRoleMappingError = true;
    }

    function clearCurrentSelection() {
      $scope.programSelected = null;
      $scope.selectedRoleIds = null;
      $scope.$parent.showHomeFacilityRoleMappingError = false;
    }

    function addRoleAssignment(newRoleAssignment) {
      if (!$scope.user.homeFacilityRoles) {
        $scope.user.homeFacilityRoles = [];
      }
      $scope.user.homeFacilityRoles.push(newRoleAssignment);
    }
  };

  $scope.addSupervisoryRole = function () {
    if (isPresent($scope.selectedProgramIdToSupervise) && isPresent($scope.selectedSupervisoryNodeIdToSupervise) && isPresent($scope.selectedRoleIdsToSupervise)) {
      var newRoleAssignment = {programId:$scope.selectedProgramIdToSupervise, supervisoryNode:{id:$scope.selectedSupervisoryNodeIdToSupervise}, roleIds:$scope.selectedRoleIdsToSupervise};
      addSupervisoryRole(newRoleAssignment);
      clearCurrentSelection();
    } else {
      $scope.$parent.showSupervisorRoleMappingError = true;
    }

    function clearCurrentSelection() {
      $scope.selectedProgramIdToSupervise = null;
      $scope.selectedSupervisoryNodeIdToSupervise = null;
      $scope.selectedRoleIdsToSupervise = null;
      $scope.$parent.showSupervisorRoleMappingError = false;
    }

    function addSupervisoryRole(newRoleAssignment) {
      if (!$scope.user.supervisorRoles) {
        $scope.user.supervisorRoles = [];
      }
      $scope.user.supervisorRoles.push(newRoleAssignment);
    }
  }

  $scope.getProgramName = function (programId) {
    if (!$scope.programs) return;
    var programName = null;
    $.each($scope.programs, function (index, program) {
      if (program.id == programId) {
        programName = program.name;
        return false;
      }
    });
    return programName;
  };

  $scope.getSupervisoryNodeName = function (supervisoryNodeId) {
    if (!$scope.supervisoryNodes) return;
    var supervisoryNodeName = null;
    $.each($scope.supervisoryNodes, function (index, supervisoryNode) {
      if (supervisoryNode.id == supervisoryNodeId) {
        supervisoryNodeName = supervisoryNode.name;
        return false;
      }
    });
    return supervisoryNodeName;
  };

  $scope.hasMappingError = function (mappingErrorFlag, field) {
    return mappingErrorFlag && !isPresent(field);
  };

  var isPresent = function (obj) {
    return obj != undefined && obj != "" && obj != [] && obj != null;
  };
}