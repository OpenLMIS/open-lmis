function ForgotPasswordController($scope, ForgotPassword, $location) {

    $scope.sendForgotPasswordEmail = function(){

    if (!$scope.user.username && !$scope.user.email) {
           $scope.error="Username not entered";
    } else {
      ForgotPassword.save({}, $scope.user, function () {
        $location.path('email-sent.html');
      }, {})
      }
    }


}
