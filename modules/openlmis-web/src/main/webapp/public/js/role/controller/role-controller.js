function RoleController($scope, $routeParams, $location, Roles, Role, Rights) {
  $scope.$parent.error = "";
  $scope.$parent.message = "";
  $scope.role = {rights: []};
  $scope.role.adminRole = "true";

  if ($routeParams.id) {
    Role.get({id: $routeParams.id}, function (data) {
      $scope.role = data.role;
      $scope.role.adminRole = data.role.adminRole.toString();
    });
  }

  Rights.get({}, function (data) {
    $scope.rights = data.rights;
    $scope.adminRights = _.where($scope.rights, {"adminRight": "true"});
    $scope.nonAdminRights = _.where($scope.rights, {"adminRight": "false"});
  }, {});


  $scope.updateRights = function (checked, right) {
    if (checked) {
      if ($scope.contains(right.right))
        return;

      $scope.role.rights.push(right);
      if (right.right == 'CREATE_REQUISITION' || right.right == 'AUTHORIZE_REQUISITION' ||
        right.right == 'APPROVE_REQUISITION' || right.right == 'CONVERT_TO_ORDER') {
        $scope.updateRights(true, $scope.getRightFromRightList("VIEW_REQUISITION"));
      }
    } else {
      $scope.role.rights = $.grep($scope.role.rights, function (rightObj) {
        return (rightObj.right != right.right);
      });
    }
  }

  $scope.getRightFromRightList = function (rightName) {
    return _.find($scope.rights, function (right) {
      return right.right == rightName;
    });
  }

  $scope.areRelatedFieldsSelected = function (right) {
    if (right.right != 'VIEW_REQUISITION') return false;
    return ($scope.contains('CREATE_REQUISITION') ||
      $scope.contains('AUTHORIZE_REQUISITION') ||
      $scope.contains('APPROVE_REQUISITION') ||
      $scope.contains('CONVERT_TO_ORDER'));
  }

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

  }
  $scope.showRoleTypeModal = function (selected) {
    window.selected = selected;
    $scope.roleTypeModal = true;
  }

  $scope.deSelectRights = function () {
    $scope.role.rights = [];
    $scope.disableSection = "disableSection";
    $scope.role.adminRole = window.selected.toString();
    $scope.roleTypeModal = false;
  }

  $scope.cancel = function () {
    $scope.roleTypeModal = false;
    $scope.role.adminRole= (!window.selected).toString();
  }

  var validRole = function () {
    var valid = true;
    $scope.showError = false;
    $scope.showRightError = false;
    if ($scope.role.name == undefined) {
      $scope.showError = true;
      valid = false;
    }
    if ($scope.role.rights.length == 0) {
      $scope.showRightError = true;
      valid = false;
    }
    return valid;
  }

}
