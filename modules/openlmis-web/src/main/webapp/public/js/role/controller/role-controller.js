/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function RoleController($scope, $routeParams, $location, Roles, Role, Rights, $dialog, messageService) {
  $scope.$parent.error = "";
  $scope.$parent.message = "";
  $scope.role = {rights: []};

  if ($routeParams.id) {
    Role.get({id: $routeParams.id}, function (data) {
      $scope.role = data.role;
      $scope.role.type = data.role.type;
      $scope.previousType = $scope.role.type;
    });
  }
  else {
    $scope.role.type = "ADMIN";
    $scope.previousType = $scope.role.type;
  }

  Rights.get({}, function (data) {
    $scope.rights = data.rights;
    $scope.adminRights = _.where($scope.rights, {"type": "ADMIN"});
    $scope.requisitionRights = _.where($scope.rights, {"type": "REQUISITION"});
    $scope.allocationRights = _.where($scope.rights, {"type": "ALLOCATION"});
  }, {});


  $scope.updateRights = function (checked, right) {
    $scope.showRightError = false;

    if (checked) {
      if ($scope.contains(right.right)) return;

      $scope.role.rights.push(right);
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
    if (right.right != 'VIEW_REQUISITION') return false;
    return ($scope.contains('CREATE_REQUISITION') ||
      $scope.contains('AUTHORIZE_REQUISITION') ||
      $scope.contains('APPROVE_REQUISITION'));
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
        Role.update({id: id}, $scope.role, successHandler, errorHandler);
      } else {
        Roles.save({}, $scope.role, successHandler, errorHandler);
      }
    }

  };


  $scope.dialogCloseCallback = function (result) {
    if (result) {
      $scope.role.rights = [];
      $scope.showRightError = false;
      $scope.showError = false;
      $scope.previousType = $scope.role.type;
    } else {
      $scope.role.type = $scope.previousType;
    }
  };

  $scope.showRoleTypeModal = function () {
    var options = {
      id: "roleTypeDialog",
      header: messageService.get("header.change.roleType"),
      body: messageService.get("confirm.roleType.change")
    };
    OpenLmisDialog.newDialog(options, $scope.dialogCloseCallback, $dialog, messageService);
  };

  var validRole = function () {
    var valid = true;
    $scope.showError = false;
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
