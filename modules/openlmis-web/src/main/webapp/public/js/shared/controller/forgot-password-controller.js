function ForgotPasswordController($scope, ForgotPassword, $location) {

  $scope.user = {};

    $scope.sendForgotPasswordEmail = function(){
    if (!$scope.user.userName && !$scope.user.email) {
           $scope.error="Please enter either your Email or Username";
    } else {
      ForgotPassword.save({}, $scope.user, function () {
        window.location = "email-sent.html";
      }, function (data) {
        $scope.error = data.data.error;
      });
      }
    }


}
