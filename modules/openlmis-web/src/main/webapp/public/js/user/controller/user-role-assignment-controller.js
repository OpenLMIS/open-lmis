/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function UserRoleAssignmentController($scope, $dialog, messageService) {

  $scope.selectSuperviseProgramMessage = '--Select Program--';
  $scope.selectSupervisoryNodeMessage = '--Select Node--';

  $("#adminRoles").on("change", function (e) {
    if (e.removed) {
      var dialogOpts = {
        id:"deleteAdminRolesModal",
        header: messageService.get("create.user.deleteAdminRoleHeader"),
        body: messageService.get("create.user.deleteAdminRoles")
      };
      OpenLmisDialog.newDialog(dialogOpts, $scope.restoreAdminRole, $dialog);

      window.lastAdminRoleRemoved = e.removed;
    }
  });

  $scope.restoreAdminRole = function (result) {
    if(!result) {
      if (window.lastAdminRoleRemoved) {
        $scope.user.adminRole.roleIds.push(window.lastAdminRoleRemoved.id);
      }
    }
    else {
      return;
    }
  };

  $scope.deleteCurrentRow = function (rowNum, supervisoryRole) {
    var dialogOpts = {
      id:"deleteRolesModal",
      header: messageService.get("create.user.deleteRoles"),
      body: messageService.get("create.user.homeRoles.delete.warning")
    };

    OpenLmisDialog.newDialog(dialogOpts, $scope.deleteFacilityRole, $dialog);
    $scope.rowNum = rowNum;
    $scope.supervisorRole =  supervisoryRole ? true : false;
  };

  $scope.deleteFacilityRole = function (result) {
    if(!result) return;

    var rolesArray = $scope.supervisorRole? $scope.user.supervisorRoles : $scope.user.homeFacilityRoles;

    rolesArray.splice($scope.rowNum, 1);
    $scope.rowNum = $scope.supervisorRole = null;
  };

  $scope.availableSupportedProgramsWithStatus = function () {
    var assignedProgramIds = _.map($scope.user.homeFacilityRoles, function (roleAssignment) {
      return roleAssignment.programId;
    });

    var programsToDisplay = _.reject($scope.$parent.allSupportedPrograms, function (supportedProgram) {
      return _.contains(assignedProgramIds, supportedProgram.program.id);
    });

    $.each(programsToDisplay, function (index, program) {
      program.program.status = program.program.active ? messageService.get('label.active') : messageService.get('label.inactive');
    });

    $scope.selectedProgramMessage = (programsToDisplay.length) ? messageService.get('label.selectProgram') : messageService.get('label.noProgramLeft') ;

    return programsToDisplay;
  };

  $scope.showHomeFacilityRoleAssignmentOptions = function () {
    return ($scope.user != null && $scope.user.facilityId != null)
  };

  $scope.addHomeFacilityRole = function () {
    if (isPresent($scope.programSelected) && isPresent($scope.selectedRoleIds)) {
      var newRoleAssignment = {programId:$scope.programSelected, roleIds:$scope.selectedRoleIds};
      addHomeFacilityRole(newRoleAssignment);
      clearCurrentSelection();
    } else {
      $scope.$parent.showHomeFacilityRoleMappingError = true;
    }

    function clearCurrentSelection() {
      $scope.programSelected = null;
      $scope.selectedRoleIds = null;
      $scope.$parent.showHomeFacilityRoleMappingError = false;
    }

    function addHomeFacilityRole(newRoleAssignment) {
      if (!$scope.user.homeFacilityRoles) {
        $scope.user.homeFacilityRoles = [];
      }
      $scope.user.homeFacilityRoles.push(newRoleAssignment);
    }
  };

  $scope.addSupervisoryRole = function () {
    if (isPresent($scope.selectedProgramIdToSupervise) && isPresent($scope.selectedSupervisoryNodeIdToSupervise) && isPresent($scope.selectedRoleIdsToSupervise)) {
      var newRoleAssignment = {programId:$scope.selectedProgramIdToSupervise, supervisoryNode:{id:$scope.selectedSupervisoryNodeIdToSupervise}, roleIds:$scope.selectedRoleIdsToSupervise};
      if (isDuplicateSupervisoryRole(newRoleAssignment)) {
        $scope.duplicateSupervisorRoleError = messageService.get('error.duplicate.programNode.combination');
        return;
      }
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
      $scope.duplicateSupervisorRoleError = undefined;
    }

    function isDuplicateSupervisoryRole(newRoleAssignment) {
      var result = _.find($scope.user.supervisorRoles, function (roleAssignment) {
        return (roleAssignment.programId == newRoleAssignment.programId && roleAssignment.supervisoryNode.id == newRoleAssignment.supervisoryNode.id)
      });

      if (result)return true;

      return false;
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