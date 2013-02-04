function ResetPasswordController($scope, UpdateUserPassword, $location, $route) {

  $scope.resetPassword = function () {
    var reWhiteSpace = new RegExp("\\s");
    var digits = new RegExp("\\d");
    if ($scope.password1.length < 8 || $scope.password1.length > 16 || !digits.test($scope.password1) || reWhiteSpace.test($scope.password1)) {
      $scope.error = "Password is invalid. Password must be between 8 to 16 characters, should not contain spaces and contain at least 1 number.";
      return;
    }
    if ($scope.password1 != $scope.password2) {
      $scope.error = "Passwords do not match";
      return;
    }
    UpdateUserPassword.update({token:$route.current.params.token}, $scope.password1, function (data) {
      $location.path('/reset/password/complete');
    },function (data) {});
  }
}

function ValidateTokenController() {
}

function ResetCompleteController($scope) {

  $scope.goToLoginPage = function() {
    window.location = 'login.html'
  }

}

ValidateTokenController.resolve = {

  userId:function ($q, $timeout, ValidatePasswordToken, $route, $location) {
    var deferred = $q.defer();
    $timeout(function () {
      ValidatePasswordToken.get({token:$route.current.params.token }, function (data) {
        $location.path('/reset/' + $route.current.params.token);
      }, function (data) {
        window.location = 'access-denied.html';
      });
    }, 100);
    return deferred.promise;
  }

}