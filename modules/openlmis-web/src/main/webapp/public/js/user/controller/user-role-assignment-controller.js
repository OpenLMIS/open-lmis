/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function UserRoleAssignmentController($scope, $dialog, messageService, DeliveryZonePrograms) {

  $scope.selectSuperviseProgramMessage = messageService.get('label.select.program');
  $scope.selectSupervisoryNodeMessage = messageService.get('label.select.node');

  $("#adminRoles").on("change", function (e) {
    if (e.removed) {
      var dialogOpts = {
        id: "deleteAdminRolesModal",
        header: messageService.get("create.user.deleteAdminRoleHeader"),
        body: messageService.get("create.user.deleteAdminRoles")
      };
      OpenLmisDialog.newDialog(dialogOpts, $scope.restoreAdminRole, $dialog, messageService);

      window.lastAdminRoleRemoved = e.removed;
    }
  });

  $scope.restoreAdminRole = function (result) {
    if (result) return;

    if (window.lastAdminRoleRemoved) {
      $scope.user.adminRole.roleIds.push(window.lastAdminRoleRemoved.id);
    }
  };

  $scope.loadProgramsForDeliveryZone = function () {

    $scope.deliveryZonePrograms = $scope.deliveryZoneRoles = $scope.deliveryZoneRole.roleIds = [];
    $scope.deliveryZoneRole.programId = undefined;

    if (isUndefined($scope.deliveryZoneRole.deliveryZone.id)) return;

    DeliveryZonePrograms.get({zoneId: $scope.deliveryZoneRole.deliveryZone.id}, function (data) {
      $scope.deliveryZonePrograms = data.deliveryZonePrograms;
      $.each($scope.deliveryZonePrograms, function (index, program) {
        program.status = program.active ? messageService.get("label.active") : messageService.get('label.inactive');
      });
    }, function () {
    });
  };

  $scope.deleteCurrentRow = function (rowNum, roleList) {
    var dialogOpts = {
      id: "deleteRolesModal",
      header: messageService.get("create.user.deleteRoles"),
      body: roleList == 'allocationRoles' ? messageService.get('msg.roles.delivery.zone.deletion') : messageService.get("create.user.homeRoles.delete.warning")
    };

    OpenLmisDialog.newDialog(dialogOpts, $scope.deleteRole, $dialog, messageService);
    $scope.rowNum = rowNum;
    $scope.deleteRoleList = roleList;
  };

  $scope.deleteRole = function (result) {
    if (!result) return;

    $scope.user[$scope.deleteRoleList].splice($scope.rowNum, 1);
    $scope.rowNum = $scope.deleteRoleList = null;
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

    $scope.selectedProgramMessage = (programsToDisplay.length) ? messageService.get('label.select.program') : messageService.get('label.noProgramLeft');

    return programsToDisplay;
  };

  $scope.showHomeFacilityRoleAssignmentOptions = function () {
    return ($scope.user != null && $scope.user.facilityId != null)
  };

  $scope.addHomeFacilityRole = function () {
    if (isPresent($scope.programSelected) && isPresent($scope.selectedRoleIds)) {
      var newRoleAssignment = {programId: $scope.programSelected, roleIds: $scope.selectedRoleIds};
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
      var newRoleAssignment = {programId: $scope.selectedProgramIdToSupervise, supervisoryNode: {id: $scope.selectedSupervisoryNodeIdToSupervise}, roleIds: $scope.selectedRoleIdsToSupervise};
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

  function validate() {
    var valid = true;
    $($scope.user.allocationRoles).each(function (index, role) {
      if (role.deliveryZone.id == $scope.deliveryZoneRole.deliveryZone.id && role.programId == $scope.deliveryZoneRole.programId) {
        valid = false;
        return false;
      }
      return true;
    });
    return valid;
  }

  $scope.addAllocationRole = function () {
    $scope.user.allocationRoles = $scope.user.allocationRoles ? $scope.user.allocationRoles : [];
    $scope.showAllocationError = true;

    if (!$scope.deliveryZoneRole.deliveryZone.id || !$scope.deliveryZoneRole.programId || !$scope.deliveryZoneRole.roleIds || !$scope.deliveryZoneRole.roleIds.length) return;

    if (!validate()) {
      $scope.duplicateAllocationRoleError = 'error.delivery.zone.program.combination';
      return;
    }

    $scope.user.allocationRoles.push(angular.copy($scope.deliveryZoneRole));
    $scope.showAllocationError = $scope.deliveryZoneRole = undefined;
  };


  // WHO WROTE THIS? THIS IS AWESOME!
  $scope.getProgramName = function (programId) {
    return _.findWhere(_.flatten($scope.programsMap), {id: programId}).name;
  };

  $scope.getDeliveryZoneName = function (zoneId) {
    return _.findWhere($scope.deliveryZones, {id: zoneId}).name;
  };

  $scope.getSupervisoryNodeName = function (supervisoryNodeId) {
    return _.findWhere($scope.supervisoryNodes, {id: supervisoryNodeId}).name;
  };

  $scope.hasMappingError = function (mappingErrorFlag, field) {
    return mappingErrorFlag && !isPresent(field);
  };

  var isPresent = function (obj) {
    return obj != undefined && obj != "" && obj != [] && obj != null;
  };
}