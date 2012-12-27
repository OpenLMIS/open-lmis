function SaveRoleController($scope, CreateRole, Role, Rights, $routeParams) {

  if ($routeParams.id) {
    Role.get({id:$routeParams.id}, function (data) {
      $scope.role = data.openLmisResponse.responseData;
    });
  } else {
    $scope.role = {rights:[]};
  }

  Rights.get({}, function (data) {
    $scope.rights = data.rightList;
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
    })
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
    if ($scope.roleForm.$invalid) {
      $scope.showError = true;
      $scope.error = "Please correct the errors";
    }
    else {
      CreateRole.save({}, $scope.role, function (data) {
        $scope.message = data.success;
        $scope.error = "";
      }, function (data) {
        $scope.error = data.data.error;
        $scope.message = "";
      });
    }
  }
}
