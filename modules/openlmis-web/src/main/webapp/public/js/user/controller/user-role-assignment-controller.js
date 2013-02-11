function UserRoleAssignmentController($scope) {


  $scope.deleteCurrentRow = function (rowNum) {
    $scope.deleteRolesModal = true;
    $scope.rowNum = rowNum;
  };

  $scope.deleteRow = function () {
    $scope.user.roleAssignments.splice($scope.rowNum, 1);
    $scope.deleteRolesModal = false;
    $scope.rowNum = undefined;
  }

  $scope.availablePrograms = function () {
    var assignedProgramIds = _.map($scope.user.roleAssignments, function (roleAssignment) {
      return roleAssignment.programId;
    });

    var programsToDisplay = _.reject($scope.$parent.allSupportedPrograms, function (supportedProgram) {
      return _.contains(assignedProgramIds, supportedProgram.program.id);
    });

    $scope.selectedProgramMessage = (programsToDisplay.length) ? '--Select Program--' : '--No Program Left--';

    return programsToDisplay;
  };

  $scope.showRoleAssignmentOptions = function () {
    return ($scope.user != null && $scope.user.facilityId != null)
  };

  $scope.addRole = function () {
    if (isPresent($scope.programSelected) && isPresent($scope.selectedRoleIds)) {
      var newRoleAssignment = {programId:$scope.programSelected, roleIds:$scope.selectedRoleIds};
      addRoleAssignment(newRoleAssignment);
      clearCurrentSelection();
    } else {
      $scope.showHomeFacilityRoleMappingError = true;
    }

    function clearCurrentSelection() {
      $scope.programSelected = null;
      $scope.selectedRoleIds = null;
      $scope.showHomeFacilityRoleMappingError = false;
    }

    function addRoleAssignment(newRoleAssignment) {
      if (!$scope.user.roleAssignments) {
        $scope.user.roleAssignments = [];
      }
      $scope.user.roleAssignments.push(newRoleAssignment);
    }
  };

  $scope.addSupervisoryRole = function () {
    if (isPresent($scope.selectedProgramIdToSupervise) && isPresent($scope.selectedSupervisoryNodeIdToSupervise) && isPresent($scope.selectedRoleIdsToSupervise)) {
      var newRoleAssignment = {programId:$scope.selectedProgramIdToSupervise, supervisoryNode:{id:$scope.selectedSupervisoryNodeIdToSupervise}, roleIds:$scope.selectedRoleIdsToSupervise};
      addSupervisoryRole(newRoleAssignment);
      clearCurrentSelection();
    } else {
      $scope.showSupervisorRoleMappingError = true;
    }

    function clearCurrentSelection() {
      $scope.selectedProgramIdToSupervise = null;
      $scope.selectedSupervisoryNodeIdToSupervise = null;
      $scope.selectedRoleIdsToSupervise = null;
      $scope.showSupervisorRoleMappingError = false;
    }

    function addSupervisoryRole(newRoleAssignment) {
      if (!$scope.user.supervisorRoles) {
        $scope.user.supervisorRoles = [];
      }
      $scope.user.supervisorRoles.push(newRoleAssignment);
    }
  }

  $scope.getProgramName = function (programId) {
    if (!$scope.$parent.programs) return;
    var programName = null;
    $.each($scope.$parent.programs, function (index, program) {
      if (program.id == programId) {
        programName = program.name;
        return false;
      }
    });
    return programName;
  };

  $scope.getSupervisoryNodeName = function (supervisoryNodeId) {
    if (!$scope.$parent.supervisoryNodes) return;
    var supervisoryNodeName = null;
    $.each($scope.$parent.supervisoryNodes, function (index, supervisoryNode) {
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