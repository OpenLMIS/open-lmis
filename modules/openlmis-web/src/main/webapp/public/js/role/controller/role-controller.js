/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function RoleController($scope, $routeParams, $location, Roles, Rights, $dialog,
                        messageService) {
  $scope.$parent.error = "";
  $scope.$parent.message = "";
  $scope.role = {rights: []};

  if ($routeParams.id) {
    Roles.get({id: $routeParams.id}, function (data) {
      $scope.role = data.role;
      $scope.currentRightType = data.right_type;
      $scope.previousRightType = $scope.currentRightType;
    });
  }
  else {
    $scope.currentRightType = "ADMIN";
    $scope.previousRightType = $scope.currentRightType;
  }

  Rights.get({}, function (data) {
    $scope.rights = data.rights;
    $scope.adminRights = _.where($scope.rights, {"type": "ADMIN"});
    $scope.requisitionRights = _.where($scope.rights, {"type": "REQUISITION"});
    $scope.allocationRights = _.where($scope.rights, {"type": "ALLOCATION"});
    $scope.shipmentRights = _.where($scope.rights, {"type": "SHIPMENT"});
  }, {});


  $scope.updateRights = function (checked, right) {
    $scope.showRightError = false;

    if (checked) {
      if ($scope.contains(right.right)) return;

      $scope.role.rights.push(right);
      if (right.right == 'MANAGE_REPORT') {
        $scope.updateRights(true, $scope.getRightFromRightList("VIEW_REPORT"));
      }
      if (right.right == 'CREATE_REQUISITION' || right.right == 'AUTHORIZE_REQUISITION' ||
        right.right == 'APPROVE_REQUISITION') {
        $scope.updateRights(true, $scope.getRightFromRightList("VIEW_REQUISITION"));
      }
    } else {
      $scope.role.rights = $.grep($scope.role.rights, function (rightObj) {
        return (rightObj.right != right.right);
      });
    }
  };

  $scope.getRightFromRightList = function (rightName) {
    return _.find($scope.rights, function (right) {
      return right.right == rightName;
    });
  };

  $scope.areRelatedFieldsSelected = function (right) {
    if (right.right == 'VIEW_REQUISITION') {
      return ($scope.contains('CREATE_REQUISITION') ||
        $scope.contains('AUTHORIZE_REQUISITION') ||
        $scope.contains('APPROVE_REQUISITION'));
    }

    if (right.right == 'VIEW_REPORT') {
      return ($scope.contains('MANAGE_REPORT'));
    }
  };

  $scope.contains = function (right) {
    var containFlag = false;
    $($scope.role.rights).each(function (index, assignedRight) {
      if (assignedRight.right == right) {
        containFlag = true;
        return false;
      }
    });
    return containFlag;
  };


  $scope.saveRole = function () {
    var errorHandler = function (data) {
      $scope.error = data.data.error;
      $scope.message = "";
    };

    var successHandler = function (data) {
      $scope.$parent.message = data.success;
      $scope.$parent.$error = "";
      $location.path('list');
    };

    if (validRole()) {
      var id = $routeParams.id;
      if (id) {
        Roles.update({id: id}, $scope.role, successHandler, errorHandler);
      } else {
        Roles.save({}, $scope.role, successHandler, errorHandler);
      }
    }

  };


  $scope.dialogCloseCallback = function (result) {
    if (result) {
      $scope.role.rights = [];
      $scope.previousRightType = $scope.currentRightType;
    } else {
      $scope.currentRightType = $scope.previousRightType;
    }
  };

  $scope.showRoleTypeModal = function (selectedRoleType) {
    if (selectedRoleType == $scope.previousRightType) {
      return;
    } else {
      $scope.currentRightType = selectedRoleType;
      $scope.showRightError = false;
      $scope.error = "";
      var options = {
        id: "roleTypeDialog",
        header: messageService.get("header.change.roleType"),
        body: messageService.get("confirm.roleType.change")
      };
      OpenLmisDialog.newDialog(options, $scope.dialogCloseCallback, $dialog, messageService);
    }
  };

  var validRole = function () {
    var valid = true;
    $scope.error = "";
    $scope.showRightError = false;
    if ($scope.role.name == undefined) {
      $scope.showError = true;
      $scope.error = "form.error";
      valid = false;
    }
    if ($scope.role.rights.length == 0) {
      $scope.showRightError = true;
      $scope.error = "form.error";
      valid = false;
    }
    return valid;
  }

}
