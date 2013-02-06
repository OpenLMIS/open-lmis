function UserRoleAssignmentController($scope) {

  $scope.deleteCurrentRow = function (rowNum) {
    $scope.user.roleAssignments.splice(rowNum, 1);
  };

  $scope.availablePrograms = function () {
    var assignedProgramIds = _.map($scope.user.roleAssignments, function (roleAssignment) {
      return roleAssignment.programId;
    });

    return _.reject($scope.$parent.allSupportedPrograms, function (supportedProgram) {
      return _.contains(assignedProgramIds, supportedProgram.id);
    });
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
      $scope.showNewMappingError = true;
    }

    function clearCurrentSelection() {
      $scope.programSelected = null;
      $scope.selectedRoleIds = null;
      $scope.showNewMappingError = false;
    }

    function addRoleAssignment(newRoleAssignment) {
      if (!$scope.user.roleAssignments) {
        $scope.user.roleAssignments = [];
      }
      $scope.user.roleAssignments.push(newRoleAssignment);
    }
  };

  $scope.getProgramName = function (programId) {
    if (!$scope.$parent.allSupportedPrograms) return;
    var programName = null;
    $.each($scope.$parent.allSupportedPrograms, function (index, supportedProgram) {
      if (supportedProgram.id == programId) {
        programName = supportedProgram.name;
        return false;
      }
    });
    return programName;
  };

  $scope.hasError = function (field) {
    return $scope.showNewMappingError && !isPresent(field);
  };

  var isPresent = function (obj) {
    return obj != undefined && obj != "" && obj != [] && obj != null;
  };
}