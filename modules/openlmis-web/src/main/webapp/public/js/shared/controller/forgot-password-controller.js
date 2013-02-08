function ForgotPasswordController($scope, ForgotPassword, messageService) {

  $scope.user = {};
  $scope.submitButtonLabel = "password.forgot.submit.button";
  $scope.submitDisabled = false;
    $scope.sendForgotPasswordEmail = function(){
    if (!$scope.user.userName && !$scope.user.email) {
           $scope.error="assword.forgot.enter.email.userName";
    } else {
      $scope.submitButtonLabel = "password.forgot.sending.email";
      $scope.submitDisabled = true;
      ForgotPassword.save({}, $scope.user, function () {
        window.location = "email-sent.html";
      }, function (data) {
        $scope.submitDisabled = false;
        $scope.submitButtonLabel = "password.forgot.submit.button";
        $scope.error = data.data.error;
      });
      }
    }

}
