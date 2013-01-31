function ResetPasswordController($scope, $routeParams, $location, UpdateUserPassword) {

  $scope.resetPassword = function () {
    if ($scope.password1 != $scope.password2) {
      $scope.error = "passwords should match";
    } else {
      $scope.error ="";
    }

  }
}
