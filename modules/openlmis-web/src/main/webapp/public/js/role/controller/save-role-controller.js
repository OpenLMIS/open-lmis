function SaveRoleController($scope, $routeParams,$location, Roles, Role, Rights) {
  $scope.$parent.error = "";
  $scope.$parent.message = "";

  if ($routeParams.id) {
    Role.get({id:$routeParams.id}, function (data) {
      $scope.role = data.role;
    });
  } else {
    $scope.role = {rights:[]};
  }

  Rights.get({}, function (data) {
    $scope.rights = data.rights;
  }, {});

  $scope.updateRights = function (checked, rightToUpdate) {
    if (checked == true) {
      $scope.role.rights.push(rightToUpdate);
    } else {
      deleteRight(rightToUpdate);
    }
  };

  function deleteRight(rightToUpdate) {
    $.each($scope.role.rights, function (index, right) {
      if (rightToUpdate == right) {
        $scope.role.rights.splice(index, 1);
      }
    });
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
    };

    if ($scope.roleForm.$invalid) {
      $scope.showError = true;
      $scope.error = "Please correct the errors";
    } else {
      var id = $routeParams.id;
      if (id) {
        Role.update({id:id}, $scope.role, successHandler, errorHandler);
        $location.path('list');
      } else {
        Roles.save({}, $scope.role, successHandler, errorHandler);
        $location.path('list');
      }
    }
  }
}
