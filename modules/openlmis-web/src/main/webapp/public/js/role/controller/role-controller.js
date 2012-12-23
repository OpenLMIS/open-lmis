function RoleController($scope, Role, Rights) {

  $scope.role = {rights:[]};

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

  $scope.saveRole = function () {
    if ($scope.roleForm.$invalid) {
      $scope.showError = true;
      $scope.error = "Please correct the errors";
    }
    else {
      Role.save({}, $scope.role, function (data) {
        $scope.message = data.success;
        $scope.error = "";
      }, function (data) {
        $scope.error = data.data.error;
        $scope.message = "";
      });
    }
  }
}
