function ForgotPasswordController($scope, ForgotPassword) {

  $scope.user = {};
  $scope.submitButtonLabel = "Submit";
  $scope.submitDisabled = false;
    $scope.sendForgotPasswordEmail = function(){
    if (!$scope.user.userName && !$scope.user.email) {
           $scope.error="Please enter either your Email or Username";
    } else {
      $scope.submitButtonLabel = "Sending...";
      $scope.submitDisabled = true;
      ForgotPassword.save({}, $scope.user, function () {
        window.location = "email-sent.html";
      }, function (data) {
        $scope.submitDisabled = false;
        $scope.submitButtonLabel = "Submit";
        $scope.error = data.data.error;
      });
      }
    }

}
