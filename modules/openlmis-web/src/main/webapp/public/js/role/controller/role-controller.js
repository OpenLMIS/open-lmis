function RoleController($scope, rights, Role) {

  $scope.rights = rights;
  $scope.role = {rights:[]};
  $scope.error = '';
  $scope.showError = '';

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

RoleController.resolve = {
  rights:function ($q, Rights, $location, $route, $timeout) {
    var deferred = $q.defer();
    var code = $route.current.params.programCode;

    $timeout(function () {
      Rights.get({}, function (data) {
        deferred.resolve(data.rightList);
      }, {});
    }, 100);
    return deferred.promise;
  }
};