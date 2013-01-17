function UserController($scope, Users) {

  $scope.saveUser = function () {

      Users.save({}, $scope.user, function (data) {
        $scope.user = data.user;
        $scope.showError = false;
        $scope.error = "";
        $scope.message = data.success;
      }, function (data) {
        $scope.showError = true;
        $scope.message = "";
        $scope.error = data.data.error;
      });
    };
}