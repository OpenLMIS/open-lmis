/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

function RoleController($scope, $routeParams, $location, Roles, Rights, $dialog) {
  $scope.$parent.error = "";
  $scope.$parent.message = "";
  $scope.role = {rights: []};

  function getRole() {
    if ($routeParams.id) {
      Roles.get({id: $routeParams.id}, function (data) {
        $scope.role = data.role;
        $scope.currentRightType = data.right_type;
        $scope.previousRightType = $scope.currentRightType;
        if ($scope.role.rights) {
          _.each($scope.rights, function (right) {
            _.find($scope.role.rights, function (role_right) {
              if (role_right.name === right.name) {
                right.selected = true;
              }
            });
          });
        }
      });
    }
    else {
      $scope.currentRightType = "ADMIN";
      $scope.previousRightType = $scope.currentRightType;
    }
  }

  Rights.get({}, function (data) {
    $scope.rights = data.rights;
    $scope.adminRights = _.where($scope.rights, {"type": "ADMIN"});
    $scope.requisitionRights = _.where($scope.rights, {"type": "REQUISITION"});
    $scope.allocationRights = _.where($scope.rights, {"type": "ALLOCATION"});
    $scope.fulfillmentRights = _.where($scope.rights, {"type": "FULFILLMENT"});
    $scope.reportingRights = _.where($scope.rights, {"type": "REPORTING"});
    $scope.reportRights = _.where($scope.rights, {"type": "REPORT"});
    getRole();
  }, {});

  function resetSelected() {
    _.each($scope.rights, function (right) {
      right.selected = false;
    });
  }

  function addDefaultRight(defaultRightName) {
    var rightExists = _.find($scope.role.rights, function (role_right) {
      return role_right.name === defaultRightName;
    });
    if (rightExists) return;
    var defaultRight = $scope.getRightFromRightList(defaultRightName);
    defaultRight.selected = true;
    $scope.role.rights.push(defaultRight);
  }

  $scope.updateRights = function (right) {
    $scope.showRightError = false;
    right.selected = !right.selected;
    if (right.selected) {
      $scope.role.rights.push(right);

      if (right.name == 'CREATE_REQUISITION' || right.name == 'AUTHORIZE_REQUISITION' || right.name == 'APPROVE_REQUISITION') {
        addDefaultRight("VIEW_REQUISITION");
      }
      if (right.name == 'CONVERT_TO_ORDER' || right.name == 'MANAGE_POD' || right.name == 'FACILITY_FILL_SHIPMENT') {
        addDefaultRight("VIEW_ORDER");
      }
    } else {
      $scope.role.rights = _.filter($scope.role.rights, function (rightObj) {
        return (rightObj.name != right.name);
      });
    }
  };

  $scope.getRightFromRightList = function (rightName) {
    return _.find($scope.rights, function (right) {
      return right.name == rightName;
    });
  };

  $scope.areRelatedFieldsSelected = function (right) {
    if (right.name == 'VIEW_REQUISITION') {
      return (contains('CREATE_REQUISITION') ||
        contains('AUTHORIZE_REQUISITION') ||
        contains('APPROVE_REQUISITION'));
    }
    if (right.name == 'VIEW_ORDER') {
      return (contains('CONVERT_TO_ORDER') ||
        contains('MANAGE_POD') ||
        contains('FACILITY_FILL_SHIPMENT'));
    }
  };

  function contains(right) {
    var containFlag = false;
    $($scope.role.rights).each(function (index, assignedRight) {
      if (assignedRight.name == right) {
        containFlag = true;
        return false;
      }
    });
    return containFlag;
  }

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
      resetSelected();
      $scope.previousRightType = $scope.currentRightType;
    } else {
      $scope.currentRightType = $scope.previousRightType;
    }
  };

  $scope.showRoleTypeModal = function (selectedRoleType) {
    if (selectedRoleType !== $scope.previousRightType) {
      $scope.currentRightType = selectedRoleType;
      $scope.showRightError = false;
      $scope.error = "";
      var options = {
        id: "roleTypeDialog",
        header: "header.change.roleType",
        body: "confirm.roleType.change"
      };
      OpenLmisDialog.newDialog(options, $scope.dialogCloseCallback, $dialog);
    }
  };

  var validRole = function () {
    var valid = true;
    $scope.error = "";
    $scope.showRightError = false;
    if ($scope.role.name === undefined) {
      $scope.showError = true;
      $scope.error = "form.error";
      valid = false;
    }
    if ($scope.role.rights.length === 0) {
      $scope.showRightError = true;
      $scope.error = "form.error";
      valid = false;
    }
    return valid;
  };
}
