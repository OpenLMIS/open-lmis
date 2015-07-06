/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function UserRoleAssignmentController($scope, $dialog, messageService, DeliveryZonePrograms) {
  $scope.programsToDisplay = [];
  $scope.selectSuperviseProgramMessage = 'label.select.program';
  $scope.selectSupervisoryNodeMessage = 'label.select.node';
  $scope.selectWarehouseMessage = 'label.select.warehouse';

  $("#adminRoles").on("change", function (e) {
    if (e.removed) {
      var dialogOpts = {
        id: "deleteAdminRolesModal",
        header: "create.user.deleteAdminRoleHeader",
        body: "create.user.deleteAdminRoles"
      };
      OpenLmisDialog.newDialog(dialogOpts, $scope.restoreAdminRole, $dialog);

      window.lastAdminRoleRemoved = e.removed;
    }
  });

  $("#reportRoles").on("change", function (e) {
        if (e.removed) {
            var dialogOpts = {
                id: "deleteReportRolesModal",
                header: messageService.get("create.user.deleteReportRoleHeader"),
                body: messageService.get("create.user.deleteReportRoles")
            };
            OpenLmisDialog.newDialog(dialogOpts, $scope.restoreReportRole, $dialog, messageService);

            window.lastReportRoleRemoved = e.removed;
        }
  });

    $scope.restoreReportRole = function (result) {
        if (result) return;

        if (window.lastReportRoleRemoved) {
            $scope.user.reportRoles.roleIds.push(window.lastReportRoleRemoved.id);
        }
    };

    $scope.restoreAdminRole = function (result) {
    if (result) return;

    if (window.lastAdminRoleRemoved) {
      $scope.user.adminRole.roleIds.push(window.lastAdminRoleRemoved.id);
    }
  };

  $scope.loadProgramsForDeliveryZone = function () {

    $scope.deliveryZonePrograms = $scope.deliveryZoneRoles = [];
    $scope.deliveryZoneRole.programId = undefined;
    $scope.checkDeliveryZoneAndProgramDuplicity();

    if (isUndefined($scope.deliveryZoneRole.deliveryZone.id)) return;

    DeliveryZonePrograms.get({zoneId: $scope.deliveryZoneRole.deliveryZone.id}, function (data) {
      $scope.deliveryZonePrograms = data.deliveryZonePrograms;
      $.each($scope.deliveryZonePrograms, function (index, program) {
        program.status = program.active ? messageService.get("label.active") : messageService.get('label.inactive');
      });
    }, function () {
    });
  };

  function getBodyMsgKey(roleList) {
    if (roleList == 'fulfillmentRoles') {
      return 'msg.roles.fulfillment.delete.warning';
    }
    else {
      return roleList == 'allocationRoles' ? 'msg.roles.delivery.zone.deletion' : "create.user.homeRoles.delete.warning";
    }
  }

  $scope.deleteCurrentRow = function (rowNum, roleList) {
    var dialogOpts = {
      id: "deleteRolesModal",
      header: "create.user.deleteRoles",
      body: getBodyMsgKey(roleList)
    };

    OpenLmisDialog.newDialog(dialogOpts, $scope.deleteRole, $dialog);
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

    $scope.programsToDisplay = programsToDisplay;
  };

  $scope.availableWarehouses = function () {
    var assignedWarehouseId = _.map($scope.user.fulfillmentRoles, function (role) {
      return role.facilityId;
    });
    return _.reject($scope.warehouses, function (warehouse) {
      return _.contains(assignedWarehouseId, warehouse.id);
    });
  };

  $scope.showHomeFacilityRoleAssignmentOptions = function () {
    var showHomeFacilityAccordion =  ($scope.user !== null && $scope.user.facilityId !== null);
    if(showHomeFacilityAccordion) {
      $scope.availableSupportedProgramsWithStatus();
    }
    return showHomeFacilityAccordion;
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

  $scope.checkSupervisoryRolesDuplicity = function () {
    var result = _.find($scope.user.supervisorRoles, function (roleAssignment) {
      return (roleAssignment.programId === $scope.selectedProgramIdToSupervise &&
        roleAssignment.supervisoryNode.id === $scope.selectedSupervisoryNodeIdToSupervise);
    });

    if (result) {
      $scope.duplicateSupervisorRoleError = messageService.get('error.duplicate.programNode.combination');
      return true;
    }
    $scope.duplicateSupervisorRoleError = undefined;
    return false;
  };

  $scope.addSupervisoryRole = function () {
    if (isPresent($scope.selectedProgramIdToSupervise) && isPresent($scope.selectedSupervisoryNodeIdToSupervise) &&
        isPresent($scope.selectedRoleIdsToSupervise))
    {
      var newRoleAssignment = {programId: $scope.selectedProgramIdToSupervise,
        supervisoryNode: {id: $scope.selectedSupervisoryNodeIdToSupervise},
        roleIds: $scope.selectedRoleIdsToSupervise};
      if ($scope.checkSupervisoryRolesDuplicity()) {
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

    function addSupervisoryRole(newRoleAssignment) {
      if (!$scope.user.supervisorRoles) {
        $scope.user.supervisorRoles = [];
      }
      $scope.user.supervisorRoles.push(newRoleAssignment);
    }
  };

  function validate() {
    var valid = true;
    $($scope.user.allocationRoles).each(function (index, role) {
      if (role.deliveryZone.id === $scope.deliveryZoneRole.deliveryZone.id &&
        role.programId === $scope.deliveryZoneRole.programId) {
        valid = false;
        return false;
      }
      return true;
    });
    return valid;
  }

  $scope.checkDeliveryZoneAndProgramDuplicity = function () {
    if (!validate()) {
      $scope.duplicateAllocationRoleError = 'error.delivery.zone.program.combination';
      return true;
    }
    $scope.duplicateAllocationRoleError = undefined;
    return false;
  };

  $scope.addAllocationRole = function () {
    $scope.user.allocationRoles = $scope.user.allocationRoles ? $scope.user.allocationRoles : [];
    $scope.showAllocationError = true;

    if (!$scope.deliveryZoneRole.deliveryZone.id || !$scope.deliveryZoneRole.programId ||
      !$scope.deliveryZoneRole.roleIds || !$scope.deliveryZoneRole.roleIds.length)
    {
      return;
    }

    if ($scope.checkDeliveryZoneAndProgramDuplicity()) {
      return;
    }

    $scope.user.allocationRoles.push(angular.copy($scope.deliveryZoneRole));
    $scope.showAllocationError = $scope.deliveryZoneRole = $scope.duplicateAllocationRoleError = undefined;
  };

  $scope.addFulfillmentRole = function () {
    $scope.user.fulfillmentRoles = $scope.user.fulfillmentRoles ? $scope.user.fulfillmentRoles : [];
    if (!$scope.warehouseRole || !isPresent($scope.warehouseRole.facilityId) || !isPresent($scope.warehouseRole.roleIds)) {
      $scope.warehouseRoleMappingError = true;
      return;
    }

    $scope.user.fulfillmentRoles.push(angular.copy($scope.warehouseRole));
      $scope.warehouseRole = null;
      $scope.warehouseRoleMappingError = false;
  };

  $scope.getProgramName = function (programId) {
    return _.findWhere(_.flatten(_.values($scope.programsMap)), {id: programId}).name;
  };

  $scope.getDeliveryZoneName = function (zoneId) {
    return _.findWhere($scope.deliveryZones, {id: zoneId}).name;
  };

  $scope.getSupervisoryNodeName = function (supervisoryNodeId) {
    return _.findWhere($scope.supervisoryNodes, {id: supervisoryNodeId}).name;
  };

  $scope.getWarehouseName = function (facilityId) {
    return _.findWhere($scope.warehouses, {id: facilityId}).name;
  };

  $scope.hasMappingError = function (mappingErrorFlag, field) {
    return mappingErrorFlag && !isPresent(field);
  };

  var isPresent = function (obj) {
    return obj !== undefined && obj !== null && obj !== "" && !(obj instanceof Array && obj.length === 0) ;
  };
}